import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class CustomerManager {
    private final Connection conn;
    private final Scanner scanner;

    private static final String BASE_SELECT =
            "SELECT UserID, First_name, Last_name, Address, Phone, Email, " +
            "       Start_date, Distance_to_warehouse, Nearest_warehouse " +
            "FROM CUSTOMER";

    public CustomerManager(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    public void menu() {
        int choice;
        do {
            System.out.println("\n--- Manage Customers ---");
            System.out.println("1. Add Customer");
            System.out.println("2. Edit Customer");
            System.out.println("3. Delete Customer");
            System.out.println("4. Search Customer by User ID");
            System.out.println("5. List All Customers");
            System.out.println("6. Return to Main Menu");
            choice = readInt("Enter choice: ");

            switch (choice) {
                case 1 -> addCustomer();
                case 2 -> editCustomer();
                case 3 -> deleteCustomer();
                case 4 -> searchCustomer();
                case 5 -> listAll();
                case 6 -> System.out.println("Returning to main menu...");
                default -> System.out.println("Invalid option.");
            }
        } while (choice != 6);
    }

    private void addCustomer() {
        System.out.println("\n--- Add Customer ---");
        int userId = readInt("User ID: ");

        if (customerExists(userId)) {
            System.out.println("User ID already exists.");
            return;
        }

        String first = prompt("First Name: ");
        String last = prompt("Last Name: ");
        String address = prompt("Address: ");
        String phone = prompt("Phone: ");
        String email = prompt("Email: ");
        String startDate = prompt("Start Date (YYYY-MM-DD): ");
        double distance = readDouble("Warehouse Distance (miles): ", null);
        String nearest = prompt("Nearest Warehouse Address: ");

        String sql = "INSERT INTO CUSTOMER " +
                "(UserID, First_name, Last_name, Address, Phone, Email, " +
                " Distance_to_warehouse, Nearest_warehouse, Start_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, first);
            ps.setString(3, last);
            ps.setString(4, address);
            ps.setString(5, phone);
            ps.setString(6, email);
            ps.setDouble(7, distance);
            ps.setString(8, nearest);
            ps.setString(9, startDate);
            ps.executeUpdate();
            System.out.println("Customer added successfully!");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void editCustomer() {
        System.out.println("\n--- Edit Customer ---");
        int userId = readInt("Enter User ID to edit: ");

        Customer existing = findCustomer(userId);
        if (existing == null) {
            System.out.println("Customer not found.");
            return;
        }

        System.out.println("Editing Customer " + existing.getUserId() + ". Press Enter to keep each value.");

        String first = optionalPrompt("Current First Name (" + existing.getFirstName() + "): ", existing.getFirstName());
        String last = optionalPrompt("Current Last Name (" + existing.getLastName() + "): ", existing.getLastName());
        String address = optionalPrompt("Current Address (" + existing.getAddress() + "): ", existing.getAddress());
        String phone = optionalPrompt("Current Phone (" + existing.getPhone() + "): ", existing.getPhone());
        String email = optionalPrompt("Current Email (" + existing.getEmail() + "): ", existing.getEmail());
        String start = optionalPrompt("Current Start Date (" + existing.getStartDate() + "): ", existing.getStartDate());
        double distance = readDouble("Current Warehouse Distance (" + existing.getWarehouseDistance() + "): ", existing.getWarehouseDistance());
        String nearest = optionalPrompt("Current Nearest Warehouse (" + existing.getNearestWarehouse() + "): ", existing.getNearestWarehouse());

        String sql = "UPDATE CUSTOMER " +
                "SET First_name = ?, Last_name = ?, Address = ?, Phone = ?, Email = ?, " +
                "    Start_date = ?, Distance_to_warehouse = ?, Nearest_warehouse = ? " +
                "WHERE UserID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, first);
            ps.setString(2, last);
            ps.setString(3, address);
            ps.setString(4, phone);
            ps.setString(5, email);
            ps.setString(6, start);
            ps.setDouble(7, distance);
            ps.setString(8, nearest);
            ps.setInt(9, userId);
            int updated = ps.executeUpdate();
            if (updated > 0) {
                System.out.println("Customer details updated successfully!");
            } else {
                System.out.println("No changes applied.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void deleteCustomer() {
        System.out.println("\n--- Delete Customer ---");
        int userId = readInt("Enter User ID to delete: ");

        String sql = "DELETE FROM CUSTOMER WHERE UserID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            int removed = ps.executeUpdate();
            System.out.println(removed > 0 ? "Customer deleted." : "Customer not found.");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void searchCustomer() {
        System.out.println("\n--- Search Customer ---");
        int userId = readInt("Enter User ID to search: ");

        Customer customer = findCustomer(userId);
        if (customer == null) {
            System.out.println("Customer not found.");
        } else {
            System.out.println(customer);
        }
    }

    public void listAll() {
        System.out.println("\n--- All Customers ---");
        String sql = BASE_SELECT + " ORDER BY UserID";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            boolean found = false;
            while (rs.next()) {
                found = true;
                Customer c = mapCustomer(rs);
                System.out.println(c);
            }
            if (!found) {
                System.out.println("No customers found.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private boolean customerExists(int userId) {
        String sql = "SELECT 1 FROM CUSTOMER WHERE UserID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }

    private Customer findCustomer(int userId) {
        String sql = BASE_SELECT + " WHERE UserID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCustomer(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return null;
    }

    private Customer mapCustomer(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("UserID"),
                rs.getString("First_name"),
                rs.getString("Last_name"),
                rs.getString("Address"),
                rs.getString("Phone"),
                rs.getString("Email"),
                rs.getString("Start_date"),
                rs.getDouble("Distance_to_warehouse"),
                rs.getString("Nearest_warehouse")
        );
    }

    private String prompt(String label) {
        System.out.print(label);
        return scanner.nextLine().trim();
    }

    private String optionalPrompt(String label, String currentValue) {
        System.out.print(label);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? currentValue : input;
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

    private double readDouble(String label, Double currentValue) {
        while (true) {
            System.out.print(label);
            String input = scanner.nextLine().trim();
            if (input.isEmpty() && currentValue != null) {
                return currentValue;
            }
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid decimal number.");
            }
        }
    }
}
