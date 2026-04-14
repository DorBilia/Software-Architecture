import java.sql.ResultSet;
import java.sql.SQLException;

public class GateController {

    private dbConnection db;

    public GateController(dbConnection db) {
        this.db = db;
    }

    // Opens the gate by assigning a worker to it
    // checkGateSql checks whether the gate exists
    // checkWorkerSql checks whether the worker is already at another gate and whether the worker exists
    // Also notifies if the operation failed due to a database error
    public void assignOperator(String gateID, int workerID) {
        try {

            String checkGateSql = String.format("SELECT id FROM gate WHERE id = '%s'", gateID);
            ResultSet rsGate = (ResultSet) db.execute(checkGateSql, true);

            if (rsGate == null || !rsGate.next()) {
                System.out.println("Error: Gate " + gateID + " does not exist.");
                return;
            }

            String checkWorkerSql = String.format(
                    "SELECT e.employee_id, g.id as assigned_gate " +
                            "FROM employees e " +
                            "LEFT JOIN gate g ON e.employee_id = g.operatorid " +
                            "WHERE e.employee_id = %d", workerID);

            ResultSet rsWorker = (ResultSet) db.execute(checkWorkerSql, true);

            if (rsWorker == null || !rsWorker.next()) {
                System.out.println("Error: Worker " + workerID + " does not exist.");
                return;
            }

            String assignedGate = rsWorker.getString("assigned_gate");
            if (assignedGate != null && !assignedGate.equals(gateID)) {
                System.out.println("Error: Worker " + workerID + " is busy working at Gate " + assignedGate + ".");
                return;
            }

            String updateSql = String.format("UPDATE gate SET operatorid = %d WHERE id = '%s'", workerID, gateID);
            db.execute(updateSql, false);
            System.out.println("Success: Worker " + workerID + " has been assigned to Gate " + gateID + ".");

        } catch (SQLException e) {
            System.out.println("System Error: " + e.getMessage());
        }
    }

    // Closes the gate by unassigning the worker in the gate
    // checkSql checks whether the gate is already closed and whether it exists
    // Also notifies if the operation failed due to a database error
    public void removeOperator(String gateID) {
        try {

            String checkSql = String.format("SELECT operatorid FROM gate WHERE id = '%s'", gateID);
            ResultSet rs = (ResultSet) db.execute(checkSql, true);

            if (rs != null && rs.next()) {
                if (rs.getObject("operatorid") == null) {
                    System.out.println("Gate " + gateID + " is already closed.");
                    return;
                }
            } else {
                System.out.println("Operation Failed: Gate ID " + gateID + " does not exist.");
                return;
            }

            String updateSql = String.format("UPDATE gate SET operatorid = NULL WHERE id = '%s'", gateID);
            db.execute(updateSql, false);
            System.out.println("Success: Gate " + gateID + " is now closed.");

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }
}