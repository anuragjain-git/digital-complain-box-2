package sprint1;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//("anuragjain", "IT", "Software", "Website broken", "Need Help with software"));
//("anushka", "Finance", "Payroll", "Salary issue", "Salary not credited"));
//("aviral", "Finance", "Software", "Software not working", "Screen flickering"));
//("anuragjain", "IT", "Internet", "Wi-Fi not working", "Should be blocked"));

public class ConcurrentTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number of complaints to simulate: ");
        int count = sc.nextInt();
        sc.nextLine(); // Consume newline

        // Create a thread pool with fixed number of threads (can be tuned)
        ExecutorService executor = Executors.newFixedThreadPool(2); // or count, depending on use case

        for (int i = 0; i < count; i++) {
            System.out.println("\nComplaint " + (i + 1));
            System.out.print("Username: ");
            String username = sc.nextLine();
            System.out.print("Department: ");
            String dept = sc.nextLine();
            System.out.print("Category: ");
            String category = sc.nextLine();
            System.out.print("Title: ");
            String title = sc.nextLine();
            System.out.print("Description: ");
            String desc = sc.nextLine();

            // Submit task to thread pool
            executor.execute(new ComplaintSubmitter(username, dept, category, title, desc));
        }

        // Gracefully shut down the executor
        executor.shutdown();
        System.out.println("\nAll complaint tasks submitted to thread pool.");
    }
}
