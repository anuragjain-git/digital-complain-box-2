package sprint1;

import java.sql.*;
import java.util.*;

public class InsertComplaint {

    private String getUserRole(Connection conn, String username) throws SQLException {
        String sql = "SELECT role FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("role");
            }
        }
        return null;
    }

    public int fetchId(Connection conn, String query, String param) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public void insertComplaint(Complaint complaint, int userId, int deptId, int categoryId) {
        String sql = "INSERT INTO complaints (user_id, dept_id, category_id, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {

            String checkDuplicateQuery = "SELECT 1 FROM complaints WHERE user_id = ? AND dept_id = ? AND category_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(checkDuplicateQuery)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, deptId);
                stmt.setInt(3, categoryId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Complaint already exists for this user/service area/category.");
                        return;
                    }
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, deptId);
                stmt.setInt(3, categoryId);
                stmt.setString(4, complaint.getDescription());
                stmt.executeUpdate();
                System.out.println("Complaint inserted successfully!");
            }

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
                System.out.println("Description: " + rs.getString("description"));
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("Created At: " + rs.getTimestamp("created_at"));
                System.out.println("----------------------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateComplaintStatus(String role) {
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("employee")) {
            System.out.println("Access denied. Only admin and employee can update complaints.");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             Scanner sc = new Scanner(System.in)) {

            System.out.print("Enter Complaint ID to update: ");
            int id = sc.nextInt(); sc.nextLine();

            System.out.print("Enter new status (e.g. OPEN, Resolved, Closed): ");
            String status = sc.nextLine();

            String sql = "UPDATE complaints SET status = ? WHERE complaint_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, status);
                stmt.setInt(2, id);
                int rows = stmt.executeUpdate();
                System.out.println("Rows updated: " + rows);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteComplaint(String role) {
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("employee")) {
            System.out.println("Access denied. Only admin and employee can delete complaints.");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             Scanner sc = new Scanner(System.in)) {

            System.out.print("Enter Complaint ID to delete: ");
            int id = sc.nextInt();

            String sql = "DELETE FROM complaints WHERE complaint_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                int rows = stmt.executeUpdate();
                System.out.println("Rows deleted: " + rows);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        InsertComplaint app = new InsertComplaint();
        Scanner sc = new Scanner(System.in);

        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("Enter your username: ");
            String username = sc.nextLine();
            String role = app.getUserRole(conn, username);
            if (role == null) {
                System.out.println("User not found.");
                return;
            }

            int userId = app.fetchId(conn, "SELECT user_id FROM users WHERE username = ?", username);

            while (true) {
                System.out.println("\n--- Complaint CRUD Menu ---");
                System.out.println("1. Insert Complaint");
                System.out.println("2. Read All Complaints");
                System.out.println("3. Update Complaint Status");
                System.out.println("4. Delete Complaint");
                System.out.println("5. Exit");
                System.out.print("Choose option: ");

                if (!sc.hasNextInt()) {
                    System.out.println("Invalid input.");
                    sc.next();
                    continue;
                }

                int choice = sc.nextInt(); sc.nextLine();

                switch (choice) {
                    case 1:
                        // Select Service Area
                        Map<Integer, Integer> deptOptions = new HashMap<>();
                        Statement deptStmt = conn.createStatement();
                        ResultSet deptRs = deptStmt.executeQuery("SELECT dept_id, dept_name FROM departments");
                        int idx = 1;
                        System.out.println("\nChoose a Service Area:");
                        while (deptRs.next()) {
                            System.out.println(idx + ". " + deptRs.getString("dept_name"));
                            deptOptions.put(idx, deptRs.getInt("dept_id"));
                            idx++;
                        }
                        System.out.print("Enter your choice: ");
                        int deptChoice = sc.nextInt(); sc.nextLine();
                        if (!deptOptions.containsKey(deptChoice)) {
                            System.out.println("Invalid service area.");
                            break;
                        }
                        int deptId = deptOptions.get(deptChoice);

                        // Select Category
                        Map<Integer, Integer> catOptions = new HashMap<>();
                        Statement catStmt = conn.createStatement();
                        ResultSet catRs = catStmt.executeQuery("SELECT category_id, category_name FROM categories");
                        idx = 1;
                        System.out.println("\nSelect Complaint Category:");
                        while (catRs.next()) {
                            System.out.println(idx + ". " + catRs.getString("category_name"));
                            catOptions.put(idx, catRs.getInt("category_id"));
                            idx++;
                        }
                        System.out.print("Enter your choice: ");
                        int catChoice = sc.nextInt(); sc.nextLine();
                        if (!catOptions.containsKey(catChoice)) {
                            System.out.println("Invalid category.");
                            break;
                        }
                        int categoryId = catOptions.get(catChoice);

                        // Description
                        System.out.print("Enter complaint description: ");
                        String desc = sc.nextLine();

                        Complaint complaint = new Complaint("", desc);
                        app.insertComplaint(complaint, userId, deptId, categoryId);
                        break;

                    case 2:
                        app.readComplaints();
                        break;

                    case 3:
                        app.updateComplaintStatus(role);
                        break;

                    case 4:
                        app.deleteComplaint(role);
                        break;

                    case 5:
                        System.out.println("Exiting...");
                        return;

                    default:
                        System.out.println("Invalid choice.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


           
