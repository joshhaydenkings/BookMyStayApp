abstract class Room {
    private String type;
    private int beds;
    private double price;

    // Constructor
    public Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }

    // Getters (Encapsulation)
    public String getType() {
        return type;
    }

    public int getBeds() {
        return beds;
    }

    public double getPrice() {
        return price;
    }

    // Abstract method (forces subclasses to define behavior)
    public abstract void displayDetails();
}

// Concrete Class - Single Room
class SingleRoom extends Room {

    public SingleRoom() {
        super("Single Room", 1, 1000.0);
    }

    @Override
    public void displayDetails() {
        System.out.println("Room Type: " + getType());
        System.out.println("Beds: " + getBeds());
        System.out.println("Price per night: ₹" + getPrice());
    }
}

// Concrete Class - Double Room
class DoubleRoom extends Room {

    public DoubleRoom() {
        super("Double Room", 2, 1800.0);
    }

    @Override
    public void displayDetails() {
        System.out.println("Room Type: " + getType());
        System.out.println("Beds: " + getBeds());
        System.out.println("Price per night: ₹" + getPrice());
    }
}

// Concrete Class - Suite Room
class SuiteRoom extends Room {

    public SuiteRoom() {
        super("Suite Room", 3, 3500.0);
    }

    @Override
    public void displayDetails() {
        System.out.println("Room Type: " + getType());
        System.out.println("Beds: " + getBeds());
        System.out.println("Price per night: ₹" + getPrice());
    }
}


public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("   Welcome to Book My Stay");
        System.out.println("   Version: v1.1");
        System.out.println("========================================");

        // Create Room Objects (Polymorphism)
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Static Availability (Simple Variables)
        int singleAvailability = 5;
        int doubleAvailability = 3;
        int suiteAvailability = 2;

        // Display Details
        System.out.println("\n--- Room Details & Availability ---\n");

        single.displayDetails();
        System.out.println("Available: " + singleAvailability);
        System.out.println("----------------------------------");

        doubleRoom.displayDetails();
        System.out.println("Available: " + doubleAvailability);
        System.out.println("----------------------------------");

        suite.displayDetails();
        System.out.println("Available: " + suiteAvailability);
        System.out.println("----------------------------------");

        System.out.println("\nApplication समाप्त.");
    }
}