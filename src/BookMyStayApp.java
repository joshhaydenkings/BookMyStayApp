import java.util.*;

/**
 * Book My Stay - Hotel Booking System
 *
 * Use Case 6: Reservation Confirmation & Room Allocation
 *
 * This version processes booking requests from a queue,
 * assigns unique room IDs, updates inventory, and prevents
 * double-booking using Set and Map.
 *
 * @author Josh
 * @version 1.5
 */

// -------------------- DOMAIN MODEL --------------------

abstract class Room {
    private String type;
    private int beds;
    private double price;

    public Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }

    public String getType() { return type; }
    public int getBeds() { return beds; }
    public double getPrice() { return price; }
}

class SingleRoom extends Room {
    public SingleRoom() { super("Single Room", 1, 1000.0); }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super("Double Room", 2, 1800.0); }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super("Suite Room", 3, 3500.0); }
}

// -------------------- INVENTORY --------------------

class RoomInventory {
    private Map<String, Integer> availabilityMap = new HashMap<>();

    public void addRoomType(String type, int count) {
        availabilityMap.put(type, count);
    }

    public int getAvailability(String type) {
        return availabilityMap.getOrDefault(type, 0);
    }

    public void decrement(String type) {
        int current = getAvailability(type);
        if (current > 0) {
            availabilityMap.put(type, current - 1);
        }
    }

    public void display() {
        System.out.println("\n--- Inventory ---");
        for (String type : availabilityMap.keySet()) {
            System.out.println(type + " → " + availabilityMap.get(type));
        }
    }
}

// -------------------- RESERVATION --------------------

class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

// -------------------- QUEUE --------------------

class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.offer(r);
    }

    public Reservation getNext() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

// -------------------- BOOKING SERVICE --------------------

class BookingService {

    private RoomInventory inventory;

    // Track all allocated room IDs (global uniqueness)
    private Set<String> allocatedRoomIds = new HashSet<>();

    // Track room-type → assigned room IDs
    private Map<String, Set<String>> roomAllocations = new HashMap<>();

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    // Process queue (FIFO)
    public void processQueue(BookingQueue queue) {

        System.out.println("\n--- Processing Booking Requests ---");

        while (!queue.isEmpty()) {

            Reservation request = queue.getNext();
            String type = request.getRoomType();

            System.out.println("\nProcessing request for " + request.getGuestName());

            int available = inventory.getAvailability(type);

            // Check availability
            if (available <= 0) {
                System.out.println("❌ No rooms available for " + type);
                continue;
            }

            // Generate unique room ID
            String roomId = generateRoomId(type);

            // Ensure uniqueness using Set
            while (allocatedRoomIds.contains(roomId)) {
                roomId = generateRoomId(type);
            }

            // Allocate room
            allocatedRoomIds.add(roomId);

            roomAllocations
                    .computeIfAbsent(type, k -> new HashSet<>())
                    .add(roomId);

            // Update inventory immediately (atomic step)
            inventory.decrement(type);

            // Confirmation
            System.out.println("✅ Booking Confirmed!");
            System.out.println("Guest: " + request.getGuestName());
            System.out.println("Room Type: " + type);
            System.out.println("Assigned Room ID: " + roomId);
        }
    }

    // Simple unique ID generator
    private String generateRoomId(String type) {
        return type.substring(0, 2).toUpperCase() + "-" + (int)(Math.random() * 1000);
    }

    // Display allocated rooms
    public void displayAllocations() {
        System.out.println("\n--- Allocated Rooms ---");

        for (String type : roomAllocations.keySet()) {
            System.out.println(type + " → " + roomAllocations.get(type));
        }
    }
}

// -------------------- APPLICATION --------------------

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("   Welcome to Book My Stay");
        System.out.println("   Version: v1.5");
        System.out.println("========================================");

        // Initialize Inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 2);
        inventory.addRoomType("Double Room", 1);
        inventory.addRoomType("Suite Room", 1);

        // Initialize Queue
        BookingQueue queue = new BookingQueue();

        queue.addRequest(new Reservation("Alice", "Single Room"));
        queue.addRequest(new Reservation("Bob", "Suite Room"));
        queue.addRequest(new Reservation("Charlie", "Single Room"));
        queue.addRequest(new Reservation("David", "Single Room")); // should fail

        // Process Bookings
        BookingService bookingService = new BookingService(inventory);
        bookingService.processQueue(queue);

        // Show Results
        bookingService.displayAllocations();
        inventory.display();

        System.out.println("\nApplication समाप्त.");
    }
}