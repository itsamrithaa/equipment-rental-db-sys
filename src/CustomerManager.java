import java.util.ArrayList;
import java.util.Scanner;

public class CustomerManager {
    private ArrayList<Customer> customers = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    public void menu() {
        int choice;
        do {
            System.out.println("\n--- Manage Customers ---");
            System.out.println("1. Add Customer");
            System.out.println("2. Edit Customer");
            System.out.println("3. Delete Customer");
            System.out.println("4. Search Customer by User ID");
            System.out.println("5. Return to Main Menu");
            System.out.print("Enter choice: ");
            while (!scanner.hasNextInt()) { scanner.nextLine(); System.out.print("Enter choice: "); }
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addCustomer();
                case 2 -> editCustomer();
                case 3 -> deleteCustomer();
                case 4 -> searchCustomer();
                case 5 -> System.out.println("Returning to main menu...");
                default -> System.out.println("Invalid option.");
            }
        } while (choice != 5);
    }

    private void addCustomer() {
        System.out.print("User ID: ");
        String userId = scanner.nextLine();

        // ensure unique by userId 
        for (Customer c : customers) {
            if (c.getUserId().equals(userId)) {
                System.out.println("User ID already exists.");
                return;
            }
        }

        System.out.print("First Name: ");
        String first = scanner.nextLine();
        System.out.print("Last Name: ");
        String last = scanner.nextLine();
        System.out.print("Address: ");
        String address = scanner.nextLine();
        System.out.print("Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Start Date (YYYY-MM-DD): ");
        String startDate = scanner.nextLine();
        System.out.print("Warehouse Distance (number, e.g., 12.5): ");
        String distStr = scanner.nextLine();
        double warehouseDistance;
        try { warehouseDistance = Double.parseDouble(distStr); }
        catch (Exception e) { System.out.println("Invalid distance. Using 0."); warehouseDistance = 0.0; }

        customers.add(new Customer(userId, first, last, address, phone, email, startDate, warehouseDistance));
        System.out.println("Customer added successfully!");
    }

    private void editCustomer() {
        System.out.print("Enter User ID to edit: ");
        String userId = scanner.nextLine();

        for (Customer c : customers) {
            if (c.getUserId().equals(userId)) {
                System.out.println("Editing Customer: " + c.getUserId());
                System.out.println("(Press Enter to keep the current value)");

                System.out.print("Current First Name (" + c.getFirstName() + "): ");
                String first = scanner.nextLine();
                if (!first.isEmpty()) c.setFirstName(first);

                System.out.print("Current Last Name (" + c.getLastName() + "): ");
                String last = scanner.nextLine();
                if (!last.isEmpty()) c.setLastName(last);

                System.out.print("Current Address (" + c.getAddress() + "): ");
                String address = scanner.nextLine();
                if (!address.isEmpty()) c.setAddress(address);

                System.out.print("Current Phone (" + c.getPhone() + "): ");
                String phone = scanner.nextLine();
                if (!phone.isEmpty()) c.setPhone(phone);

                System.out.print("Current Email (" + c.getEmail() + "): ");
                String email = scanner.nextLine();
                if (!email.isEmpty()) c.setEmail(email);

                System.out.print("Current Start Date (" + c.getStartDate() + "): ");
                String start = scanner.nextLine();
                if (!start.isEmpty()) c.setStartDate(start);

                System.out.print("Current Warehouse Distance (" + c.getWarehouseDistance() + "): ");
                String distStr = scanner.nextLine();
                if (!distStr.isEmpty()) {
                    try { c.setWarehouseDistance(Double.parseDouble(distStr)); }
                    catch (Exception e) { System.out.println("Invalid distance. Keeping old value."); }
                }

                System.out.println("Customer details updated successfully!");
                return;
            }
        }
        System.out.println("Customer not found.");
    }

    private void deleteCustomer() {
        System.out.print("Enter User ID to delete: ");
        String userId = scanner.nextLine();
        boolean removed = customers.removeIf(c -> c.getUserId().equals(userId));
        System.out.println(removed ? "Deleted." : "Customer not found.");
    }

    private void searchCustomer() {
        System.out.print("Enter User ID to search: ");
        String userId = scanner.nextLine();
        for (Customer c : customers) {
            if (c.getUserId().equals(userId)) {
                System.out.println(c);
                return;
            }
        }
        System.out.println("Customer not found.");
    }

    public void listAll() {
        if (customers.isEmpty()) { System.out.println("No customers."); return; }
        for (Customer c : customers) System.out.println(c);
    }
}

