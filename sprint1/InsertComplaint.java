package sprint1;

import java.sql.*;
import java.util.Scanner;

import digital_complain_box.Complaint;

public class InsertComplaint {

    private int fetchId(Connection conn, String query, String param) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public void insertComplaint(Complaint complaint) {
        String sql = "INSERT INTO complaints (user_id, dept_id, category_id, title, description, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             Scanner sc = new Scanner(System.in)) {

            System.out.print("Enter username: ");
            String username = sc.nextLine();
            int userId = fetchId(conn, "SELECT user_id FROM users WHERE username = ?", username);
            if (userId == -1) {
                System.out.println("User not found!");
                return;
            }

            System.out.print("Enter department name: ");
            String deptname = sc.nextLine();
            int deptId = fetchId(conn, "SELECT dept_id FROM departments WHERE dept_name = ?", deptname);
            if (deptId == -1) {
                System.out.println("Department not found!");
                return;
            }

            System.out.print("Select a category name: ");
            String categoryname = sc.nextLine();
            int categoryId = fetchId(conn, "SELECT category_id FROM categories WHERE category_name = ?", categoryname);
            if (categoryId == -1) {
                System.out.println("Category not found!");
                return;
            }
            
            String checkDuplicateQuery = "SELECT 1 FROM complaints WHERE user_id = ? AND dept_id = ? AND category_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(checkDuplicateQuery)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, deptId);
                stmt.setInt(3, categoryId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if(rs.next()) { // returns true if a matching row exists
                    	System.out.println("Complain already exist (username, deptname, categoryname are same)");
                    	return;
                    }
                }
            }

//            System.out.print("Enter complaint title: ");
//            String title = sc.nextLine();
//            System.out.print("Enter description: ");
//            String description = sc.nextLine();

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, deptId);
            stmt.setInt(3, categoryId);
            stmt.setString(4, complaint.getTitle());
            stmt.setString(5, complaint.getDescription());
            stmt.setString(6, complaint.getStatus());

            stmt.executeUpdate();
            System.out.println("Complaint inserted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void readComplaints() {
        String sql = "SELECT * FROM complaints";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n=== Complaints List ===");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("complaint_id"));
                System.out.println("Title: " + rs.getString("title"));
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("Created At: " + rs.getTimestamp("created_at"));
                System.out.println("----------------------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateComplaintStatus() {
        try (Connection conn = DBConnection.getConnection();
             Scanner sc = new Scanner(System.in)) {

            System.out.print("Enter Complaint ID to update: ");
            int id = sc.nextInt(); sc.nextLine();
            System.out.print("Enter new status (e.g. OPEN, Resolved, Closed): ");
            String status = sc.nextLine();

            String sql = "UPDATE complaints SET status = ? WHERE complaint_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, id);

            int rows = stmt.executeUpdate();
            System.out.println("Rows updated: " + rows);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteComplaint() {
        try (Connection conn = DBConnection.getConnection();
             Scanner sc = new Scanner(System.in)) {

            System.out.print("Enter Complaint ID to delete: ");
            int id = sc.nextInt();

            String sql = "DELETE FROM complaints WHERE complaint_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            int rows = stmt.executeUpdate();
            System.out.println("Rows deleted: " + rows);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        InsertComplaint app = new InsertComplaint();
        Scanner sc = new Scanner(System.in);

        while (true) {
        	try {
	            System.out.println("\n--- Complaint CRUD Menu ---");
	            System.out.println("1. Insert Complaint");
	            System.out.println("2. Read All Complaints");
	            System.out.println("3. Update Complaint Status");
	            System.out.println("4. Delete Complaint");
	            System.out.println("5. Exit");
	            System.out.print("Choose option: ");
	           
	            if (!sc.hasNextInt()) {
	                System.out.println("Invalid input. Please enter a number.");
	                sc.next(); // skip invalid
	                continue;
	            }
	
	            int choice = sc.nextInt();
	            sc.nextLine();  
	            
	            switch (choice) {
	                case 1:
	                	// Create complaint with title, description, and timestamps only
	                    Timestamp now = new Timestamp(System.currentTimeMillis());
	                    Complaint complaint = new Complaint(
	                        null, null, null, null,
	                        "Database not working",
	                        "Database connectivity issue since morning.",
	                        now, now
	                    );
	                	app.insertComplaint(complaint); 
	                	break;
	                case 2: app.readComplaints(); break;
	                case 3: app.updateComplaintStatus(); break;
	                case 4: app.deleteComplaint(); break;
	                case 5: System.out.println("Exiting..."); sc.close(); return;
	                default: System.out.println("Invalid option. Try again.");
	            }
        	}
            catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                sc.nextLine(); // recover from scanner error
            }
        }
    }
}
