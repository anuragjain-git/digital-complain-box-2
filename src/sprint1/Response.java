package sprint1;

import java.sql.*;

public class Response extends BaseEntity{
    private Integer responseId;
    private Integer complaintId;
    private Integer userId;
    private String comment;

    public Response(Integer responseId, Integer complaintId, Integer userId, String comment, Timestamp createdAt) {
    	super(createdAt);
    	this.responseId = responseId;
        this.complaintId = complaintId;
        this.userId = userId;
        this.comment = comment;
    }

    public Integer getResponseId() {
        return responseId;
    }

    public Integer getComplaintId() {
        return complaintId;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getComment() {
        return comment;
    }
}
