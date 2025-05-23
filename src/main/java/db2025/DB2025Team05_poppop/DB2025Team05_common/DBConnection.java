package db2025.DB2025Team05_poppop.DB2025Team05_common;

import java.sql.*;

public class DBConnection {
    //JDBC driver name and database URL
	private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
	private static final String DB_URL = "jdbc:mysql://localhost:3306/DB2025Team05";
	private static final String USER = "DB2025Team05";
    private static final String PASSWORD = "DB2025Team05";

    static {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버 로딩 실패: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }
} 