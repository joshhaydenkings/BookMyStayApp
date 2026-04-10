import java.util.*;

/**
 * Book My Stay - Hotel Booking System
 *
 * Use Case 7: Add-On Service Selection
 *
 * Adds optional services (like WiFi, Breakfast) to existing reservations
 * without modifying booking or inventory logic.
 *
 * Demonstrates:
 * - Map + List (One-to-Many relationship)
 * - Composition over inheritance
 * - Cost aggregation
 *
 * @author Josh
 * @version 1.6
 */

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
    private Set<String> allocatedRoomIds = new HashSet<>();
    private Map<String, String> reservationToRoom = new HashMap<>();

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void processQueue(BookingQueue queue) {

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
            reservationToRoom.put(request.getReservationId(), roomId);

            inventory.decrement(type);

            System.out.println("✅ Booking Confirmed: " + request.getGuestName()
                    + " | Room ID: " + roomId);
        }
    }

    private String generateRoomId(String type) {
        return type.substring(0, 2).toUpperCase() + "-" + (int)(Math.random() * 1000);
    }
}

// -------------------- ADD-ON SERVICE --------------------

class AddOnService {
    private String name;
    private double price;

    public AddOnService(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
}

// -------------------- ADD-ON SERVICE MANAGER --------------------

class AddOnServiceManager {

    // reservationId → list of services
    private Map<String, List<AddOnService>> serviceMap = new HashMap<>();

    // Add service to a reservation
    public void addService(String reservationId, AddOnService service) {
        serviceMap
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);

        System.out.println("Service added: " + service.getName()
                + " → Reservation: " + reservationId);
    }

    // Calculate total cost
    public double calculateTotal(String reservationId) {
        List<AddOnService> services = serviceMap.getOrDefault(reservationId, new ArrayList<>());

        double total = 0;
        for (AddOnService s : services) {
            total += s.getPrice();
        }
        return total;
    }

    // Display services
    public void displayServices(String reservationId) {
        System.out.println("\n--- Services for Reservation " + reservationId + " ---");

        List<AddOnService> services = serviceMap.get(reservationId);

        if (services == null || services.isEmpty()) {
            System.out.println("No services selected.");
            return;
        }

        for (AddOnService s : services) {
            System.out.println(s.getName() + " → ₹" + s.getPrice());
        }

        System.out.println("Total Add-On Cost: ₹" + calculateTotal(reservationId));
    }
}

// -------------------- APPLICATION --------------------

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("   Book My Stay - v1.6");
        System.out.println("========================================");

        // Inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 2);

        // Queue
        BookingQueue queue = new BookingQueue();

        Reservation r1 = new Reservation("R1", "Alice", "Single Room");
        Reservation r2 = new Reservation("R2", "Bob", "Single Room");

        queue.addRequest(r1);
        queue.addRequest(r2);

        // Booking
        BookingService bookingService = new BookingService(inventory);
        bookingService.processQueue(queue);

        // Add-On Services
        AddOnServiceManager serviceManager = new AddOnServiceManager();

        serviceManager.addService("R1", new AddOnService("Breakfast", 200));
        serviceManager.addService("R1", new AddOnService("WiFi", 100));
        serviceManager.addService("R2", new AddOnService("Airport Pickup", 500));

        // Display Services
        serviceManager.displayServices("R1");
        serviceManager.displayServices("R2");

        System.out.println("\nApplication समाप्त.");
    }
}