import java.util.Scanner;


public class Returns {
    public void menu() {
        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        while (choice != 2) {
            System.out.println("--- Returns ---");
            System.out.println("1. Pickup");
            System.out.println("2. Return to Main Menu");
            choice = scanner.nextInt();

            switch (choice) {
                case 1 -> System.out.println("You selected Delivery");
                case 2 -> System.out.println("Returning to main menu...");
            }
        }
        
        scanner.close();
    }
}
