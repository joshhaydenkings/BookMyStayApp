import java.util.*;

/**
 * Book My Stay - Hotel Booking System
 *
 * Use Case 8: Booking History & Reporting
 *
 * Adds:
 * - BookingHistory (stores confirmed reservations)
 * - ReportingService (generates reports)
 *
 * Focus:
 * - List for ordered storage
 * - Audit trail
 * - Read-only reporting
 *
 * @author Josh
 * @version 1.7
 */

// -------------------- RESERVATION --------------------

class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private String roomId;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public String getRoomId() { return roomId; }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
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

// -------------------- BOOKING HISTORY --------------------

class BookingHistory {

    // Maintains insertion order (chronological)
    private List<Reservation> history = new ArrayList<>();

    public void add(Reservation reservation) {
        history.add(reservation);
    }

    public List<Reservation> getAll() {
        return Collections.unmodifiableList(history); // read-only
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

    public void processQueue(BookingQueue queue) {

        System.out.println("\n--- Processing Bookings ---");

        while (!queue.isEmpty()) {

            Reservation request = queue.getNext();
            String type = request.getRoomType();

            if (inventory.getAvailability(type) <= 0) {
                System.out.println("❌ No rooms for " + request.getGuestName());
                continue;
            }

            String roomId = generateRoomId(type);

            while (allocatedRoomIds.contains(roomId)) {
                roomId = generateRoomId(type);
            }

            allocatedRoomIds.add(roomId);
            request.setRoomId(roomId);

            inventory.decrement(type);

            // 🔥 Store in booking history (IMPORTANT)
            history.add(request);

            System.out.println("✅ Confirmed: " + request.getGuestName()
                    + " | Room ID: " + roomId);
        }
    }

    private String generateRoomId(String type) {
        return type.substring(0, 2).toUpperCase() + "-" + (int)(Math.random() * 1000);
    }
}

// -------------------- REPORTING SERVICE --------------------

class BookingReportService {

    private BookingHistory history;

    public BookingReportService(BookingHistory history) {
        this.history = history;
    }

    // Display all bookings
    public void displayAllBookings() {
        System.out.println("\n--- Booking History ---");

        List<Reservation> bookings = history.getAll();

        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (Reservation r : bookings) {
            System.out.println("Reservation: " + r.getReservationId()
                    + " | Guest: " + r.getGuestName()
                    + " | Room Type: " + r.getRoomType()
                    + " | Room ID: " + r.getRoomId());
        }
    }

    // Summary report
    public void generateSummary() {
        System.out.println("\n--- Booking Summary ---");

        Map<String, Integer> countByType = new HashMap<>();

        for (Reservation r : history.getAll()) {
            countByType.put(
                    r.getRoomType(),
                    countByType.getOrDefault(r.getRoomType(), 0) + 1
            );
        }

        for (String type : countByType.keySet()) {
            System.out.println(type + " → " + countByType.get(type) + " bookings");
        }
    }
}

// -------------------- APPLICATION --------------------

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("   Book My Stay - v1.7");
        System.out.println("========================================");

        // Setup
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 2);
        inventory.addRoomType("Suite Room", 1);

        BookingHistory history = new BookingHistory();

        BookingQueue queue = new BookingQueue();
        queue.addRequest(new Reservation("R1", "Alice", "Single Room"));
        queue.addRequest(new Reservation("R2", "Bob", "Suite Room"));
        queue.addRequest(new Reservation("R3", "Charlie", "Single Room"));

        // Process bookings
        BookingService bookingService = new BookingService(inventory, history);
        bookingService.processQueue(queue);

        // Reporting (Admin)
        BookingReportService reportService = new BookingReportService(history);

        reportService.displayAllBookings();
        reportService.generateSummary();

        System.out.println("\nApplication समाप्त.");
    }
}