import java.util.ArrayList;
import java.util.Scanner;

public class DroneManager {
    private ArrayList<Drone> drones = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    public void menu() {
        int choice;
        do {
            System.out.println("\n--- Manage Drones ---");
            System.out.println("1. Add Drone");
            System.out.println("2. Edit Drone");
            System.out.println("3. Delete Drone");
            System.out.println("4. Search Drone by Serial Number");
            System.out.println("5. Return to Main Menu");
            System.out.print("Enter choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addDrone();
                case 2 -> editDrone();
                case 3 -> deleteDrone();
                case 4 -> searchDrone();
                case 5 -> System.out.println("Returning to main menu...");
                default -> System.out.println("Invalid option.");
            }
        } while (choice != 5);
    }

    private void addDrone() {
        System.out.print("Serial Number: ");
        String serial = scanner.nextLine();
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Model: ");
        String model = scanner.nextLine();
        System.out.print("Status: ");
        String status = scanner.nextLine();
        System.out.print("Location: ");
        String location = scanner.nextLine();
        System.out.print("Warranty Expiration (YYYY-MM-DD): ");
        String warranty = scanner.nextLine();
        System.out.print("Warehouse Address: ");
        String warehouse = scanner.nextLine();

        drones.add(new Drone(serial, name, model, status, location, warranty, warehouse));
        System.out.println("Drone added successfully!");
    }

    private void editDrone() {
        System.out.print("Enter Drone Serial Number to edit: ");
        String serial = scanner.nextLine();

        for (Drone d : drones) {
            if (d.getSerialNum().equals(serial)) {
                System.out.println("Editing Drone: " + d.getSerialNum());
                System.out.println("(Press Enter to keep the current value)");

                System.out.print("Current Name (" + d.getName() + "): ");
                String name = scanner.nextLine();
                if (!name.isEmpty()) d.setName(name);

                System.out.print("Current Model (" + d.getModel() + "): ");
                String model = scanner.nextLine();
                if (!model.isEmpty()) d.setModel(model);

                System.out.print("Current Status (" + d.getStatus() + "): ");
                String status = scanner.nextLine();
                if (!status.isEmpty()) d.setStatus(status);

                System.out.print("Current Location (" + d.getLocation() + "): ");
                String location = scanner.nextLine();
                if (!location.isEmpty()) d.setLocation(location);

                System.out.print("Current Warranty Expiration (" + d.getWarrantyExp() + "): ");
                String warranty = scanner.nextLine();
                if (!warranty.isEmpty()) d.setWarrantyExp(warranty);

                System.out.print("Current Warehouse Address (" + d.getWarehouseAddress() + "): ");
                String warehouse = scanner.nextLine();
                if (!warehouse.isEmpty()) d.setWarehouseAddress(warehouse);

                System.out.println("Drone details updated successfully!");
                return;
            }
        }

        System.out.println("Drone not found.");
    }


    private void deleteDrone() {
        System.out.print("Enter Serial Number to delete: ");
        String serial = scanner.nextLine();
        drones.removeIf(d -> d.getSerialNum().equals(serial));
        System.out.println("If drone existed, it was deleted.");
    }

    private void searchDrone() {
        System.out.print("Enter Serial Number to search: ");
        String serial = scanner.nextLine();
        for (Drone d : drones) {
            if (d.getSerialNum().equals(serial)) {
                System.out.println(d.toString());
                return;
            }
        }
        System.out.println("Drone not found.");
    }
}
