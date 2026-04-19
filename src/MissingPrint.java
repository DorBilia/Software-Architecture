import java.sql.ResultSet;
import java.sql.SQLException;

public class MissingPrint {
    private final dbConnection db;

    public MissingPrint(dbConnection db) {
        this.db = db;
    }

    public void printMissingPassengers(String gateID) {
        String query = String.format(
                "SELECT PassengerID FROM Passenger_Status WHERE GateID = '%s' AND IsAboard = false",
                gateID
        );

        Object result = db.execute(query, true);

        if (result instanceof ResultSet) {
            ResultSet rs = (ResultSet) result;
            try {
                System.out.println("Passengers who haven't arrived at Gate " + gateID + ":");
                boolean found = false;
                while (rs.next()) {
                    System.out.println("- Passenger ID: " + rs.getInt("PassengerID"));
                    found = true;
                }
                if (!found) {
                    System.out.println("No missing passengers found for this gate.");
                }
                rs.close();
            } catch (SQLException e) {
                System.err.println("Error reading results: " + e.getMessage());
            }
        } else {
            System.out.println("Failed to retrieve data from the database.");
        }
    }
}
