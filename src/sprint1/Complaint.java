package sprint1;

import java.sql.*;

public class Complaint extends BaseEntity{
    private Integer complaintId;
    private Integer userId;
    private Integer deptId;
    private Integer categoryId;
    private String title;
    private String description;
    private String status;
    private Timestamp updatedAt;

    public Complaint(Integer complaintId, Integer userId, Integer deptId, Integer categoryId,
                     String title, String description, Timestamp createdAt, Timestamp updatedAt) {
    	super(createdAt);
    	this.complaintId = complaintId;
        this.userId = userId;
        this.deptId = deptId;
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.status = "OPEN";
        this.updatedAt = updatedAt;
    }

    public Integer getComplaintId() {
        return complaintId;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public Integer getCategoryId() {
        return categoryId;
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
}