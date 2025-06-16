package CRUD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import digital_complain_box.Category;

public class InsertCategories {
	public void insertCategory(Category category) {
        String sql = "INSERT INTO categories (category_name, description) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category.getCategoryName());
            stmt.setString(2, category.getDescription());

            stmt.executeUpdate();
            System.out.println("Category inserted.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
