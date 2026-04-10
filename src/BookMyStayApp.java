import java.util.HashMap;
import java.util.Map;

// Abstract Class
abstract class Room {
    private String type;
    private int beds;
    private double price;

    public Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public int getBeds() {
        return beds;
    }

    public double getPrice() {
        return price;
    }

    public abstract void displayDetails();
}

// Concrete Room Types
class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 1000.0);
    }

    public void displayDetails() {
        System.out.println("Room Type: " + getType());
        System.out.println("Beds: " + getBeds());
        System.out.println("Price: ₹" + getPrice());
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 1800.0);
    }

    public void displayDetails() {
        System.out.println("Room Type: " + getType());
        System.out.println("Beds: " + getBeds());
        System.out.println("Price: ₹" + getPrice());
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 3500.0);
    }

    public void displayDetails() {
        System.out.println("Room Type: " + getType());
        System.out.println("Beds: " + getBeds());
        System.out.println("Price: ₹" + getPrice());
    }
}

// -------------------- INVENTORY MANAGEMENT --------------------

class RoomInventory {

    // Centralized storage
    private Map<String, Integer> availabilityMap;

    // Constructor initializes inventory
    public RoomInventory() {
        availabilityMap = new HashMap<>();
    }

    // Register room type with initial count
    public void addRoomType(String roomType, int count) {
        availabilityMap.put(roomType, count);
    }

    // Get availability
    public int getAvailability(String roomType) {
        return availabilityMap.getOrDefault(roomType, 0);
    }

    // Update availability safely
    public void updateAvailability(String roomType, int change) {
        int current = getAvailability(roomType);
        int updated = current + change;

        if (updated < 0) {
            System.out.println("⚠ Cannot reduce below zero for " + roomType);
            return;
        }

        availabilityMap.put(roomType, updated);
    }

    // Display full inventory
    public void displayInventory() {
        System.out.println("\n--- Current Room Inventory ---");
        for (Map.Entry<String, Integer> entry : availabilityMap.entrySet()) {
            System.out.println(entry.getKey() + " → Available: " + entry.getValue());
        }
    }
}

// -------------------- APPLICATION ENTRY --------------------

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("   Welcome to Book My Stay");
        System.out.println("   Version: v1.2");
        System.out.println("========================================");

        // Create Room Objects (Polymorphism still intact)
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Initialize Inventory (Single Source of Truth)
        RoomInventory inventory = new RoomInventory();

        inventory.addRoomType(single.getType(), 5);
        inventory.addRoomType(doubleRoom.getType(), 3);
        inventory.addRoomType(suite.getType(), 2);

        // Display Room Details
        System.out.println("\n--- Room Details ---\n");
        single.displayDetails();
        doubleRoom.displayDetails();
        suite.displayDetails();

        // Display Inventory
        inventory.displayInventory();

        // Simulate Updates
        System.out.println("\n--- Updating Inventory ---");
        inventory.updateAvailability("Single Room", -1); // booking
        inventory.updateAvailability("Suite Room", -2);  // booking
        inventory.updateAvailability("Double Room", +1); // cancellation

        // Display Updated Inventory
        inventory.displayInventory();

        System.out.println("\nApplication समाप्त.");
    }
}