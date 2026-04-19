import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class dbConnection {
    public Connection connection;

    public dbConnection(String url, String username, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, username, password);
        if (connection != null) {
            System.out.println("Connected to the database successfully!");
        }
    }

    public Object execute(String query, boolean fetch) {
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            if (fetch) {
                // Returns a ResultSet
                return stmt.executeQuery();
            } else {
                // Returns an Integer representing rows affected
                int rowsAffected = stmt.executeUpdate();
                stmt.close();
                return rowsAffected;
            }
        } catch (SQLException e) {
            System.err.println("Execution Error: " + e.getMessage());
            return null; // Indication that the function returns an error
        }
    }
}

