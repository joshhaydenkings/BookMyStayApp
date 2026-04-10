import java.io.*;
import java.util.*;

/**
 * Book My Stay - Hotel Booking System
 *
 * Use Case 12: Data Persistence & System Recovery
 *
 * Adds:
 * - Serialization & Deserialization
 * - File-based persistence
 * - System recovery after restart
 *
 * @author Josh
 * @version 2.2
 */

// -------------------- RESERVATION --------------------

class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

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

// -------------------- INVENTORY --------------------

class RoomInventory implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Integer> availability = new HashMap<>();

    public void addRoomType(String type, int count) {
        availability.put(type, count);
    }

    public int getAvailability(String type) {
        return availability.getOrDefault(type, 0);
    }

    public void decrement(String type) {
        availability.put(type, getAvailability(type) - 1);
    }

    public Map<String, Integer> getAll() {
        return availability;
    }

    public void setAll(Map<String, Integer> data) {
        this.availability = data;
    }

    public void display() {
        System.out.println("\n--- Inventory ---");
        for (String type : availability.keySet()) {
            System.out.println(type + " → " + availability.get(type));
        }
    }
}

// -------------------- BOOKING HISTORY --------------------

class BookingHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Reservation> history = new ArrayList<>();

    public void add(Reservation r) {
        history.add(r);
    }

    public List<Reservation> getAll() {
        return history;
    }

    public void setAll(List<Reservation> data) {
        this.history = data;
    }

    public void display() {
        System.out.println("\n--- Booking History ---");
        for (Reservation r : history) {
            System.out.println(r.getId() + " | " + r.getGuestName()
                    + " | " + r.getRoomType());
        }
    }
}

// -------------------- PERSISTENCE SERVICE --------------------

class PersistenceService {

    private static final String FILE_NAME = "booking_data.ser";

    // Save state
    public void save(RoomInventory inventory, BookingHistory history) {

        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            oos.writeObject(inventory);
            oos.writeObject(history);

            System.out.println("\n💾 Data saved successfully.");

        } catch (IOException e) {
            System.out.println("❌ Error saving data: " + e.getMessage());
        }
    }

    // Load state
    public void load(RoomInventory inventory, BookingHistory history) {

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            System.out.println("\n⚠ No saved data found. Starting fresh.");
            return;
        }

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            RoomInventory savedInventory = (RoomInventory) ois.readObject();
            BookingHistory savedHistory = (BookingHistory) ois.readObject();

            inventory.setAll(savedInventory.getAll());
            history.setAll(savedHistory.getAll());

            System.out.println("\n🔄 Data restored successfully.");

        } catch (Exception e) {
            System.out.println("❌ Error loading data. Starting fresh.");
        }
    }
}

// -------------------- APPLICATION --------------------

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("   Book My Stay - v2.2 (Persistence)");
        System.out.println("========================================");

        RoomInventory inventory = new RoomInventory();
        BookingHistory history = new BookingHistory();

        PersistenceService persistence = new PersistenceService();

        // 🔄 Load previous state
        persistence.load(inventory, history);

        // If fresh start, initialize inventory
        if (inventory.getAll().isEmpty()) {
            inventory.addRoomType("Single Room", 2);
            inventory.addRoomType("Suite Room", 1);
        }

        // Simulate booking
        Reservation r1 = new Reservation("R1", "Alice", "Single Room");

        if (inventory.getAvailability(r1.getRoomType()) > 0) {
            inventory.decrement(r1.getRoomType());
            history.add(r1);
            System.out.println("✅ Booking done for " + r1.getGuestName());
        }

        // Display current state
        inventory.display();
        history.display();

        // 💾 Save state before exit
        persistence.save(inventory, history);

        System.out.println("\nApplication समाप्त.");
    }
}