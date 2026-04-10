import java.util.*;

/**
 * Book My Stay - Hotel Booking System
 *
 * Use Case 10: Booking Cancellation & Inventory Rollback
 *
 * Adds:
 * - CancellationService
 * - Stack-based rollback (LIFO)
 * - Inventory restoration
 *
 * @author Josh
 * @version 2.0
 */

// -------------------- RESERVATION --------------------

class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private String roomId;
    private boolean active = true;

    public Reservation(String id, String guest, String type) {
        this.reservationId = id;
        this.guestName = guest;
        this.roomType = type;
    }

    public String getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public String getRoomId() { return roomId; }
    public boolean isActive() { return active; }

    public void setRoomId(String roomId) { this.roomId = roomId; }
    public void cancel() { this.active = false; }
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
        availabilityMap.put(type, getAvailability(type) - 1);
    }

    public void increment(String type) {
        availabilityMap.put(type, getAvailability(type) + 1);
    }

    public void display() {
        System.out.println("\n--- Inventory ---");
        for (String type : availabilityMap.keySet()) {
            System.out.println(type + " → " + availabilityMap.get(type));
        }
    }
}

// -------------------- BOOKING HISTORY --------------------

class BookingHistory {

    private Map<String, Reservation> history = new HashMap<>();

    public void add(Reservation r) {
        history.put(r.getReservationId(), r);
    }

    public Reservation get(String id) {
        return history.get(id);
    }

    public void display() {
        System.out.println("\n--- Booking History ---");
        for (Reservation r : history.values()) {
            System.out.println(r.getReservationId() + " | "
                    + r.getGuestName() + " | "
                    + r.getRoomType() + " | "
                    + r.getRoomId() + " | Active: " + r.isActive());
        }
    }
}

// -------------------- BOOKING SERVICE --------------------

class BookingService {

    private RoomInventory inventory;
    private BookingHistory history;
    private Set<String> allocatedRoomIds = new HashSet<>();

    public BookingService(RoomInventory inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }

    public void book(Reservation r) {

        if (inventory.getAvailability(r.getRoomType()) <= 0) {
            System.out.println("❌ No rooms available for " + r.getGuestName());
            return;
        }

        String roomId = generateRoomId(r.getRoomType());

        while (allocatedRoomIds.contains(roomId)) {
            roomId = generateRoomId(r.getRoomType());
        }

        allocatedRoomIds.add(roomId);
        r.setRoomId(roomId);

        inventory.decrement(r.getRoomType());
        history.add(r);

        System.out.println("✅ Booked: " + r.getGuestName()
                + " | Room ID: " + roomId);
    }

    private String generateRoomId(String type) {
        return type.substring(0, 2).toUpperCase() + "-" + (int)(Math.random() * 1000);
    }
}

// -------------------- CANCELLATION SERVICE --------------------

class CancellationService {

    private RoomInventory inventory;
    private BookingHistory history;

    // Stack for rollback (LIFO)
    private Stack<String> rollbackStack = new Stack<>();

    public CancellationService(RoomInventory inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }

    public void cancel(String reservationId) {

        System.out.println("\nProcessing cancellation for " + reservationId);

        Reservation r = history.get(reservationId);

        // Validation
        if (r == null) {
            System.out.println("❌ Reservation not found.");
            return;
        }

        if (!r.isActive()) {
            System.out.println("❌ Reservation already cancelled.");
            return;
        }

        // Push to rollback stack
        rollbackStack.push(r.getRoomId());

        // Restore inventory
        inventory.increment(r.getRoomType());

        // Mark reservation cancelled
        r.cancel();

        System.out.println("✅ Cancellation successful for " + r.getGuestName());
        System.out.println("Released Room ID: " + r.getRoomId());
    }

    public void showRollbackStack() {
        System.out.println("\n--- Rollback Stack (LIFO) ---");
        System.out.println(rollbackStack);
    }
}

// -------------------- APPLICATION --------------------

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("   Book My Stay - v2.0");
        System.out.println("========================================");

        // Setup
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 2);

        BookingHistory history = new BookingHistory();

        BookingService bookingService = new BookingService(inventory, history);

        // Bookings
        Reservation r1 = new Reservation("R1", "Alice", "Single Room");
        Reservation r2 = new Reservation("R2", "Bob", "Single Room");

        bookingService.book(r1);
        bookingService.book(r2);

        // Cancellation
        CancellationService cancelService = new CancellationService(inventory, history);

        cancelService.cancel("R2"); // valid
        cancelService.cancel("R2"); // duplicate cancel
        cancelService.cancel("R3"); // invalid

        // Show results
        history.display();
        inventory.display();
        cancelService.showRollbackStack();

        System.out.println("\nApplication समाप्त.");
    }
}