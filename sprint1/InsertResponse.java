package sprint1;

import java.sql.*;
import java.util.Scanner;

import digital_complain_box.Response;

public class InsertResponse {

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

    public void insertResponse(Response response) {
        String sql = "INSERT INTO responses (complaint_id, user_id, comment) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             Scanner sc = new Scanner(System.in)) {

        	System.out.print("Enter username(ADMIN): ");
            String username = sc.nextLine();
            int userId = fetchId(conn, "SELECT user_id FROM users WHERE username = ?", username);
            if (userId == -1) {
                System.out.println("ADMIN user not found!");
                return;
            }
            
            String userRole = fetchUserRole(conn, "SELECT role FROM users WHERE username = ?", username);
            System.out.println(userRole.toLowerCase());
            if (!userRole.toLowerCase().equals("admin")) {
                System.out.println("User is not an ADMIN!");
                return;
            }
            
            System.out.print("Enter Password: ");
            String password = sc.nextLine();
            String userPass = fetchUserPass(conn, "SELECT password_hash FROM users WHERE username = ?", username);
            if (!userPass.equals(password)) {
                System.out.println("Wrong Password");
                return;
            }
            
            System.out.print("Enter username(USER): ");
            String usernameUser = sc.nextLine();
            int userIdUser = fetchId(conn, "SELECT user_id FROM users WHERE username = ?", usernameUser);
            if (userIdUser == -1) {
                System.out.println("User not found!");
                return;
            }
            
            System.out.print("Enter Department name: ");
            String deptName = sc.nextLine();
            int deptId = fetchId(conn, "SELECT dept_id FROM departments WHERE dept_name = ?", deptName);
            if (deptId == -1) {
                System.out.println("This Department does not exist");
                return;
            }
            
            System.out.print("Enter Category name: ");
            String categoryName = sc.nextLine();
            int categoryId = fetchId(conn, "SELECT category_id FROM categories WHERE category_name = ?", categoryName);
            if (categoryId == -1) {
                System.out.println("This Category does not exist");
                return;
            }

            int complaintId = fetchComplaintId(conn, "SELECT complaint_id FROM complaints WHERE user_id = ? AND dept_id = ? AND category_id = ?", userIdUser, deptId, categoryId);
            if (complaintId == -1) {
                System.out.println("Department not found!");
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, complaintId);
                stmt.setInt(2, userIdUser);
                stmt.setString(3, response.getComment());
                stmt.executeUpdate();
                System.out.println("Response added successfully!");
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

    public static void main(String[] args) {
        InsertResponse app = new InsertResponse();
        Scanner sc = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("\n--- Response CRUD Menu ---");
                System.out.println("1. Insert Response");
                System.out.println("2. Read All Responses");
                System.out.println("3. Exit");
                System.out.print("Choose option: ");

                if (!sc.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a number.");
                    sc.next(); // skip invalid
                    continue;
                }

                int choice = sc.nextInt();
                sc.nextLine();  // clear buffer

                switch (choice) {
                    case 1:
                    	Timestamp now = new Timestamp(System.currentTimeMillis());
                        Response response = new Response(
                            null, null, null, "Fixed", now
                        );
                        app.insertResponse(response);
                        break;
                    case 2:
                        app.readResponses();
                        break;
                    case 3:
                        System.out.println("Exiting...");
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid option. Try again.");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
