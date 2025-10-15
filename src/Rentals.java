import java.util.Scanner;


public class Rentals {
    public void menu() {
        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        System.out.println("--- Rentals ---");
        while (choice != 3) {
            System.out.println("1. Delivery");
            System.out.println("2. Pickup");
            System.out.println("3. Return to Main Menu");
            choice = scanner.nextInt();

            switch (choice) {
                case 1 -> System.out.println("You selected Delivery");
                case 2 -> System.out.println("You selected Pickup");
                case 3 -> System.out.println("Returning to main menu...");
            }
        }
        
        scanner.close();
    }
}
