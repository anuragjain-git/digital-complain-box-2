package sprint1;

import java.sql.*;

public class Response extends BaseEntity{
    private String comment;
    
    public Response(String comment) {
    	super();
        this.comment = comment;
    }

    public Response(String comment, Timestamp createdAt) {
    	super(createdAt);
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }
}
