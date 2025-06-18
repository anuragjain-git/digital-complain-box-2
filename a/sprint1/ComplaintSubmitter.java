package sprint1;

import java.sql.*;

public class ComplaintSubmitter implements Runnable {
    private String username;
    private String deptName;
    private String categoryName;
    private String title;
    private String description;

    public ComplaintSubmitter(String username, String deptName, String categoryName, String title, String description) {
        this.username = username;
        this.deptName = deptName;
        this.categoryName = categoryName;
        this.title = title;
        this.description = description;
    }

    @Override
    public void run() {
        try (Connection conn = DBConnection.getConnection()) {
            InsertComplaint insert = new InsertComplaint();
            int userId = insert.fetchId(conn, "SELECT user_id FROM users WHERE username = ?", username);
            if (userId == -1) {
                System.out.println("User not found: " + username);
                return;
            }

            int deptId = insert.fetchId(conn, "SELECT dept_id FROM departments WHERE dept_name = ?", deptName);
            if (deptId == -1) {
                System.out.println("Department not found: " + deptName);
                return;
            }

            int categoryId = insert.fetchId(conn, "SELECT category_id FROM categories WHERE category_name = ?", categoryName);
            if (categoryId == -1) {
                System.out.println("Category not found: " + categoryName);
                return;
            }
            
            String checkDuplicateQuery = "SELECT 1 FROM complaints WHERE user_id = ? AND dept_id = ? AND category_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(checkDuplicateQuery)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, deptId);
                stmt.setInt(3, categoryId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                    	System.out.println("Complaint already exists for: " + username + " / " + deptName + " / " + categoryName);
                        return;
                    }
                }
            }


            String sql = "INSERT INTO complaints (user_id, dept_id, category_id, title, description, status) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, deptId);
                stmt.setInt(3, categoryId);
                stmt.setString(4, title);
                stmt.setString(5, description);
                stmt.setString(6, "OPEN");
                stmt.executeUpdate();
                System.out.println("Complaint inserted for: " + username);
            }

        } catch (Exception e) {
            System.out.println("Error in thread for user " + username + ": " + e.getMessage());
        }
    }
}