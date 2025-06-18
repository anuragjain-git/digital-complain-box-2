package sprint1;

import java.sql.*;
import java.util.Scanner;


public class InsertCategories {

    public void insertCategory(Category category) {
        String sql = "INSERT INTO categories (category_name, description) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection()){
             PreparedStatement stmt = conn.prepareStatement(sql);
        	 stmt.setString(1, category.getCategoryName());
             stmt.setString(2, category.getDescription());

            stmt.executeUpdate();
            System.out.println("Category inserted.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void readCategories() {
        String sql = "SELECT * FROM categories";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n=== Category List ===");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("category_id"));
                System.out.println("Name: " + rs.getString("category_name"));
                System.out.println("Description: " + rs.getString("description"));
                System.out.println("------------------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCategory() {
        String sql = "UPDATE categories SET category_name = ?, description = ? WHERE category_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             Scanner sc = new Scanner(System.in)) {

            System.out.print("Enter category ID to update: ");
            int id = sc.nextInt(); sc.nextLine();
            System.out.print("Enter new name: ");
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

    public void deleteCategory() {
        String sql = "DELETE FROM categories WHERE category_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             Scanner sc = new Scanner(System.in)) {

            System.out.print("Enter category ID to delete: ");
            int id = sc.nextInt();

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            System.out.println(" Rows deleted: " + rows);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        InsertCategories app = new InsertCategories();
        Scanner sc = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("\n--- Category CRUD Menu ---");
                System.out.println("1. Insert Category");
                System.out.println("2. View All Categories");
                System.out.println("3. Update Category");
                System.out.println("4. Delete Category");
                System.out.println("5. Exit");
                System.out.print("Choose option: ");

                if (!sc.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a number.");
                    sc.next();
                    continue;
                }

                int choice = sc.nextInt();
                sc.nextLine(); // consume newline

                switch (choice) {
                    case 1: 
                    	System.out.print("Enter description: ");
                    	String categoryname = sc.nextLine();
                    	System.out.print("Enter status: ");
                    	String description = sc.nextLine();
                    	Category category = new Category(categoryname,description);
                    	app.insertCategory(category); 
                    	break;
                    case 2: app.readCategories(); break;
                    case 3: app.updateCategory(); break;
                    case 4: app.deleteCategory(); break;
                    case 5:
                        System.out.println("Exiting...");
                        sc.close();
                        return;
                    default:
                        System.out.println(" Invalid choice. Try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                sc.nextLine(); // recover from input exception
            }
        }
    }
}

	
