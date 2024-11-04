package mini_projectDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LotteryDatabase {
	// oracle DB 연결
	private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
	private static final String DB_USER = "scott";
	private static final String DB_PASSWORD = "tiger";

	// DB 연결 메서드
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
	} // Connection 메서드
} // LotteryDatabase Class
