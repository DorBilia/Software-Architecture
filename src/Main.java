import java.sql.SQLException;


public static void main() throws SQLException {
    String dbAddr = "localhost";
    int dbPort = 5433;
    String dbUser = "postgres";
    String dbPass = "postgres";
    String dbName = "postgres";
    dbConnection db = new dbConnection(dbUser, dbPass, dbPort, dbAddr, dbName);
    ManageBoarding.board_passengers(db);
}

