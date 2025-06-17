package sprint1;

/** One thread = one simulated complaint submission. */
public class ComplaintSubmissionThread extends Thread {

    private final int userId;
    private final int deptId;
    private final int categoryId;
    private final String title;
    private final String description;

    public ComplaintSubmissionThread(int userId,
                                     int deptId,
                                     int categoryId,
                                     String title,
                                     String description) {
        this.userId     = userId;
        this.deptId     = deptId;
        this.categoryId = categoryId;
        this.title      = title;
        this.description= description;
    }

    @Override
    public void run() {
        ComplaintDao dao = new ComplaintDao();
        try {
            int id = dao.insert(userId, deptId, categoryId, title, description);
            System.out.printf("✅ %s inserted complaint #%d%n", getName(), id);
        } catch (Exception e) {
            System.out.printf("❌ %s failed: %s%n", getName(), e.getMessage());
        }
    }
}
