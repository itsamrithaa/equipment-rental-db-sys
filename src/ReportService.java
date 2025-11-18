import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Scanner;

public class ReportService {
    private final Connection conn;
    private final Scanner scanner;

    public ReportService(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    public void menu() {
        int choice;
        do {
            System.out.println("\n--- Useful Reports ---");
            System.out.println("1. Renting checkouts for a member");
            System.out.println("2. Most popular item");
            System.out.println("3. Most popular manufacturer");
            System.out.println("4. Most used drone");
            System.out.println("5. Member with most items checked out");
            System.out.println("6. Equipment by type before a year");
            System.out.println("7. Return to Main Menu");
            choice = readInt("Enter choice: ");

            switch (choice) {
                case 1 -> showRentingCheckouts();
                case 2 -> showPopularItem();
                case 3 -> showPopularManufacturer();
                case 4 -> showPopularDrone();
                case 5 -> showTopMemberByCheckouts();
                case 6 -> showEquipmentByTypeBeforeYear();
                case 7 -> System.out.println("Returning to main menu...");
                default -> System.out.println("Invalid option.");
            }
        } while (choice != 7);
    }

    private void showRentingCheckouts() {
        System.out.println("\n--- Renting Checkouts ---");
        int userId = readInt("Enter the member User ID: ");

        String sql = "SELECT c.UserID, c.First_name, c.Last_name, " +
                "       COUNT(r.RentalID) AS TotalItems " +
                "FROM CUSTOMER c " +
                "LEFT JOIN RENTAL r ON c.UserID = r.UserID " +
                "WHERE c.UserID = ? " +
                "GROUP BY c.UserID, c.First_name, c.Last_name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            printResult(ps);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void showPopularItem() {
        System.out.println("\n--- Most Popular Item ---");
        String sql = "SELECT e.Serial_num AS SerialNumber, " +
                "       em.Description, " +
                "       COUNT(r.RentalID) AS TimesRented, " +
                "       ROUND(COALESCE(SUM(julianday(COALESCE(r.Return_date, DATE('now'))) - " +
                "            julianday(r.Checkout_date)), 0), 2) AS TotalRentalDays " +
                "FROM RENTAL r " +
                "JOIN EQUIPMENT e ON r.Serial_num = e.Serial_num " +
                "JOIN EQUIPMENT_MODEL em ON e.Model_num = em.Model_num " +
                "GROUP BY e.Serial_num, em.Description " +
                "ORDER BY TimesRented DESC, TotalRentalDays DESC " +
                "LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            printResult(ps);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void showPopularManufacturer() {
        System.out.println("\n--- Most Popular Manufacturer ---");
        String sql = "SELECT em.Manufacturer, " +
                "       COUNT(r.RentalID) AS UnitsRented " +
                "FROM RENTAL r " +
                "JOIN EQUIPMENT e ON r.Serial_num = e.Serial_num " +
                "JOIN EQUIPMENT_MODEL em ON e.Model_num = em.Model_num " +
                "GROUP BY em.Manufacturer " +
                "ORDER BY UnitsRented DESC " +
                "LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            printResult(ps);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void showPopularDrone() {
        System.out.println("\n--- Most Used Drone ---");
        String sql = "SELECT d.Serial_num AS DroneSerial, " +
                "       d.Name, " +
                "       COUNT(r.RentalID) AS Deliveries, " +
                "       ROUND(COALESCE(SUM(c.Distance_to_warehouse), 0), 2) AS TotalMiles " +
                "FROM RENTAL r " +
                "JOIN EQUIPMENT e ON r.Serial_num = e.Serial_num " +
                "JOIN DRONE d ON e.Drone_snum = d.Serial_num " +
                "JOIN CUSTOMER c ON r.UserID = c.UserID " +
                "GROUP BY d.Serial_num, d.Name " +
                "ORDER BY TotalMiles DESC, Deliveries DESC " +
                "LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            printResult(ps);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void showTopMemberByCheckouts() {
        System.out.println("\n--- Items Checked Out ---");
        String sql = "SELECT c.UserID, c.First_name, c.Last_name, " +
                "       COUNT(r.RentalID) AS ItemsCheckedOut " +
                "FROM RENTAL r " +
                "JOIN CUSTOMER c ON c.UserID = r.UserID " +
                "GROUP BY c.UserID, c.First_name, c.Last_name " +
                "ORDER BY ItemsCheckedOut DESC " +
                "LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            printResult(ps);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void showEquipmentByTypeBeforeYear() {
        System.out.println("\n--- Equipment by Type ---");
        String type = prompt("Enter the equipment type: ");
        int year = readInt("Enter the latest release year to include: ");

        String sql = "SELECT Model_num, Description, Type, Year " +
                "FROM EQUIPMENT_MODEL " +
                "WHERE Type = ? AND Year < ? " +
                "ORDER BY Year DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setInt(2, year);
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
