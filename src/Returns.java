import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Returns {
    private static final String SQL_ACTIVE_RENTAL = "SELECT RentalID, UserID FROM RENTAL WHERE Serial_num = ? AND Return_date IS NULL ORDER BY Checkout_date DESC LIMIT 1";
    private static final String SQL_UPDATE_RENTAL = "UPDATE RENTAL SET Return_date = ?, Fee = COALESCE(?, Fee) WHERE RentalID = ?";
    private static final String SQL_UPDATE_EQUIPMENT = "UPDATE EQUIPMENT SET Status = ?, Location = ? WHERE Serial_num = ?";
    private static final String SQL_DRONE_FOR_EQUIPMENT = "SELECT Drone_snum FROM EQUIPMENT WHERE Serial_num = ?";
    private static final String SQL_UPDATE_DRONE_LOCATION = "UPDATE DRONE SET Location = ? WHERE Serial_num = ?";

    private final Connection conn;
    private final Scanner scanner;

    public Returns(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    public void handleReturnEquipment() {
        System.out.println("\n--- Return Equipment ---");
        int userId = readInt("Customer User ID: ");
        String equipmentSerial = prompt("Equipment Serial Number: ");
        String condition = prompt("Condition upon return (e.g., Good, Damaged): ");
        String returnDateInput = prompt("Return Date (YYYY-MM-DD, blank for today): ");
        double finalFee = readDouble("Final Fee (blank to keep expected fee): ", Double.NaN);

        ActiveRental rental = findActiveRental(equipmentSerial);
        if (rental == null) {
            System.out.println("No open rental found for that equipment.");
            return;
        }
        if (rental.userId != userId) {
            System.out.println("That rental belongs to a different user (Rental UserID: " + rental.userId + ").");
            return;
        }

        String returnDate = returnDateInput.isEmpty() ? LocalDate.now().toString() : returnDateInput;
        String newStatus = condition.toLowerCase().contains("damag") ? "Repair" : "Available";

        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_RENTAL)) {
            ps.setString(1, returnDate);
            if (Double.isNaN(finalFee)) {
                ps.setNull(2, Types.DECIMAL);
            } else {
                ps.setDouble(2, finalFee);
            }
            ps.setInt(3, rental.rentalId);
            ps.executeUpdate();

            updateEquipment(equipmentSerial, newStatus, "Warehouse");
            moveAssignedDroneHome(equipmentSerial);
            System.out.printf("Return registered for rental %d on %s. Condition: %s. Status set to %s.%n",
                    rental.rentalId, returnDate, condition, newStatus);
        } catch (SQLException e) {
            System.out.println("Database error while completing return: " + e.getMessage());
        }
    }

    private void updateEquipment(String serial, String status, String location) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_EQUIPMENT)) {
            ps.setString(1, status);
            ps.setString(2, location);
            ps.setString(3, serial);
            ps.executeUpdate();
        }
    }

    private void moveAssignedDroneHome(String equipmentSerial) {
        String droneSerial = null;
        try (PreparedStatement lookup = conn.prepareStatement(SQL_DRONE_FOR_EQUIPMENT)) {
            lookup.setString(1, equipmentSerial);
            try (ResultSet rs = lookup.executeQuery()) {
                if (rs.next()) {
                    droneSerial = rs.getString("Drone_snum");
                }
            }
        } catch (SQLException e) {
            System.out.println("Could not look up assigned drone: " + e.getMessage());
        }

        if (droneSerial == null || droneSerial.isBlank()) {
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_DRONE_LOCATION)) {
            ps.setString(1, "Warehouse");
            ps.setString(2, droneSerial);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Could not update drone location: " + e.getMessage());
        }
    }

    private ActiveRental findActiveRental(String equipmentSerial) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_ACTIVE_RENTAL)) {
            ps.setString(1, equipmentSerial);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ActiveRental(rs.getInt("RentalID"), rs.getInt("UserID"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error finding active rental: " + e.getMessage());
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

    private record ActiveRental(int rentalId, int userId) {}
}
