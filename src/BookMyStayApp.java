import java.util.*;

/**
 * Book My Stay - Hotel Booking System
 *
 * Use Case 11: Concurrent Booking Simulation (Thread Safety)
 *
 * Demonstrates:
 * - Multi-threading
 * - Race condition prevention
 * - Synchronized critical sections
 * - Thread-safe booking
 *
 * @author Josh
 * @version 2.1
 */

// -------------------- RESERVATION --------------------

class Reservation {
    private String id;
    private String guestName;
    private String roomType;

    public Reservation(String id, String guestName, String roomType) {
        this.id = id;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getId() { return id; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

// -------------------- THREAD-SAFE INVENTORY --------------------

class RoomInventory {

    private Map<String, Integer> availability = new HashMap<>();

    public synchronized void addRoomType(String type, int count) {
        availability.put(type, count);
    }

    public synchronized int getAvailability(String type) {
        return availability.getOrDefault(type, 0);
    }

    // 🔥 Critical section (synchronized)
    public synchronized boolean allocate(String type) {

        int current = getAvailability(type);

        if (current <= 0) {
            return false;
        }

        // Simulate delay (to expose race condition if not synchronized)
        try { Thread.sleep(50); } catch (InterruptedException e) {}

        availability.put(type, current - 1);
        return true;
    }

    public synchronized void display() {
        System.out.println("\n--- Final Inventory ---");
        for (String type : availability.keySet()) {
            System.out.println(type + " → " + availability.get(type));
        }
    }
}

// -------------------- THREAD-SAFE QUEUE --------------------

class BookingQueue {

    private Queue<Reservation> queue = new LinkedList<>();

    public synchronized void addRequest(Reservation r) {
        queue.offer(r);
    }

    public synchronized Reservation getNext() {
        return queue.poll();
    }
}

// -------------------- CONCURRENT BOOKING PROCESSOR --------------------

class BookingProcessor implements Runnable {

    private BookingQueue queue;
    private RoomInventory inventory;

    public BookingProcessor(BookingQueue queue, RoomInventory inventory) {
        this.queue = queue;
        this.inventory = inventory;
    }

    @Override
    public void run() {

        while (true) {

            Reservation r;

            // 🔥 Critical section for queue access
            synchronized (queue) {
                r = queue.getNext();
            }

            if (r == null) break;

            boolean success;

            // 🔥 Critical section for allocation
            synchronized (inventory) {
                success = inventory.allocate(r.getRoomType());
            }

            if (success) {
                System.out.println(Thread.currentThread().getName()
                        + " ✅ Booked for " + r.getGuestName());
            } else {
                System.out.println(Thread.currentThread().getName()
                        + " ❌ Failed for " + r.getGuestName());
            }
        }
    }
}

// -------------------- APPLICATION --------------------

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("   Book My Stay - v2.1 (Concurrent)");
        System.out.println("========================================");

        // Shared Inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 2);

        // Shared Queue
        BookingQueue queue = new BookingQueue();

        // Simulate multiple requests
        queue.addRequest(new Reservation("R1", "Alice", "Single Room"));
        queue.addRequest(new Reservation("R2", "Bob", "Single Room"));
        queue.addRequest(new Reservation("R3", "Charlie", "Single Room"));
        queue.addRequest(new Reservation("R4", "David", "Single Room"));

        // Create multiple threads (simulating concurrent users)
        Thread t1 = new Thread(new BookingProcessor(queue, inventory), "Thread-1");
        Thread t2 = new Thread(new BookingProcessor(queue, inventory), "Thread-2");
        Thread t3 = new Thread(new BookingProcessor(queue, inventory), "Thread-3");

        // Start threads
        t1.start();
        t2.start();
        t3.start();

        // Wait for completion
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Final state
        inventory.display();

        System.out.println("\nApplication समाप्त.");
    }
}