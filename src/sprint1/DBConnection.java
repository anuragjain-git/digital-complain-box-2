package sprint1;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/sprint1";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";
    
    public static Connection getConnection() throws SQLException {
    	try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected successfully!");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

