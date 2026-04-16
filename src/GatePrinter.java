import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GatePrinter {

    private final Connection connection;

    public GatePrinter(Connection connection) {
        this.connection = connection;
    }

    // Print all gates
    public void printGates() {
        String sql = "SELECT ID, OperatorID FROM Gate ORDER BY ID";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("===== All Gates =====");

            boolean found = false;

            while (rs.next()) {
                found = true;

                String gateID = rs.getString("ID");
                Integer operatorID = (Integer) rs.getObject("OperatorID");

                System.out.println("Gate ID: " + gateID);

                if (operatorID != null) {
                    System.out.println("Operator ID: " + operatorID);
                    System.out.println("Status: OPEN");
                } else {
                    System.out.println("Operator ID: None");
                    System.out.println("Status: CLOSED");
                }

                System.out.println("------------------------");
            }

            if (!found) {
                System.out.println("No gates found.");
            }

        } catch (SQLException e) {
            System.out.println("Error printing gates: " + e.getMessage());
        }
    }

    // Print one gate with full details
    public void printGateById(String gateID) {
        String gateSql = "SELECT ID, OperatorID FROM Gate WHERE ID = ?";

        String summarySql = """
                SELECT
                    COUNT(*) AS total_passengers,
                    SUM(CASE WHEN IsAboard = TRUE THEN 1 ELSE 0 END) AS boarded_count,
                    SUM(CASE WHEN IsAboard = FALSE THEN 1 ELSE 0 END) AS not_boarded_count
                FROM Passenger_Status
                WHERE GateID = ?
                """;

        String passengersSql = """
                SELECT
                    ps.PassengerID,
                    p.first_name,
                    p.last_name,
                    ps.IsAboard
                FROM Passenger_Status ps
                LEFT JOIN Passenger p
                    ON ps.PassengerID = p.passenger_id
                WHERE ps.GateID = ?
                ORDER BY ps.PassengerID
                """;

        try (PreparedStatement gateStmt = connection.prepareStatement(gateSql);
             PreparedStatement summaryStmt = connection.prepareStatement(summarySql);
             PreparedStatement passengersStmt = connection.prepareStatement(passengersSql)) {

            gateStmt.setString(1, gateID);

            try (ResultSet gateRs = gateStmt.executeQuery()) {
                if (!gateRs.next()) {
                    System.out.println("Error: Gate does not exist.");
                    return;
                }

                Integer operatorID = (Integer) gateRs.getObject("OperatorID");

                System.out.println("===== Gate Details =====");
                System.out.println("Gate ID: " + gateRs.getString("ID"));

                if (operatorID != null) {
                    System.out.println("Operator ID: " + operatorID);
                    System.out.println("Status: OPEN");
                } else {
                    System.out.println("Operator ID: None");
                    System.out.println("Status: CLOSED");
                }

                System.out.println();
            }

            summaryStmt.setString(1, gateID);

            try (ResultSet summaryRs = summaryStmt.executeQuery()) {
                if (summaryRs.next()) {
                    int totalPassengers = summaryRs.getInt("total_passengers");
                    int boardedCount = summaryRs.getInt("boarded_count");
                    int notBoardedCount = summaryRs.getInt("not_boarded_count");

                    System.out.println("===== Passenger Summary =====");
                    System.out.println("Total Passengers: " + totalPassengers);
                    System.out.println("Boarded: " + boardedCount);
                    System.out.println("Not Boarded: " + notBoardedCount);
                    System.out.println();
                }
            }

            passengersStmt.setString(1, gateID);

            try (ResultSet passengersRs = passengersStmt.executeQuery()) {
                System.out.println("===== Passenger List =====");

                boolean foundPassengers = false;

                while (passengersRs.next()) {
                    foundPassengers = true;

                    int passengerID = passengersRs.getInt("PassengerID");
                    String firstName = passengersRs.getString("first_name");
                    String lastName = passengersRs.getString("last_name");
                    boolean isAboard = passengersRs.getBoolean("IsAboard");

                    String fullName = ((firstName == null ? "" : firstName) + " " +
                            (lastName == null ? "" : lastName)).trim();

                    if (fullName.isEmpty()) {
                        fullName = "Name unavailable";
                    }

                    System.out.println("Passenger ID: " + passengerID);
                    System.out.println("Name: " + fullName);
                    System.out.println("Boarding Status: " + (isAboard ? "Aboard" : "Not Aboard"));
                    System.out.println("------------------------");
                }

                if (!foundPassengers) {
                    System.out.println("No passengers assigned to this gate.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error printing gate details: " + e.getMessage());
        }
    }
}