import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Returns {
    private final Scanner scanner;

    public Returns(Scanner scanner) {
        this.scanner = scanner;
    }

    public void handleReturnEquipment() {
        System.out.println("\n--- Return Equipment ---");
        String userIdStr = prompt("Customer User ID: ");
        String equipmentSerial = prompt("Equipment Serial Number: ");
        String condition = prompt("Condition upon return: ");
        String returnDate = prompt("Return Date (YYYY-MM-DD): ");
        String notes = prompt("Any issues to report: ");

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

            // Find the open rental for this user and equipment
            Integer rentalId = null;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT RentalID FROM RENTAL WHERE UserID = ? AND Serial_num = ? AND Return_date IS NULL ORDER BY Checkout_date DESC LIMIT 1")) {
                ps.setInt(1, userId);
                ps.setString(2, equipmentSerial);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        rentalId = rs.getInt("RentalID");
                    }
                }
            }

            if (rentalId == null) {
                System.out.println("No active rental found for user " + userId + " and equipment " + equipmentSerial);
                conn.rollback();
                return;
            }

            // Update rental with return date (and optionally fee)
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE RENTAL SET Return_date = ? WHERE RentalID = ?")) {
                ps.setString(1, returnDate);
                ps.setInt(2, rentalId);
                ps.executeUpdate();
            }

            // Update equipment to available and reset drone assignment/location
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE EQUIPMENT SET Status = ?, Location = ?, Drone_snum = NULL WHERE Serial_num = ?")) {
                ps.setString(1, "available");
                ps.setString(2, "warehouse");
                ps.setString(3, equipmentSerial);
                ps.executeUpdate();
            }

            // Optionally, if condition indicates maintenance needed, insert a maintenance log (simple heuristic)
            if (condition != null && !condition.trim().isEmpty() && !condition.equalsIgnoreCase("good") && !condition.equalsIgnoreCase("ok") && !condition.equalsIgnoreCase("okay")) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO MAINTENANCE_LOG (Maintenance_ID, Date, Cost, Description, Serial_num) VALUES ((SELECT COALESCE(MAX(Maintenance_ID),0)+1 FROM MAINTENANCE_LOG), ?, ?, ?, ?)") ) {
                    ps.setString(1, returnDate);
                    ps.setNull(2, java.sql.Types.DECIMAL);
                    ps.setString(3, "Returned in condition: " + condition + ". Notes: " + (notes.isEmpty() ? "None" : notes));
                    ps.setString(4, equipmentSerial);
                    ps.executeUpdate();
                } catch (SQLException ex) {
                    // If maintenance table doesn't exist or insertion fails, just log and continue
                    System.out.println("Could not log maintenance: " + ex.getMessage());
                }
            }

            conn.commit();

            System.out.printf("Return recorded for user %d and equipment %s on %s. Condition: %s. Notes: %s%n",
                    userId, equipmentSerial, returnDate, condition, notes.isEmpty() ? "None" : notes);
        } catch (SQLException e) {
            System.out.println("Error during return processing: " + e.getMessage());
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
