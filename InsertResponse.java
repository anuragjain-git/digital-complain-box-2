package sprint1;

import java.sql.*;
import java.util.Scanner;

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

    public void insertResponse(Response response, Scanner sc) {
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

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, complaintId);
                stmt.setInt(2, userIdUser);
                stmt.setString(3, response.getComment());
                stmt.executeUpdate();
                System.out.println("Response added successfully!");

                String updateStatus = "UPDATE complaints SET status = ? WHERE complaint_id = ?";
                try (PreparedStatement statusStmt = conn.prepareStatement(updateStatus)) {
                    statusStmt.setString(1, "Responded");
                    statusStmt.setInt(2, complaintId);
                    statusStmt.executeUpdate();
                    System.out.println("Complaint status updated to 'Responded'.");
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

    public static void main(String[] args) {
        InsertResponse app = new InsertResponse();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Response CRUD Menu ---");
            System.out.println("1. Insert Response");
            System.out.println("2. Read All Responses");
            System.out.println("3. Exit");
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
                    Timestamp now = new Timestamp(System.currentTimeMillis());
                    Response response = new Response(null, null, null, "Fixed", now);
                    app.insertResponse(response, sc);
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
        }
    }
}
