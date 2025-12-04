import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Scanner;

public class Transactions {
    private final Connection conn;
    private final Scanner scanner;

    public Transactions(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    public void menu() {
        int choice;
        do {
            System.out.println("\n--- View Transactions ---");
            System.out.println("1. List all transactions");
            System.out.println("2. List transactions by user");
            System.out.println("3. List transactions by status");
            System.out.println("4. Return to Main Menu");
            choice = readInt("Enter choice: ");

            switch (choice) {
                case 1 -> listAllTransactions();
                case 2 -> listTransactionsByUser();
                case 3 -> listTransactionsByStatus();
                case 4 -> System.out.println("Returning to main menu...");
                default -> System.out.println("Invalid option.");
            }
        } while (choice != 4);
    }

    private void listAllTransactions() {
        System.out.println("\n--- All Transactions ---");
        String sql = "SELECT TransactionID, Amount, Currency, Payment_method, Date, Status, RentalID, UserID " +
                "FROM TRANSACTIONS " +
                "ORDER BY Date DESC, TransactionID DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            printResult(ps);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void listTransactionsByUser() {
        System.out.println("\n--- Transactions by User ---");
        int userId = readInt("Enter User ID: ");
        String sql = "SELECT TransactionID, Amount, Currency, Payment_method, Date, Status, RentalID, UserID " +
                "FROM TRANSACTIONS WHERE UserID = ? " +
                "ORDER BY Date DESC, TransactionID DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            printResult(ps);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void listTransactionsByStatus() {
        System.out.println("\n--- Transactions by Status ---");
        String status = prompt("Enter Status (e.g., Pending, Completed, Refunded): ");
        String sql = "SELECT TransactionID, Amount, Currency, Payment_method, Date, Status, RentalID, UserID " +
                "FROM TRANSACTIONS WHERE Status = ? " +
                "ORDER BY Date DESC, TransactionID DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
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
