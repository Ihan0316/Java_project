package mini_project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LotteryDatabase {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe"; 
    private static final String DB_USER = "scott"; 
    private static final String DB_PASSWORD = "tiger";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
