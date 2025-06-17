package sprint1;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class InsertComplaint {

    /* ---------- shared Scanner so we don’t close System.in ---------- */
    private static final Scanner sc = new Scanner(System.in);

    /* ---------- 1. manual single insert ---------- */
    public void insertComplaint() {
        ComplaintDao dao = new ComplaintDao();

        try {
            System.out.print("Username: ");
            String username = sc.nextLine();
            int userId = dao.fetchId(
                    "SELECT user_id FROM users WHERE username=?", username);
            if (userId == -1) {
                System.out.println("❌ User not found!");
                return;
            }

            System.out.print("Department name: ");
            String dept = sc.nextLine();
            int deptId = dao.fetchId(
                    "SELECT dept_id FROM departments WHERE dept_name=?", dept);
            if (deptId == -1) {
                System.out.println("❌ Department not found!");
                return;
            }

            System.out.print("Category name: ");
            String cat = sc.nextLine();
            int catId = dao.fetchId(
                    "SELECT category_id FROM categories WHERE category_name=?", cat);
            if (catId == -1) {
                System.out.println("❌ Category not found!");
                return;
            }

            System.out.print("Title: ");
            String title = sc.nextLine();
            System.out.print("Description: ");
            String desc = sc.nextLine();

            int id = dao.insert(userId, deptId, catId, title, desc);
            System.out.println("✅ Inserted with ID: " + id);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* ---------- 2. multi‑thread simulation ---------- */
    public void simulateConcurrentSubmissions() {
        try {
            ComplaintDao dao = new ComplaintDao();
            List<Integer> userIds = dao.listIds("users", "user_id");
            List<Integer> deptIds = dao.listIds("departments", "dept_id");
            List<Integer> catIds = dao.listIds("categories", "category_id");

            if (userIds.isEmpty() || deptIds.isEmpty() || catIds.isEmpty()) {
                System.out.println("⚠️  Insert data into users/departments/categories first!");
                return;
            }

            List<Thread> threads = new ArrayList<>();

            for (int i = 1; i <= 5; i++) {
                int userId = pick(userIds);
                int deptId = pick(deptIds);
                int catId = pick(catIds);

                Thread t = new ComplaintSubmissionThread(
                        userId, deptId, catId,
                        "Thread Title " + i,
                        "Threaded Description " + i);
                t.setName("UserThread-" + i);
                t.start();
                threads.add(t);
            }

            /* wait until every thread finishes */
            for (Thread t : threads) t.join();

            System.out.println("✅ All simulated submissions finished.");

        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static int pick(List<Integer> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    /* ---------- 3. CLI menu ---------- */
    public static void main(String[] args) {
        InsertComplaint app = new InsertComplaint();

        while (true) {
            System.out.println("\n--- Complaint CLI ---");
            System.out.println("1. Insert Complaint (manual)");
            System.out.println("2. Simulate Concurrent Complaints");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            if (!sc.hasNextInt()) {
                sc.next();
                continue;
            }
            int choice = sc.nextInt();
            sc.nextLine();   // clear newline

            switch (choice) {
                case 1 -> app.insertComplaint();
                case 2 -> app.simulateConcurrentSubmissions();
                case 3 -> {
                    System.out.println("👋 Bye!");
                    return;
                }
                default -> System.out.println("⚠️ Invalid choice.");
            }
        }
    }
}
