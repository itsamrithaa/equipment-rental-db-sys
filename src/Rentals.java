import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Rentals {
    private final Scanner scanner;

    public Rentals(Scanner scanner) {
        this.scanner = scanner;
    }

    public void handleRentEquipment() {
        System.out.println("\n--- Rent Equipment ---");
        String userIdStr = prompt("Customer User ID: ");
        String equipmentSerial = prompt("Equipment Serial Number: ");
        String checkoutDate = prompt("Checkout Date (YYYY-MM-DD): ");
        String dueDate = prompt("Due Date (YYYY-MM-DD): ");
        String notes = prompt("Any special instructions: ");

        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid user id. Aborting.");
            return;
        }

        Connection conn = DB.getConnection();
        if (conn == null) {
            System.out.println("Database connection not available.");
            return;
        }

        try {
            conn.setAutoCommit(false);

            // Verify equipment exists and is available
            try (PreparedStatement ps = conn.prepareStatement("SELECT Status FROM EQUIPMENT WHERE Serial_num = ?")) {
                ps.setString(1, equipmentSerial);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Equipment not found: " + equipmentSerial);
                        conn.rollback();
                        return;
                    }
                    String status = rs.getString("Status");
                    if (status != null && status.equalsIgnoreCase("rented")) {
                        System.out.println("Equipment is already rented: " + equipmentSerial);
                        conn.rollback();
                        return;
                    }
                }
            }

            // Generate new RentalID
            int rentalId = 1;
            try (PreparedStatement ps = conn.prepareStatement("SELECT COALESCE(MAX(RentalID),0)+1 AS nextId FROM RENTAL");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    rentalId = rs.getInt("nextId");
                }
            }

            // Insert rental record
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO RENTAL (RentalID, Due_date, Checkout_date, Return_date, Fee, UserID, Serial_num) VALUES (?,?,?,?,?,?,?)")) {
                ps.setInt(1, rentalId);
                ps.setString(2, dueDate);
                ps.setString(3, checkoutDate);
                ps.setNull(4, java.sql.Types.DATE);
                ps.setNull(5, java.sql.Types.DECIMAL);
                ps.setInt(6, userId);
                ps.setString(7, equipmentSerial);
                ps.executeUpdate();
            }

            // Update equipment status/location
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE EQUIPMENT SET Status = ?, Location = ? WHERE Serial_num = ?")) {
                ps.setString(1, "rented");
                ps.setString(2, "with user " + userId);
                ps.setString(3, equipmentSerial);
                ps.executeUpdate();
            }

            conn.commit();

            System.out.printf("Rental recorded (RentalID=%d) for user %d, equipment %s from %s to %s.%n",
                    rentalId, userId, equipmentSerial, checkoutDate, dueDate);
        } catch (SQLException e) {
            System.out.println("Error during rental: " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException ex) {
                // ignore
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    public void scheduleDelivery() {
        System.out.println("\n--- Schedule Delivery ---");
        String userIdStr = prompt("Customer User ID: ");
        String equipmentSerial = prompt("Equipment Serial Number: ");
        String droneSerial = prompt("Assign Drone Serial Number: ");
        String deliveryDate = prompt("Delivery Date (YYYY-MM-DD): ");
        String window = prompt("Preferred delivery window (e.g., 10AM-12PM): ");

        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid user id. Aborting.");
            return;
        }

        Connection conn = DB.getConnection();
        if (conn == null) {
            System.out.println("Database connection not available.");
            return;
        }

        try {
            conn.setAutoCommit(false);

            // Assign drone to equipment and mark equipment as in-transit
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE EQUIPMENT SET Drone_snum = ?, Status = ?, Location = ? WHERE Serial_num = ?")) {
                ps.setString(1, droneSerial);
                ps.setString(2, "in-transit");
                ps.setString(3, "out for delivery to user " + userId + " on " + deliveryDate + " (" + window + ")");
                ps.setString(4, equipmentSerial);
                int updated = ps.executeUpdate();
                if (updated == 0) {
                    System.out.println("Equipment not found: " + equipmentSerial);
                    conn.rollback();
                    return;
                }
            }

            // Optionally update drone status
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE DRONE SET Status = ?, Location = ? WHERE Serial_num = ?")) {
                ps.setString(1, "assigned");
                ps.setString(2, "assigned to deliver equipment " + equipmentSerial + " to user " + userId);
                ps.setString(3, droneSerial);
                ps.executeUpdate();
            }

            conn.commit();
            System.out.printf("Delivery scheduled: drone %s will deliver equipment %s to user %d on %s during %s.%n",
                    droneSerial, equipmentSerial, userId, deliveryDate, window);
        } catch (SQLException e) {
            System.out.println("Error scheduling delivery: " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException ex) {
                // ignore
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    public void schedulePickup() {
        System.out.println("\n--- Schedule Pickup ---");
        String userIdStr = prompt("Customer User ID: ");
        String equipmentSerial = prompt("Equipment Serial Number: ");
        String droneSerial = prompt("Assign Drone Serial Number for pickup: ");
        String pickupDate = prompt("Pickup Date (YYYY-MM-DD): ");
        String window = prompt("Preferred pickup window: ");

        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid user id. Aborting.");
            return;
        }

        Connection conn = DB.getConnection();
        if (conn == null) {
            System.out.println("Database connection not available.");
            return;
        }

        try {
            conn.setAutoCommit(false);

            // Mark equipment as awaiting pickup and assign drone
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE EQUIPMENT SET Status = ?, Drone_snum = ?, Location = ? WHERE Serial_num = ?")) {
                ps.setString(1, "awaiting-pickup");
                ps.setString(2, droneSerial);
                ps.setString(3, "awaiting pickup from user " + userId + " on " + pickupDate + " (" + window + ")");
                ps.setString(4, equipmentSerial);
                int updated = ps.executeUpdate();
                if (updated == 0) {
                    System.out.println("Equipment not found: " + equipmentSerial);
                    conn.rollback();
                    return;
                }
            }

            // Update drone status
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE DRONE SET Status = ?, Location = ? WHERE Serial_num = ?")) {
                ps.setString(1, "assigned");
                ps.setString(2, "assigned to pick up equipment " + equipmentSerial + " from user " + userId);
                ps.setString(3, droneSerial);
                ps.executeUpdate();
            }

            conn.commit();
            System.out.printf("Pickup scheduled: drone %s will pick up equipment %s from user %d on %s during %s.%n",
                    droneSerial, equipmentSerial, userId, pickupDate, window);
        } catch (SQLException e) {
            System.out.println("Error scheduling pickup: " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException ex) {
                // ignore
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    private String prompt(String label) {
        System.out.print(label);
        return scanner.nextLine().trim();
    }
}
