package sprint1;

import java.sql.*;
import java.util.Scanner;

public class InsertResponse {
	
	public void printResponseList() {
	    CollectionsStore.responseList.clear(); // Clear to prevent duplicates

	    String sql = "SELECT * FROM responses";

	    try (Connection conn = DBConnection.getConnection();
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {

	        while (rs.next()) {
	            Response response = new Response(
	                rs.getString("comment"),
	                rs.getTimestamp("created_at")
	            );
	            CollectionsStore.responseList.add(response);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    System.out.println("\n--- Responses in Memory (Loaded from DB) ---");
	    if (CollectionsStore.responseList.isEmpty()) {
	        System.out.println("No responses found.");
	    } else {
	        for (Response r : CollectionsStore.responseList) {
	            System.out.println("Comment: " + r.getComment());
	            System.out.println("Created At: " + r.getCreatedAt());
	            System.out.println("------------------------");
	        }
	    }
	}


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

    private int fetchComplaintId(Connection conn, String query, int param1, int param2, int param3) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, param1);
            stmt.setInt(2, param2);
            stmt.setInt(3, param3);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("complaint_id");
                }
            }
        }
        return -1;
    }

    private String fetchUserRole(Connection conn, String query, String param) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        }
        return "";
    }

    private String fetchUserPass(Connection conn, String query, String param) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password_hash");
                }
            }
        }
        return "";
    }
    
    private String fetchComplaintStatus(Connection conn, String query, int param) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        }
        return "";
    }

    public void insertResponse(Response response) {
    	Scanner sc = new Scanner(System.in);
        String sql = "INSERT INTO responses (complaint_id, user_id, comment) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {

            System.out.print("Enter username (ADMIN): ");
            String username = sc.nextLine().trim();
            int userId = fetchId(conn, "SELECT user_id FROM users WHERE username = ?", username);
            if (userId == -1) {
                System.out.println("ADMIN user not found!");
                return;
            }

            String userRole = fetchUserRole(conn, "SELECT role FROM users WHERE username = ?", username);
            if (!userRole.equalsIgnoreCase("admin")) {
                System.out.println("Access denied: User is not an ADMIN!");
                return;
            }

            System.out.print("Enter Password: ");
            String password = sc.nextLine();
            String userPass = fetchUserPass(conn, "SELECT password_hash FROM users WHERE username = ?", username);
            if (!userPass.equals(password)) {
                System.out.println("Wrong Password. Returning to menu...");
                return;
            }

            System.out.print("Enter username (USER): ");
            String usernameUser = sc.nextLine().trim();
            int userIdUser = fetchId(conn, "SELECT user_id FROM users WHERE username = ?", usernameUser);
            if (userIdUser == -1) {
                System.out.println("User not found!");
                return;
            }

            System.out.print("Enter Department name: ");
            String deptName = sc.nextLine().trim();
            int deptId = fetchId(conn, "SELECT dept_id FROM departments WHERE dept_name = ?", deptName);
            if (deptId == -1) {
                System.out.println("This Department does not exist");
                return;
            }

            System.out.print("Enter Category name: ");
            String categoryName = sc.nextLine().trim();
            int categoryId = fetchId(conn, "SELECT category_id FROM categories WHERE category_name = ?", categoryName);
            if (categoryId == -1) {
                System.out.println("This Category does not exist");
                return;
            }

            int complaintId = fetchComplaintId(conn,
                    "SELECT complaint_id FROM complaints WHERE user_id = ? AND dept_id = ? AND category_id = ?",
                    userIdUser, deptId, categoryId);
            if (complaintId == -1) {
                System.out.println("Complaint not found for provided user/department/category.");
                return;
            }
            
            String complaintStatus = fetchComplaintStatus(conn,
            		"SELECT status FROM complaints WHERE complaint_id = ?",
            		complaintId);
            if(complaintStatus.toLowerCase() == "closed") {
            	System.out.println("Complaint has been closed.");
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, complaintId);
                stmt.setInt(2, userIdUser);
                stmt.setString(3, response.getComment());
                stmt.executeUpdate();
                System.out.println("Response added successfully!");

                String updateStatus = "UPDATE complaints SET status = ? WHERE complaint_id = ?";
                try (PreparedStatement statusStmt = conn.prepareStatement(updateStatus)) {
                    statusStmt.setString(1, "In Progress");
                    statusStmt.setInt(2, complaintId);
                    statusStmt.executeUpdate();
                    System.out.println("Complaint status updated to 'In Progress'.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void readResponses() {
        String sql = "SELECT * FROM responses";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n=== Responses List ===");
            while (rs.next()) {
                System.out.println("Response ID: " + rs.getInt("response_id"));
                System.out.println("Complaint ID: " + rs.getInt("complaint_id"));
                System.out.println("User ID: " + rs.getInt("user_id"));
                System.out.println("Comment: " + rs.getString("comment"));
                System.out.println("Created At: " + rs.getTimestamp("created_at"));
                System.out.println("----------------------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateResponse() {
        try (Connection conn = DBConnection.getConnection();
             Scanner sc = new Scanner(System.in)) {

            System.out.print("Enter Response ID to update: ");
            int responseId = Integer.parseInt(sc.nextLine());

            System.out.print("Enter new comment: ");
            String newComment = sc.nextLine();

            String sql = "UPDATE responses SET comment = ? WHERE response_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newComment);
                stmt.setInt(2, responseId);

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("Response updated successfully!");
                } else {
                    System.out.println("No response found with the given ID.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteResponse() {
        try (Connection conn = DBConnection.getConnection();
             Scanner sc = new Scanner(System.in)) {

            System.out.print("Enter Response ID to delete: ");
            int responseId = Integer.parseInt(sc.nextLine());

            String sql = "DELETE FROM responses WHERE response_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, responseId);

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("Response deleted successfully!");
                } else {
                    System.out.println("No response found with the given ID.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        InsertResponse app = new InsertResponse();
        Scanner sc = new Scanner(System.in);

        while (true) {
        	System.out.println("1. Insert Response");
        	System.out.println("2. Read All Responses");
        	System.out.println("3. Update Response");
        	System.out.println("4. Delete Response");
        	System.out.println("5. Print Response List");
        	System.out.println("6. Exit");
        	System.out.print("Choose option: ");

            String input = sc.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
            case 1:
                System.out.print("Enter comment: ");
                String comment = sc.nextLine();
                Response response = new Response(comment);
                app.insertResponse(response);
                break;
            case 2:
                app.readResponses();
                break;
            case 3:
                app.updateResponse();
                break;
            case 4:
                app.deleteResponse();
                break;
            case 5:
                app.printResponseList();
                break;
            case 6:
                System.out.println("Exiting...");
                sc.close();
                return;
            default:
                System.out.println("Invalid option. Try again.");
        }

        }
    }
}

    

           

               