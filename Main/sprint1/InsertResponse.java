package sprint1;

import java.sql.*;
import java.util.*;

import sprint1.CollectionsStore;
import sprint1.Response;

public class InsertResponse {

    private int fetchId(Connection conn, String query, String param) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
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
                if (rs.next()) return rs.getInt("complaint_id");
            }
        }
        return -1;
    }

    private String fetchUserRole(Connection conn, String query, String param) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("role");
            }
        }
        return "";
    }

    private String fetchUserPass(Connection conn, String query, String param) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("password_hash");
            }
        }
        return "";
    }

    private String fetchComplaintStatus(Connection conn, int complaintId) throws SQLException {
        String sql = "SELECT status FROM complaints WHERE complaint_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, complaintId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("status");
            }
        }
        return "";
    }

    private String chooseFromList(Connection conn, String table, String column, String displayLabel, Scanner sc) throws SQLException {
        String sql = "SELECT " + column + " FROM " + table;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<String> items = new ArrayList<>();
            int index = 1;

            System.out.println("\nChoose " + displayLabel + ":");
            while (rs.next()) {
                String name = rs.getString(column);
                items.add(name);
                System.out.println(index++ + ". " + name);
            }

            if (items.isEmpty()) {
                System.out.println("No " + displayLabel + " available.");
                return null;
            }

            System.out.print("Enter choice number: ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            if (choice < 1 || choice > items.size()) {
                System.out.println("Invalid choice. Returning to menu.");
                return null;
            }

            return items.get(choice - 1);
        }
    }

    public void insertResponse(Response response, Scanner sc) {
        String sql = "INSERT INTO responses (complaint_id, user_id, comment) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("Enter your username (admin/employee): ");
            String username = sc.nextLine().trim();
            int adminId = fetchId(conn, "SELECT user_id FROM users WHERE username = ?", username);
            if (adminId == -1) {
                System.out.println("User not found!");
                return;
            }

            String role = fetchUserRole(conn, "SELECT role FROM users WHERE username = ?", username);
            if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("employee")) {
                System.out.println("Access denied: Must be admin or employee.");
                return;
            }

            System.out.print("Enter password: ");
            String password = sc.nextLine();
            String correctPass = fetchUserPass(conn, "SELECT password_hash FROM users WHERE username = ?", username);
            if (!correctPass.equals(password)) {
                System.out.println("Wrong password.");
                return;
            }

            System.out.print("Enter complaint owner's username: ");
            String userUsername = sc.nextLine().trim();
            int userId = fetchId(conn, "SELECT user_id FROM users WHERE username = ?", userUsername);
            if (userId == -1) {
                System.out.println("Complaint user not found!");
                return;
            }

            String deptName = chooseFromList(conn, "departments", "dept_name", "Service Area", sc);
            if (deptName == null) return;
            int deptId = fetchId(conn, "SELECT dept_id FROM departments WHERE dept_name = ?", deptName);

            String categoryName = chooseFromList(conn, "categories", "category_name", "Complaint Category", sc);
            if (categoryName == null) return;
            int categoryId = fetchId(conn, "SELECT category_id FROM categories WHERE category_name = ?", categoryName);

            int complaintId = fetchComplaintId(conn,
                "SELECT complaint_id FROM complaints WHERE user_id = ? AND dept_id = ? AND category_id = ?",
                userId, deptId, categoryId);
            if (complaintId == -1) {
                System.out.println("Complaint not found.");
                return;
            }

            String status = fetchComplaintStatus(conn, complaintId);
            if (status.equalsIgnoreCase("closed")) {
                System.out.println("This complaint is already closed.");
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, complaintId);
                stmt.setInt(2, userId);
                stmt.setString(3, response.getComment());
                stmt.executeUpdate();
                System.out.println("Response added successfully.");
            }

            String updateStatus = "UPDATE complaints SET status = ? WHERE complaint_id = ?";
            try (PreparedStatement statusStmt = conn.prepareStatement(updateStatus)) {
                statusStmt.setString(1, "In Progress");
                statusStmt.setInt(2, complaintId);
                statusStmt.executeUpdate();
                System.out.println("Complaint status updated to 'In Progress'.");
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

    public void updateResponse(Scanner sc) {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("Enter your username: ");
            String username = sc.nextLine().trim();
            String role = fetchUserRole(conn, "SELECT role FROM users WHERE username = ?", username);
            if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("employee")) {
                System.out.println("Access denied.");
                return;
            }

            System.out.print("Enter Response ID to update: ");
            int responseId = Integer.parseInt(sc.nextLine());

            System.out.print("Enter new comment: ");
            String newComment = sc.nextLine();

            String sql = "UPDATE responses SET comment = ? WHERE response_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newComment);
                stmt.setInt(2, responseId);
                int rows = stmt.executeUpdate();
                System.out.println(rows > 0 ? "Response updated." : "No response found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteResponse(Scanner sc) {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("Enter your username: ");
            String username = sc.nextLine().trim();
            String role = fetchUserRole(conn, "SELECT role FROM users WHERE username = ?", username);
            if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("employee")) {
                System.out.println("Access denied.");
                return;
            }

            System.out.print("Enter Response ID to delete: ");
            int responseId = Integer.parseInt(sc.nextLine());

            String sql = "DELETE FROM responses WHERE response_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, responseId);
                int rows = stmt.executeUpdate();
                System.out.println(rows > 0 ? "Response deleted." : "No response found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printResponseList() {
        CollectionsStore.responseList.clear();

        String sql = "SELECT * FROM responses";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- Responses in Memory (Loaded from DB) ---");
            while (rs.next()) {
                Response r = new Response(rs.getString("comment"), rs.getTimestamp("created_at"));
                CollectionsStore.responseList.add(r);
            }

            if (CollectionsStore.responseList.isEmpty()) {
                System.out.println("No responses found.");
            } else {
                for (Response r : CollectionsStore.responseList) {
                    System.out.println("Comment: " + r.getComment());
                    System.out.println("Created At: " + r.getCreatedAt());
                    System.out.println("------------------------");
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
            System.out.println("\n--- Response Menu ---");
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
                    app.insertResponse(response, sc);
                    break;
                case 2: app.readResponses(); break;
                case 3: app.updateResponse(sc); break;
                case 4: app.deleteResponse(sc); break;
                case 5: app.printResponseList(); break;
                case 6:
                    System.out.println("Exiting...");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}
