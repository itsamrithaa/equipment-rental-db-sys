import java.util.Scanner;

public class Rentals {
    private final Scanner scanner;

    public Rentals(Scanner scanner) {
        this.scanner = scanner;
    }

    public void handleRentEquipment() {
        System.out.println("\n--- Rent Equipment ---");
        String userId = prompt("Customer User ID: ");
        String equipmentSerial = prompt("Equipment Serial Number: ");
        String checkoutDate = prompt("Checkout Date (YYYY-MM-DD): ");
        String dueDate = prompt("Due Date (YYYY-MM-DD): ");
        String notes = prompt("Any special instructions: ");

        System.out.printf("Rental recorded for user %s, equipment %s from %s to %s. Notes: %s%n",
                userId, equipmentSerial, checkoutDate, dueDate, notes.isEmpty() ? "None" : notes);
    }

    public void scheduleDelivery() {
        System.out.println("\n--- Schedule Delivery ---");
        String userId = prompt("Customer User ID: ");
        String equipmentSerial = prompt("Equipment Serial Number: ");
        String droneSerial = prompt("Assign Drone Serial Number: ");
        String deliveryDate = prompt("Delivery Date (YYYY-MM-DD): ");
        String window = prompt("Preferred delivery window (e.g., 10AM-12PM): ");

        System.out.printf("Delivery scheduled. Drone %s will deliver equipment %s to user %s on %s during %s.%n",
                droneSerial, equipmentSerial, userId, deliveryDate, window);
    }

    public void schedulePickup() {
        System.out.println("\n--- Schedule Pickup ---");
        String userId = prompt("Customer User ID: ");
        String equipmentSerial = prompt("Equipment Serial Number: ");
        String droneSerial = prompt("Assign Drone Serial Number for pickup: ");
        String pickupDate = prompt("Pickup Date (YYYY-MM-DD): ");
        String window = prompt("Preferred pickup window: ");

        System.out.printf("Pickup scheduled. Drone %s will pick up equipment %s from user %s on %s during %s.%n",
                droneSerial, equipmentSerial, userId, pickupDate, window);
    }

    private String prompt(String label) {
        System.out.print(label);
        return scanner.nextLine().trim();
    }
}
