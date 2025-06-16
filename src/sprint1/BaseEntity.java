package sprint1;

import java.sql.Timestamp;

public abstract class BaseEntity {
    protected Timestamp createdAt;

    public BaseEntity(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}