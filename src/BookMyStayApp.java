import java.util.*;


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

    public abstract void displayDetails();
}

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

// -------------------- INVENTORY --------------------

class RoomInventory {
    private Map<String, Integer> availabilityMap;

    public RoomInventory() {
        availabilityMap = new HashMap<>();
    }

    public void addRoomType(String roomType, int count) {
        availabilityMap.put(roomType, count);
    }

    // Read-only access
    public int getAvailability(String roomType) {
        return availabilityMap.getOrDefault(roomType, 0);
    }

    // Expose read-only view (defensive)
    public Map<String, Integer> getAllAvailability() {
        return Collections.unmodifiableMap(availabilityMap);
    }

    // Write method (not used in search)
    public void updateAvailability(String roomType, int change) {
        int current = getAvailability(roomType);
        int updated = current + change;

        if (updated < 0) {
            System.out.println("⚠ Cannot reduce below zero for " + roomType);
            return;
        }

        availabilityMap.put(roomType, updated);
    }
}

// -------------------- SEARCH SERVICE --------------------

class SearchService {

    private RoomInventory inventory;
    private List<Room> rooms;

    public SearchService(RoomInventory inventory, List<Room> rooms) {
        this.inventory = inventory;
        this.rooms = rooms;
    }

    // Read-only search operation
    public void searchAvailableRooms() {

        System.out.println("\n--- Available Rooms ---\n");

        boolean found = false;

        for (Room room : rooms) {
            int available = inventory.getAvailability(room.getType());

            // Defensive check: only show if available > 0
            if (available > 0) {
                room.displayDetails();
                System.out.println("Available: " + available);
                System.out.println("----------------------------------");
                found = true;
            }
        }

        if (!found) {
            System.out.println("No rooms available at the moment.");
        }
    }
}

// -------------------- APPLICATION --------------------

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("   Welcome to Book My Stay");
        System.out.println("   Version: v1.3");
        System.out.println("========================================");

        // Create Room Objects
        List<Room> roomList = new ArrayList<>();
        roomList.add(new SingleRoom());
        roomList.add(new DoubleRoom());
        roomList.add(new SuiteRoom());

        // Initialize Inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 5);
        inventory.addRoomType("Double Room", 0); // intentionally unavailable
        inventory.addRoomType("Suite Room", 2);

        // Create Search Service
        SearchService searchService = new SearchService(inventory, roomList);

        // Perform Search (READ-ONLY)
        searchService.searchAvailableRooms();

        System.out.println("\nApplication समाप्त.");
    }
}