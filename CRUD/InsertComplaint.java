package CRUD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Scanner;

import digital_complain_box.Complaint;
import util.DatabaseUtil;

public class InsertComplaint {

    public void insertComplaint(Complaint complaint) {
        String sql = "INSERT INTO complaints (user_id, dept_id, category_id, title, description, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             Scanner sc = new Scanner(System.in)) {

            // Get user_id from username
            System.out.print("Enter username: ");
            String username = sc.nextLine();
            String userNameQuery = "SELECT user_id FROM users WHERE username = ?";
            int userId = fetchId(conn, userNameQuery, username);
            if (userId == -1) {
                System.out.println("User not found!");
                return;
            }

            // Get dept_id from department name
            System.out.print("Enter department name: ");
            String deptname = sc.nextLine();
            String deptNameQuery = "SELECT dept_id FROM departments WHERE dept_name = ?";
            int deptId = fetchId(conn, deptNameQuery, deptname);
            if (deptId == -1) {
                System.out.println("Department not found!");
                return;
            }

            // Get category_id from category name
            System.out.print("Enter category name: ");
            String categoryname = sc.nextLine();
            String categoryNameQuery = "SELECT category_id FROM categories WHERE category_name = ?";
            int categoryId = fetchId(conn, categoryNameQuery, categoryname);
            if (categoryId == -1) {
                System.out.println("Category not found!");
                return;
            }

            // Prepare insert statement
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, deptId);
                stmt.setInt(3, categoryId);
                stmt.setString(4, complaint.getTitle());
                stmt.setString(5, complaint.getDescription());
                stmt.setString(6, complaint.getStatus());

                stmt.executeUpdate();
                System.out.println("Complaint inserted.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper method to fetch ID from a query
    private int fetchId(Connection conn, String query, String param) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1; // Not found
    }

    public static void main(String[] args) {
        InsertComplaint insertComplaint = new InsertComplaint();

        // Create complaint with title, description, and timestamps only
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Complaint complaint = new Complaint(
            null, null, null, null,
            "Internet not working",
            "Internet connectivity is down since morning.",
            now, now
        );

        insertComplaint.insertComplaint(complaint);
    }
}
