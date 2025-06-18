package sprint1;

import java.util.*;

public class ConcurrentTest {
    public static void main(String[] args) {
    	Scanner sc = new Scanner(System.in);
    	
    	System.out.println("Enter");
    	
        Thread t1 = new Thread(new ComplaintSubmitter("anuragjain", "IT", "Software", "Website broken", "Need Help with software"));
        Thread t2 = new Thread(new ComplaintSubmitter("anushka", "Finance", "Payroll", "Salary issue", "Salary not credited"));
        Thread t3 = new Thread(new ComplaintSubmitter("aviral", "Finance", "Software", "Software not working", "Screen flickering"));
        Thread t4 = new Thread(new ComplaintSubmitter("anuragjain", "IT", "Internet", "Wi-Fi not working", "Should be blocked"));

        // Start all threads
        t1.start();
        t2.start();
        t3.start();
        t4.start();
    	


        // Wait for all to finish
        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("All threads completed.");
    }
}