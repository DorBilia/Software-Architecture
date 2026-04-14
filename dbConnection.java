import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class dbConnection {
    public Connection connection;

    public dbConnection(String username, String password, int port, String dbAddr, String dbName) {
        try {
            String url = "jdbc:postgresql://" + dbAddr + ":" + port + "/" + dbName;
            this.connection = DriverManager.getConnection(url, username, password);
            if (connection != null) {
                System.out.println("Connected to the database successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Connection failure: " + e.getMessage());
        }
    }

    public ResultSet execute(String query, boolean fetch) {
        try {
            PreparedStatement stmt = this.connection.prepareStatement(query);
            if (fetch) {
                return stmt.executeQuery();
            } else {
                stmt.executeUpdate();
                stmt.close();
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Execution Error: " + e.getMessage());
            return null;
        }
    }
}

