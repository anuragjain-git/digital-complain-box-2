package CRUD;

import java.sql.*;

import digital_complain_box.User;

public class InsertUser {

    public static void insertUser(User user) {
        String sql = "INSERT INTO users (username, password_hash, role, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
        	PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getEmail());
            int rows = stmt.executeUpdate();
            System.out.println(rows + " user(s) updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
    	User user1 = new User(null,"anuragjain", "123", "USER", "anurag@cognizant.com", new Timestamp(System.currentTimeMillis()));
    	InsertUser.insertUser(user1);
    	User user2 = new User(null, "xyz", "123", "USER", "xyz@cognizant.com", new Timestamp(System.currentTimeMillis()));
    	InsertUser.insertUser(user2);
    }
}
