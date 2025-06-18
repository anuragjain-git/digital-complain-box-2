package sprint1;

import java.sql.*;

public class User extends BaseEntity{
    private String username;
    private String passwordHash;
    private String role;
    private String email;

    public User(String username, String passwordHash, String role, String email) {
        super(); // assigns current timestamp
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.email = email;
    }

    public User(String username, String passwordHash, String role, String email, Timestamp createdAt) {
        super(createdAt); // for data read from DB
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }
    
    public Timestamp getUpdatedAt() {
        return createdAt;
    }
}