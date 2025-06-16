package sprint1;

import java.sql.*;

public class User extends BaseEntity{
    private Integer id;
    private String username;
    private String passwordHash;
    private String role;
    private String email;

    public User(Integer id, String username, String passwordHash, String role, String email, Timestamp createdAt) {
    	super(createdAt);
    	this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.email = email;
    }

    public Integer getId() {
        return id;
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
}