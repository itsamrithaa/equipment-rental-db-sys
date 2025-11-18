import java.util.Scanner;

public class Returns {
    private final Scanner scanner;

    public Returns(Scanner scanner) {
        this.scanner = scanner;
    }

    public void handleReturnEquipment() {
        System.out.println("\n--- Return Equipment ---");
        String userId = prompt("Customer User ID: ");
        String equipmentSerial = prompt("Equipment Serial Number: ");
        String condition = prompt("Condition upon return: ");
        String returnDate = prompt("Return Date (YYYY-MM-DD): ");
        String notes = prompt("Any issues to report: ");

        System.out.printf("Return registered for user %s and equipment %s on %s. Condition: %s. Notes: %s%n",
                userId, equipmentSerial, returnDate, condition, notes.isEmpty() ? "None" : notes);
    }

    private String prompt(String label) {
        System.out.print(label);
        return scanner.nextLine().trim();
    }
}
