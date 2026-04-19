import java.sql.ResultSet;
import java.sql.SQLException;

public class GatePrinter {

    private final dbConnection db;

    public GatePrinter(dbConnection db) {
        this.db = db;
    }

    // Print all gates
    public void printGates() {
        String sql = "SELECT id, operatorid FROM gate ORDER BY id";

        try {
            ResultSet rs = (ResultSet) db.execute(sql, true);

            if (rs == null) {
                System.out.println("Error retrieving gates.");
                return;
            }

            System.out.println("===== All Gates =====");

            boolean found = false;

            while (rs.next()) {
                found = true;

                String gateID = rs.getString("id");
                Object operatorObj = rs.getObject("operatorid");

                System.out.println("Gate ID: " + gateID);

                if (operatorObj != null) {
                    System.out.println("Operator ID: " + operatorObj);
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

            rs.close();

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    // Print gate with full details
    public void printGateById(String gateID) {

        String gateSql = String.format(
                "SELECT id, operatorid FROM gate WHERE id = '%s'", gateID);

        String summarySql = String.format("""
                SELECT
                    COUNT(*) AS total_passengers,
                    COALESCE(SUM(CASE WHEN IsAboard = TRUE THEN 1 ELSE 0 END), 0) AS boarded_count,
                    COALESCE(SUM(CASE WHEN IsAboard = FALSE THEN 1 ELSE 0 END), 0) AS not_boarded_count
                FROM Passenger_Status
                WHERE GateID = '%s'
                """, gateID);

        String passengersSql = String.format("""
                SELECT
                    ps.PassengerID,
                    p.first_name,
                    p.last_name,
                    ps.IsAboard
                FROM Passenger_Status ps
                LEFT JOIN Passenger p
                    ON ps.PassengerID = p.passenger_id
                WHERE ps.GateID = '%s'
                ORDER BY ps.PassengerID
                """, gateID);

        try {

            // ---------- Gate Info ----------
            ResultSet gateRs = (ResultSet) db.execute(gateSql, true);

            if (gateRs == null || !gateRs.next()) {
                System.out.println("Error: Gate does not exist.");
                return;
            }

            Object operatorObj = gateRs.getObject("operatorid");

            System.out.println("===== Gate Details =====");
            System.out.println("Gate ID: " + gateID);

            if (operatorObj != null) {
                System.out.println("Operator ID: " + operatorObj);
                System.out.println("Status: OPEN");
            } else {
                System.out.println("Operator ID: None");
                System.out.println("Status: CLOSED");
            }

            System.out.println();
            gateRs.close();

            // ---------- Summary ----------
            ResultSet summaryRs = (ResultSet) db.execute(summarySql, true);

            if (summaryRs != null && summaryRs.next()) {
                int total = summaryRs.getInt("total_passengers");
                int boarded = summaryRs.getInt("boarded_count");
                int notBoarded = summaryRs.getInt("not_boarded_count");

                System.out.println("===== Passenger Summary =====");
                System.out.println("Total Passengers: " + total);
                System.out.println("Boarded: " + boarded);
                System.out.println("Not Boarded: " + notBoarded);
                System.out.println();
            }

            if (summaryRs != null) summaryRs.close();

            // ---------- Passenger List ----------
            ResultSet rs = (ResultSet) db.execute(passengersSql, true);

            System.out.println("===== Passenger List =====");

            boolean found = false;

            if (rs != null) {
                while (rs.next()) {
                    found = true;

                    int passengerID = rs.getInt("PassengerID");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    boolean isAboard = rs.getBoolean("IsAboard");

                    String name = ((firstName == null ? "" : firstName) + " " +
                            (lastName == null ? "" : lastName)).trim();

                    if (name.isEmpty()) {
                        name = "Name unavailable";
                    }

                    System.out.println("Passenger ID: " + passengerID);
                    System.out.println("Name: " + name);
                    System.out.println("Boarding Status: " + (isAboard ? "Aboard" : "Not Aboard"));
                    System.out.println("------------------------");
                }

                rs.close();
            }

            if (!found) {
                System.out.println("No passengers assigned to this gate.");
            }

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }
}