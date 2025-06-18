package sprint1;

import java.sql.Timestamp;

public abstract class BaseEntity {
    protected Timestamp createdAt;

    public BaseEntity() {
        this.createdAt = new Timestamp(System.currentTimeMillis()); // Default: now
    }

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
