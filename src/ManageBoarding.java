import java.util.Scanner;

public class ManageBoarding {
    public final dbConnection db;

    public ManageBoarding(dbConnection db) {
        this.db = db;
    }

    public void board_passengers() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter boarding passenger ID (or -1 to exit): ");
            int passengerId = scanner.nextInt();

            if (passengerId == -1) {
                System.out.println("Exiting update loop...");
                break;
            }

            String updateSql = String.format(
                    "UPDATE Passenger_Status SET IsAboard = true WHERE PassengerID = %d",
                    passengerId
            );

            Object result = this.db.execute(updateSql, false);

            if (result != null) {
                int rows = (int) result;
                if (rows > 0) {
                    System.out.println("Passenger " + passengerId + " is now aboard.");
                } else {
                    System.out.println("No passenger found with ID: " + passengerId);
                }
            } else {
                System.out.println("An error occurred while add passenger");
                return;
            }
        }
    }
}
