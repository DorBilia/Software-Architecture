import java.sql.SQLException;
import java.util.Scanner;

public class main{
    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws SQLException {
        String dbAddr = "localhost";
        int dbPort = 5433;
        String dbUser = "postgres";
        String dbPass = "postgres";
        String dbName = "postgres";
        dbConnection db = new dbConnection(dbUser, dbPass, dbPort, dbAddr, dbName);
        menu(db);
    }

    public static void menu(dbConnection db) throws SQLException {
        while (true) {
            GateController gateController = new GateController(db);
            GatePrinter gatePrinter = new GatePrinter(db);
            ManageBoarding manageBoarding = new ManageBoarding(db);
            MissingPrint missingPrint = new MissingPrint(db);
            System.out.println(
                    """
                            Choose one of the following options:
                            1. Print information of all gates
                            2. Print information of a specific gate
                            3. Print all passengers who didn't board on the flight in a specific gate
                            4. Record passenger boarding
                            5. Assign operator to gate
                            6. Remove operator from gate
                            7. Exit program
                            """);
            int Input = scanner.nextInt();
            String gateID;
            int userID;
            switch (Input) {
                // Print Gates
                case 1:
                    gatePrinter.printGates();
                    break;

                // Print Specific Gate
                case 2:
                    System.out.println("Enter gate ID (or \".\" to cancel):");
                    gateID = scanner.next();
                    if (gateID.equals(".")) continue;
                    gatePrinter.printGateById(gateID);
                    break;

                // Print Missing Passengers
                case 3:
                    System.out.println("Enter gate ID (or \".\" to cancel):");
                    gateID = scanner.next();
                    if (gateID.equals(".")) continue;
                    missingPrint.printMissingPassengers(gateID);
                    break;

                // Record Passenger Boarding
                case 4:
                    manageBoarding.board_passengers();
                    break;

                // Assign Operator
                case 5:
                    System.out.println("Enter gate ID (or \".\" to cancel):");
                    gateID = scanner.next();
                    if (gateID.equals(".")) continue;
                    System.out.println("Enter operator ID:");
                    userID = scanner.nextInt();
                    gateController.assignOperator(gateID, userID);
                    break;

                // Remove Operator
                case 6:
                    System.out.println("Enter gate ID (or \".\" to cancel):");
                    gateID = scanner.next();
                    if (gateID.equals(".")) continue;
                    gateController.removeOperator(gateID);
                    break;

                // Exit
                case 7:
                    System.out.println("Goodbye!");
                    System.exit(0);

                default:
                    System.out.println("Wrong input.");
            }
        }
    }
}

