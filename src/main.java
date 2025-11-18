import java.util.Scanner;
import java.lang.String;
import java.lang.System;
import java.sql.Connection;

public class main {
    public static void main(String[] args) {
        Connection conn = DB.getConnection();
        Scanner scanner = new Scanner(System.in);
        DroneManager droneManager = new DroneManager(scanner);
        CustomerManager customerManager = new CustomerManager(conn, scanner);
        Rentals rentals = new Rentals(scanner);
        Returns returns = new Returns(scanner);
        ReportService reportService = new ReportService(conn, scanner);

        boolean stay = true;
        System.out.println("Welcome to the Database System for Group 10");
        while (stay) {
            System.out.println("\nPlease select a number option from the following menu:");
            System.out.println("1. Manage Customers");
            System.out.println("2. Manage Warehouses");
            System.out.println("3. Manage Drones");
            System.out.println("4. Manage Equipment");
            System.out.println("5. Manage Purchase Orders");
            System.out.println("6. Rent Equipment");
            System.out.println("7. Return Equipment");
            System.out.println("8. Delivery of Equipment");
            System.out.println("9. Pickup of Equipment");
            System.out.println("10. Useful Reports");
            System.out.println("11. Exit");
            System.out.print("Please enter your choice: ");

            int choice = readMenuChoice(scanner);
            switch (choice) {
                case 1:
                    System.out.println("You selected Manage Customers");
                    customerManager.menu();
                    break;
                case 2:
                    System.out.println("You selected Manage Warehouses");
                    System.out.println("This version does not support this option. We are sorry for inconveniences caused.");
                    break;
                case 3:
                    System.out.println("You selected Manage Drones");
                    droneManager.menu();
                    break;
                case 4:
                    System.out.println("You selected Manage Equipment");
                    System.out.println("This version does not support this option. We are sorry for inconveniences caused.");
                    break;
                case 5:
                    System.out.println("You selected Manage Purchase Orders");
                    System.out.println("This version does not support this option. We are sorry for inconveniences caused.");
                    break;
                case 6:
                    System.out.println("You selected Rent Equipment");
                    rentals.handleRentEquipment();
                    break;
                case 7:
                    System.out.println("You selected Return Equipment");
                    returns.handleReturnEquipment();
                    break;
                case 8:
                    System.out.println("You selected Delivery of Equipment");
                    rentals.scheduleDelivery();
                    break;
                case 9:
                    System.out.println("You selected Pickup of Equipment");
                    rentals.schedulePickup();
                    break;
                case 10:
                    System.out.println("You selected Useful Reports");
                    reportService.menu();
                    break;
                case 11:
                    System.out.println("You selected Exit");
                    stay = false;
                    break;
                default:
                    System.out.println("Invalid menu option.");
            }
        }

        scanner.close();
        DB.closeConnection();
        System.out.println("Bye");
    }

    private static int readMenuChoice(Scanner scanner) {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
}
