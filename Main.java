import java.sql.Connection;
import java.sql.SQLException;

public class Main {


    public static void main(String[] args) throws SQLException {
        String dbAddr = "localhost";
        int dbPort = 5433;
        String dbUser = "postgres";
        String dbPass = "postgres";
        String dbName = "postgres";
        dbConnection db = new dbConnection(dbUser, dbPass, dbPort, dbAddr, dbName);
    }
}
