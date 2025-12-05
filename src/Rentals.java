import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Rentals {
    private static final String SQL_INSERT_RENTAL = "INSERT INTO RENTAL " +
            "(RentalID, Due_date, Checkout_date, Return_date, Fee, UserID, Serial_num) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_NEXT_RENTAL_ID = "SELECT COALESCE(MAX(RentalID), 0) + 1 FROM RENTAL";
    private static final String SQL_VALIDATE_USER = "SELECT 1 FROM CUSTOMER WHERE UserID = ?";
    private static final String SQL_EQUIPMENT_STATUS = "SELECT Status FROM EQUIPMENT WHERE Serial_num = ?";
    private static final String SQL_ACTIVE_RENTAL_FOR_EQUIPMENT = "SELECT RentalID FROM RENTAL WHERE Serial_num = ? AND Return_date IS NULL";
    private static final String SQL_ACTIVE_RENTAL_FOR_USER_EQUIPMENT = "SELECT RentalID FROM RENTAL WHERE Serial_num = ? AND UserID = ? AND Return_date IS NULL";
    private static final String SQL_UPDATE_EQUIPMENT = "UPDATE EQUIPMENT SET Status = ?, Location = ?, Drone_snum = COALESCE(?, Drone_snum) WHERE Serial_num = ?";
    private static final String SQL_VALIDATE_DRONE = "SELECT Status FROM DRONE WHERE Serial_num = ?";
    private static final String SQL_UPDATE_DRONE_LOCATION = "UPDATE DRONE SET Location = ? WHERE Serial_num = ?";

    private final Connection conn;
    private final Scanner scanner;

    public Rentals(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    public void handleRentEquipment() {
        System.out.println("\n--- Rent Equipment ---");
        int userId = readInt("Customer User ID: ");
        String equipmentSerial = prompt("Equipment Serial Number: ");
        String checkoutDate = prompt("Checkout Date (YYYY-MM-DD): ");
        String dueDate = prompt("Due Date (YYYY-MM-DD): ");
        double expectedFee = readDouble("Expected Fee (numbers only, blank for 0): ", 0.0);

        if (!userExists(userId)) {
            System.out.println("User does not exist. Please add the customer first.");
            return;
        }

        String equipmentStatus = getEquipmentStatus(equipmentSerial);
        if (equipmentStatus == null) {
            System.out.println("Equipment not found.");
            return;
        }

        if (hasActiveRental(equipmentSerial)) {
            System.out.println("Equipment already has an active rental. Complete the return first.");
            return;
        }

        if (!equipmentStatus.equalsIgnoreCase("Available")) {
            System.out.println("Equipment is not available (current status: " + equipmentStatus + ").");
            return;
        }

        Integer rentalId = nextRentalId();
        if (rentalId == null) {
            System.out.println("Could not generate a new Rental ID.");
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT_RENTAL)) {
            ps.setInt(1, rentalId);
            ps.setString(2, dueDate);
            ps.setString(3, checkoutDate);
            ps.setNull(4, Types.DATE); // open rental
            ps.setDouble(5, expectedFee);
            ps.setInt(6, userId);
            ps.setString(7, equipmentSerial);
            ps.executeUpdate();
            updateEquipment(equipmentSerial, "CheckedOut", "Customer", null);
            System.out.printf("Rental %d recorded for user %d, equipment %s from %s to %s.%n",
                    rentalId, userId, equipmentSerial, checkoutDate, dueDate);
        } catch (SQLException e) {
            System.out.println("Database error while creating rental: " + e.getMessage());
        }
    }

    public void scheduleDelivery() {
        System.out.println("\n--- Schedule Delivery ---");
        int userId = readInt("Customer User ID: ");
        String equipmentSerial = prompt("Equipment Serial Number: ");
        String droneSerial = prompt("Assign Drone Serial Number: ");
        String deliveryDate = prompt("Delivery Date (YYYY-MM-DD): ");
        String window = prompt("Preferred delivery window (e.g., 10AM-12PM): ");

        Integer rentalId = activeRentalForUserEquipment(userId, equipmentSerial);
        if (rentalId == null) {
            System.out.println("No active rental found for that user/equipment. Create a rental first.");
            return;
        }

        if (!droneIsUsable(droneSerial)) {
            System.out.println("Drone not found or unavailable.");
            return;
        }

        try {
            updateEquipment(equipmentSerial, "CheckedOut", "Transit", droneSerial);
            updateDroneLocation(droneSerial, "In-Flight");
            System.out.printf("Delivery scheduled for rental %d. Drone %s will deliver equipment %s to user %d on %s during %s.%n",
                    rentalId, droneSerial, equipmentSerial, userId, deliveryDate, window);
        } catch (SQLException e) {
            System.out.println("Database error while scheduling delivery: " + e.getMessage());
        }
    }

    public void schedulePickup() {
        System.out.println("\n--- Schedule Pickup ---");
        int userId = readInt("Customer User ID: ");
        String equipmentSerial = prompt("Equipment Serial Number: ");
        String droneSerial = prompt("Assign Drone Serial Number for pickup: ");
        String pickupDate = prompt("Pickup Date (YYYY-MM-DD): ");
        String window = prompt("Preferred pickup window: ");

        Integer rentalId = activeRentalForUserEquipment(userId, equipmentSerial);
        if (rentalId == null) {
            System.out.println("No active rental found for that user/equipment. Nothing to pick up.");
            return;
        }

        if (!droneIsUsable(droneSerial)) {
            System.out.println("Drone not found or unavailable.");
            return;
        }

        try {
            updateEquipment(equipmentSerial, "CheckedOut", "Transit", droneSerial);
            updateDroneLocation(droneSerial, "In-Flight");
            System.out.printf("Pickup scheduled for rental %d. Drone %s will pick up equipment %s from user %d on %s during %s.%n",
                    rentalId, droneSerial, equipmentSerial, userId, pickupDate, window);
        } catch (SQLException e) {
            System.out.println("Database error while scheduling pickup: " + e.getMessage());
        }
    }

    private void updateEquipment(String serial, String status, String location, String droneSerial) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_EQUIPMENT)) {
            ps.setString(1, status);
            ps.setString(2, location);
            ps.setString(3, droneSerial);
            ps.setString(4, serial);
            ps.executeUpdate();
        }
    }

    private void updateDroneLocation(String droneSerial, String location) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_DRONE_LOCATION)) {
            ps.setString(1, location);
            ps.setString(2, droneSerial);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Could not update drone location: " + e.getMessage());
        }
    }

    private boolean userExists(int userId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_VALIDATE_USER)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Database error validating user: " + e.getMessage());
            return false;
        }
    }

    private String getEquipmentStatus(String serial) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_EQUIPMENT_STATUS)) {
            ps.setString(1, serial);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Status");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error looking up equipment: " + e.getMessage());
        }
        return null;
    }

    private boolean hasActiveRental(String serial) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_ACTIVE_RENTAL_FOR_EQUIPMENT)) {
            ps.setString(1, serial);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Database error checking active rental: " + e.getMessage());
            return true;
        }
    }

    private Integer activeRentalForUserEquipment(int userId, String serial) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_ACTIVE_RENTAL_FOR_USER_EQUIPMENT)) {
            ps.setString(1, serial);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("RentalID");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error finding active rental: " + e.getMessage());
        }
        return null;
    }

    private boolean droneIsUsable(String droneSerial) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_VALIDATE_DRONE)) {
            ps.setString(1, droneSerial);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("Status");
                    return !"Decommissioned".equalsIgnoreCase(status);
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error validating drone: " + e.getMessage());
        }
        return false;
    }

    private Integer nextRentalId() {
        try (PreparedStatement ps = conn.prepareStatement(SQL_NEXT_RENTAL_ID);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Database error generating rental id: " + e.getMessage());
        }
        return null;
    }

    private String prompt(String label) {
        System.out.print(label);
        return scanner.nextLine().trim();
    }

    private int readInt(String label) {
        while (true) {
            System.out.print(label);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private double readDouble(String label, double defaultValue) {
        while (true) {
            System.out.print(label);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return defaultValue;
            }
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid decimal number.");
            }
        }
    }
}
