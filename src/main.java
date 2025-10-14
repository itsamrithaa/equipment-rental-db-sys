import java.util.Scanner;
import java.lang.String;
import java.lang.System;
public class main {
    public static void main(String[] args) {
        //This will act as the main menu for the database system
        
        System.out.println("Welcome to the Database System for Group 10");
        System.out.println("Please select a number option from the following menu:");
        System.out.println("1. Manage Members");
        System.out.println("2. Manage Warehouses");
        System.out.println("3. Manage Drones");
        System.out.println("4. Manage Equipment");
        System.out.println("5. Manage Purchase Orders");
        System.out.println("6. Rentals");
        System.out.println("7. Ratings & Reviews");
        System.out.println("8. Search");
        System.out.println("9. Exit");
        System.out.println("Please enter your choice: ");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                System.out.println("You selected Manage Members");
                break;
            case 2:
                System.out.println("You selected Manage Warehouses");
                break;
            case 3:
                System.out.println("You selected Manage Drones");
                break;
            case 4:
                System.out.println("You selected Manage Equipment");
                break;
            case 5:
                System.out.println("You selected Manage Purchase Orders");
                break;
            case 6:
                System.out.println("You selected Rentals");
                break;
            case 7:
                System.out.println("You selected Ratings & Reviews");
                break;
            case 8:
                System.out.println("You selected Search");
                break;
            case 9:
                System.out.println("You selected Exit");
                break;
        }
    }
}
