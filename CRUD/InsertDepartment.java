package CRUD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import model.Department;
import util.DatabaseUtil;

public class InsertDepartment {
	public void insertDepartment(Department department) {
        String sql = "INSERT INTO departments (dept_name, description) VALUES (?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, department.getDeptName());
            stmt.setString(2, department.getDescription());

            stmt.executeUpdate();
            System.out.println("✅ Department inserted.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
