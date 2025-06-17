package sprint1;

import java.util.Scanner;
import java.sql.*;

import digital_complain_box.Department;


public class InsertDepartment {

    public void insertDepartment(Department department) {
        String sql = "INSERT INTO departments (dept_name, description) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

               stmt.setString(1, department.getDeptName());
               stmt.setString(2, department.getDescription());

               stmt.executeUpdate();
               System.out.println("Department inserted.");

           } catch (SQLException e) {
               e.printStackTrace();
           }
    }

    public void readDepartments() {
        String sql = "SELECT * FROM departments";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n=== Department List ===");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("dept_id"));
                System.out.println("Name: " + rs.getString("dept_name"));
                System.out.println("Description: " + rs.getString("description"));
                System.out.println("------------------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateDepartment() {
        String sql = "UPDATE departments SET dept_name = ?, description = ? WHERE dept_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             Scanner sc = new Scanner(System.in)) {

            System.out.print("Enter department ID to update: ");
            int id = sc.nextInt(); sc.nextLine();

            System.out.print("Enter new department name: ");
            String name = sc.nextLine();

            System.out.print("Enter new description: ");
            String desc = sc.nextLine();

            stmt.setString(1, name);
            stmt.setString(2, desc);
            stmt.setInt(3, id);

            int rows = stmt.executeUpdate();
            System.out.println("Rows updated: " + rows);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteDepartment() {
        String sql = "DELETE FROM departments WHERE dept_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             Scanner sc = new Scanner(System.in)) {

            System.out.print("Enter department ID to delete: ");
            int id = sc.nextInt();

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            System.out.println("Rows deleted: " + rows);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        InsertDepartment app = new InsertDepartment();
        Scanner sc = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("\n--- Department CRUD Menu ---");
                System.out.println("1. Insert Department");
                System.out.println("2. View Departments");
                System.out.println("3. Update Department");
                System.out.println("4. Delete Department");
                System.out.println("5. Exit");
                System.out.print("Choose option: ");

                if (!sc.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a number.");
                    sc.next();
                    continue;
                }

                int choice = sc.nextInt(); sc.nextLine();

                switch (choice) {
                    case 1: 
                    	Department dept1 = new Department(null,"Tech", "Technical related");
                    	app.insertDepartment(dept1);
                    	break;
                    case 2: app.readDepartments(); break;
                    case 3: app.updateDepartment(); break;
                    case 4: app.deleteDepartment(); break;
                    case 5: 
                        System.out.println("Exiting...");
                        sc.close();
                        return;
                    default: 
                        System.out.println("Invalid choice.");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                sc.nextLine(); // recover input
            }
        }
    }
}