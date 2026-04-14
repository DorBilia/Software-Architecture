import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GateController {

    private Connection connection;

    public GateController(Connection connection) {
        this.connection = connection;
    }

    // Opens the gate by assigning a worker to it.
    // checkGateSql checks if the gate doesn't exist.
    // checkWorkerSql checks if the worker is already at another gate or if the worker doesn't exist.
    // Also notifies if the operation failed for other reasons (i.e. a database error).
    public void assignOperator(String gateID, int workerID) {
        try {

            String checkGateSql = "SELECT ID FROM Gate WHERE ID = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(checkGateSql)) {
                pstmt.setString(1, gateID);
                ResultSet rs = pstmt.executeQuery();

                if (!rs.next()) {
                    System.out.println("Error: Gate " + gateID + " does not exist.");
                    return;
                }
            }

            String checkWorkerSql = "SELECT e.employee_id, g.ID as assigned_gate " +
                    "FROM EMPLOYEES e " +
                    "LEFT JOIN Gate g ON e.employee_id = g.OperatorID " +
                    "WHERE e.employee_id = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(checkWorkerSql)) {
                pstmt.setInt(1, workerID);
                ResultSet rs = pstmt.executeQuery();

                if (!rs.next()) {
                    System.out.println("Error: Worker " + workerID + " does not exist in the employee records.");
                    return;
                }

                String assignedGate = rs.getString("assigned_gate");
                if (assignedGate != null && !assignedGate.equals(gateID)) {
                    System.out.println("Error: Worker " + workerID + " is already busy at Gate " + assignedGate + ".");
                    return;
                }
            }

            String updateSql = "UPDATE Gate SET OperatorID = ? WHERE ID = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(updateSql)) {
                pstmt.setInt(1, workerID);
                pstmt.setString(2, gateID);
                pstmt.executeUpdate();
                System.out.println("Success: Worker " + workerID + " has been assigned to Gate " + gateID + ".");
            }

        } catch (SQLException e) {
            System.out.println("System Error: " + e.getMessage());
        }
    }

    // Closes the gate by unassigning the worker in the gate.
    // checkSql checks if the gate is already closed or if it doesn't exist.
    // Also notifies if the operation failed for other reasons (i.e. a database error).
    public void removeOperator(String gateID) {
        try {

            String checkSql = "SELECT OperatorID FROM Gate WHERE ID = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(checkSql)) {
                pstmt.setString(1, gateID);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    if (rs.getObject("OperatorID") == null) {
                        System.out.println("Gate " + gateID + " is already closed.");
                        return;
                    }
                } else {
                    System.out.println("Operation Failed: Gate ID " + gateID + " does not exist.");
                    return;
                }
            }

            String updateSql = "UPDATE Gate SET OperatorID = NULL WHERE ID = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(updateSql)) {
                pstmt.setString(1, gateID);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Success: Gate " + gateID + " is now closed and the worker has been unassigned.");
                } else {
                    System.out.println("Operation Failed: Could not complete the request.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }
}