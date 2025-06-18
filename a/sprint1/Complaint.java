package sprint1;

import java.sql.Timestamp;

public class Complaint extends BaseEntity {
    private String title;
    private String description;
    private String status;
    private Timestamp updatedAt;

    // Constructor for creating a new complaint (createdAt = now)
    public Complaint(String title, String description) {
        super();  // sets createdAt to now
        this.title = title;
        this.description = description;
        this.status = "OPEN";
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    // Constructor for loading complaint from DB
    public Complaint(String title, String description, Timestamp createdAt, Timestamp updatedAt) {
        super(createdAt);
        this.title = title;
        this.description = description;
        this.updatedAt = updatedAt;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
