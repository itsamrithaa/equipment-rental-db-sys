import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Scanner;

public class ReviewService {
    private final Connection conn;
    private final Scanner scanner;

    public ReviewService(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    public void menu() {
        int choice;
        do {
            System.out.println("\n--- View Reviews ---");
            System.out.println("1. List all reviews");
            System.out.println("2. List reviews by user");
            System.out.println("3. List reviews by equipment serial number");
            System.out.println("4. Return to Main Menu");
            choice = readInt("Enter choice: ");

            switch (choice) {
                case 1 -> listAllReviews();
                case 2 -> listReviewsByUser();
                case 3 -> listReviewsByEquipment();
                case 4 -> System.out.println("Returning to main menu...");
                default -> System.out.println("Invalid option.");
            }
        } while (choice != 4);
    }

    private void listAllReviews() {
        System.out.println("\n--- All Reviews ---");
        String sql = "SELECT Review_ID, Date, Comments, Rating, Equipment_serial_num, UserID " +
                "FROM REVIEW " +
                "ORDER BY Date DESC, Review_ID DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            printResult(ps);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void listReviewsByUser() {
        System.out.println("\n--- Reviews by User ---");
        int userId = readInt("Enter User ID: ");
        String sql = "SELECT Review_ID, Date, Comments, Rating, Equipment_serial_num, UserID " +
                "FROM REVIEW WHERE UserID = ? " +
                "ORDER BY Date DESC, Review_ID DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            printResult(ps);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void listReviewsByEquipment() {
        System.out.println("\n--- Reviews by Equipment ---");
        String serial = prompt("Enter Equipment Serial Number: ");
        String sql = "SELECT Review_ID, Date, Comments, Rating, Equipment_serial_num, UserID " +
                "FROM REVIEW WHERE Equipment_serial_num = ? " +
                "ORDER BY Date DESC, Review_ID DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, serial);
            printResult(ps);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void printResult(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            boolean found = false;
            while (rs.next()) {
                if (!found) {
                    found = true;
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.print(meta.getColumnLabel(i));
                        if (i < columnCount) {
                            System.out.print(" | ");
                        }
                    }
                    System.out.println();
                }
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i));
                    if (i < columnCount) {
                        System.out.print(" | ");
                    }
                }
                System.out.println();
            }
            if (!found) {
                System.out.println("No matching records found.");
            }
        }
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

    private String prompt(String label) {
        System.out.print(label);
        return scanner.nextLine().trim();
    }
}
