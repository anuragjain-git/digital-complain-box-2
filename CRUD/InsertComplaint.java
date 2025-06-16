package CRUD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

import model.Complaint;
import util.DatabaseUtil;

public class InsertComplaint {
	
	public void insertComplaint(Complaint complaint) {
        String sql = "INSERT INTO complaints (user_id, dept_id, category_id, title, description, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection()){
    		Scanner sc = new Scanner(System.in);
    		
    		System.out.print("Enter username :");
    		String username = sc.nextLine();
    		String userNameQuery = "SELECT user_id FROM Users WHERE username = ?";
    		
    		System.out.print("Enter department name :");
    		String deptname = sc.nextLine();
    		String deptNameQuery = "SELECT dept_id FROM Users WHERE dept_name = ?";
    		
    		System.out.print("Enter category name :");
    		String categoryname = sc.nextLine();
    		String categoryNameQuery = "SELECT category_id FROM Users WHERE category_name = ?";
    		
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, #);
            stmt.setInt(2, #);
            stmt.setInt(3, #);
            stmt.setString(4, complaint.getTitle());
            stmt.setString(5, complaint.getDescription());
            stmt.setString(6, complaint.getStatus());

            stmt.executeUpdate();
            System.out.println("Complaint inserted.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
