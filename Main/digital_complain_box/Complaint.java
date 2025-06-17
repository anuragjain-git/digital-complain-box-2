package digital_complain_box;

import java.sql.*;

public class Complaint extends BaseEntity{
    private String title;
    private String description;
    private String status;
    private Timestamp updatedAt;

    public Complaint(String title, String description, Timestamp createdAt, Timestamp updatedAt) {
    	super(createdAt);
        this.title = title;
        this.description = description;
        this.status = "OPEN";
        this.setUpdatedAt(updatedAt);
    }

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

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}
}