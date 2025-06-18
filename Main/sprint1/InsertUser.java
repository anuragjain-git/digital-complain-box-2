package sprint1;

import java.sql.*;
import java.util.Scanner;

import sprint1.CollectionsStore;
import sprint1.User;

public class InsertUser {

    public void printUserList() {
        CollectionsStore.userList.clear(); 

        String sql = "SELECT * FROM users";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User u = new User(
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role"),
                        rs.getString("email"),
                        rs.getTimestamp("created_at")
                );
                CollectionsStore.userList.add(u);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("\n--- Users in Memory (Loaded from DB) ---");
        if (CollectionsStore.userList.isEmpty()) {
            System.out.println("No users found.");
        } else {
            for (User user : CollectionsStore.userList) {
                System.out.println("Username: " + user.getUsername());
                System.out.println("Email: " + user.getEmail());
                System.out.println("Role: " + user.getRole());
                System.out.println("Created At: " + user.getCreatedAt());
                System.out.println("------------------------");
            }
        }
    }

    public void insertUser(User user) {
        String sql = "INSERT INTO users (username, password_hash, role, email) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getEmail());
            int rows = stmt.executeUpdate();
            System.out.println(rows + " user(s) inserted successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void readUsers() {
        String sql = "SELECT * FROM users";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- User List ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("user_id"));
                System.out.println("Username: " + rs.getString("username"));
                System.out.println("Role: " + rs.getString("role"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Created At: " + rs.getTimestamp("created_at"));
                System.out.println("------------------------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUser() {
        String sql = "UPDATE users SET username = ?, password_hash = ?, role = ?, email = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             Scanner sc = new Scanner(System.in)) {

            System.out.print("Enter user ID to update: ");
            int id = sc.nextInt();
            sc.nextLine();

            System.out.print("Enter new username: ");
            String username = sc.nextLine();
            System.out.print("Enter new password: ");
            String password = sc.nextLine();
            System.out.print("Enter new role: ");
            String role = sc.nextLine();
            System.out.print("Enter new email: ");
            String email = sc.nextLine();

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            stmt.setString(4, email);
            stmt.setInt(5, id);

            int rows = stmt.executeUpdate();
            System.out.println(rows + " user(s) updated successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUserByUsername() {
        String sql = "DELETE FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             Scanner sc = new Scanner(System.in)) {

            System.out.print("Enter username to delete: ");
            String username = sc.nextLine();

            stmt.setString(1, username);
            int rows = stmt.executeUpdate();
            System.out.println(rows + " user(s) deleted.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User authenticateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role"),
                        rs.getString("email"),
                        rs.getTimestamp("created_at")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        InsertUser app = new InsertUser();
        Scanner sc = new Scanner(System.in);

        User currentUser = null;

        while (currentUser == null) {
            System.out.println("\n--- Choose an Option ---");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.print("Enter choice: ");

            int option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1:
                    System.out.print("Enter username: ");
                    String loginUsername = sc.nextLine();
                    System.out.print("Enter password: ");
                    String loginPassword = sc.nextLine();
                    currentUser = app.authenticateUser(loginUsername, loginPassword);
                    if (currentUser == null) {
                        System.out.println("Invalid credentials. Try again.");
                    }
                    break;
                case 2:
                    System.out.print("Enter username: ");
                    String username = sc.nextLine();
                    System.out.print("Enter password: ");
                    String password = sc.nextLine();
                    System.out.print("Enter role: ");
                    String role = sc.nextLine();
                    System.out.print("Enter email: ");
                    String email = sc.nextLine();
                    User newUser = new User(username, password, role, email);
                    app.insertUser(newUser);
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }

        System.out.println("Welcome, " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");

        while (true) {
            try {
                System.out.println("\n--- User CRUD Menu ---");
                System.out.println("1. Register User");
                System.out.println("2. View All Users");
                System.out.println("3. Update User");
                System.out.println("4. Delete User by Username (Admin Only)");
                System.out.println("5. Exit");
                System.out.println("6. Print User List");
                System.out.print("Choose option: ");

                if (!sc.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a number.");
                    sc.next();
                    continue;
                }

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1:
                        System.out.print("Enter username: ");
                        String username = sc.nextLine();
                        System.out.print("Enter password: ");
                        String password = sc.nextLine();
                        System.out.print("Enter role: ");
                        String role = sc.nextLine();
                        System.out.print("Enter email: ");
                        String email = sc.nextLine();
                        User user = new User(username, password, role, email);
                        app.insertUser(user);
                        break;
                    case 2:
                        app.readUsers();
                        break;
                    case 3:
                        app.updateUser();
                        break;
                    case 4:
                        if (!"admin".equalsIgnoreCase(currentUser.getRole())) {
                            System.out.println("Only admin users can delete a user.");
                        } else {
                            app.deleteUserByUsername();
                        }
                        break;
                    case 5:
                        System.out.println("Exiting...");
                        sc.close();
                        return;
                    case 6:
                        app.printUserList();
                        break;
                    default:
                        System.out.println("Invalid option. Try again.");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                sc.nextLine();
            }
        }
    }
}
