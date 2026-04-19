import java.sql.SQLException;

public static Scanner scanner = new Scanner(System.in);

public static void main(String[] args) throws SQLException {
    String dbAddr = "localhost";
    int dbPort = 5433;
    String dbUser = "postgres";
    String dbPass = "postgres";
    String dbName = "postgres";
    dbConnection db = new dbConnection(dbUser, dbPass, dbPort, dbAddr, dbName);
    ManageBoarding.board_passengers(db);

    menu();
}

public static void menu() {
    while (true) {
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
                printGates();
                break;

            // Print Specific Gate
            case 2:
                System.out.println("Enter gate ID (or \".\" to cancel):");
                gateID = scanner.next();
                if (gateID.equals(".")) continue;
                printGate(gateID);
                break;

            // Print Missing Passengers
            case 3:
                System.out.println("Enter gate ID (or \".\" to cancel):");
                gateID = scanner.next();
                if (gateID.equals(".")) continue;
                printMissingPassengers(gateID);
                break;

            // Record Passenger Boarding
            case 4:
                System.out.println("Enter gate ID (or \".\" to cancel):");
                gateID = scanner.next();
                if (gateID.equals(".")) continue;
                System.out.println("Enter passenger ID:");
                userID = scanner.nextInt();
                recordPassengerBoarding(gateID, userID);
                break;

            // Assign Operator
            case 5:
                System.out.println("Enter gate ID (or \".\" to cancel):");
                gateID = scanner.next();
                if (gateID.equals(".")) continue;
                System.out.println("Enter operator ID:");
                userID = scanner.nextInt();
                assignOperator(gateID, userID);
                break;

            // Remove Operator
            case 6:
                System.out.println("Enter gate ID (or \".\" to cancel):");
                gateID = scanner.next();
                if (gateID.equals(".")) continue;
                removeOperator(gateID);
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

