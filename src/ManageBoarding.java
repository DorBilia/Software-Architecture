import java.util.Scanner;

public class ManageBoarding {

    public static void board_passengers(dbConnection db) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter boarding passenger ID (or -1 to exit): ");
            int passengerId = scanner.nextInt();

            // Exit condition
            if (passengerId == -1) {
                System.out.println("Exiting update loop...");
                break;
            }

            // Build the SQL string
            String updateSql = String.format(
                    "UPDATE PassengerStatus SET IsAboard = true WHERE PassengerID = %d",
                    passengerId
            );

            // Use your generic function (isSelect = false)
            Object result = db.execute(updateSql, false);

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
