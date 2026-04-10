import java.util.*;

/**
 * Book My Stay - Hotel Booking System
 *
 * Use Case 9: Error Handling & Validation
 *
 * Adds:
 * - Input validation
 * - Custom exceptions
 * - Fail-fast design
 * - Safe state handling
 *
 * @author Josh
 * @version 1.8
 */

// -------------------- CUSTOM EXCEPTIONS --------------------

class InvalidRoomTypeException extends Exception {
    public InvalidRoomTypeException(String message) {
        super(message);
    }
}

class NoAvailabilityException extends Exception {
    public NoAvailabilityException(String message) {
        super(message);
    }
}

// -------------------- RESERVATION --------------------

class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

// -------------------- INVENTORY --------------------

class RoomInventory {
    private Map<String, Integer> availabilityMap = new HashMap<>();

    public void addRoomType(String type, int count) {
        availabilityMap.put(type, count);
    }

    public boolean isValidRoomType(String type) {
        return availabilityMap.containsKey(type);
    }

    public int getAvailability(String type) {
        return availabilityMap.getOrDefault(type, 0);
    }

    public void decrement(String type) throws NoAvailabilityException {
        int current = getAvailability(type);

        if (current <= 0) {
            throw new NoAvailabilityException("No rooms available for: " + type);
        }

        availabilityMap.put(type, current - 1);
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

// -------------------- VALIDATOR --------------------

class BookingValidator {

    private RoomInventory inventory;

    public BookingValidator(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void validate(Reservation r)
            throws InvalidRoomTypeException, NoAvailabilityException {

        // Validate room type
        if (!inventory.isValidRoomType(r.getRoomType())) {
            throw new InvalidRoomTypeException(
                    "Invalid room type: " + r.getRoomType()
            );
        }

        // Validate availability
        if (inventory.getAvailability(r.getRoomType()) <= 0) {
            throw new NoAvailabilityException(
                    "No availability for room type: " + r.getRoomType()
            );
        }
    }
}

// -------------------- BOOKING SERVICE --------------------

class BookingService {

    private RoomInventory inventory;
    private BookingValidator validator;

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
        this.validator = new BookingValidator(inventory);
    }

    public void processQueue(BookingQueue queue) {

        System.out.println("\n--- Processing Bookings with Validation ---");

        while (!queue.isEmpty()) {

            Reservation request = queue.getNext();

            try {
                // 🔥 Fail-fast validation
                validator.validate(request);

                // If valid → proceed
                inventory.decrement(request.getRoomType());

                System.out.println("✅ Booking confirmed for "
                        + request.getGuestName()
                        + " (" + request.getRoomType() + ")");

            } catch (InvalidRoomTypeException e) {
                System.out.println("❌ ERROR: " + e.getMessage());

            } catch (NoAvailabilityException e) {
                System.out.println("❌ ERROR: " + e.getMessage());

            } catch (Exception e) {
                System.out.println("❌ Unexpected error: " + e.getMessage());
            }
        }
    }
}

// -------------------- APPLICATION --------------------

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("   Book My Stay - v1.8");
        System.out.println("========================================");

        // Setup inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 1);
        inventory.addRoomType("Suite Room", 0);

        // Queue with valid + invalid requests
        BookingQueue queue = new BookingQueue();

        queue.addRequest(new Reservation("R1", "Alice", "Single Room")); // valid
        queue.addRequest(new Reservation("R2", "Bob", "Suite Room"));   // no availability
        queue.addRequest(new Reservation("R3", "Charlie", "Deluxe"));   // invalid type
        queue.addRequest(new Reservation("R4", "David", "Single Room")); // now unavailable

        // Process
        BookingService bookingService = new BookingService(inventory);
        bookingService.processQueue(queue);

        System.out.println("\nApplication continues running safely.");
    }
}