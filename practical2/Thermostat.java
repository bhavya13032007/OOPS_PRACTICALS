public class Thermostat {

    // Private variables
    private String location;
    private int temperature;

    // Constants
    private static final int MIN = 16;
    private static final int MAX = 30;

    // Static variable
    private static int activeCount = 0;

    // Constructor with location and temperature
    Thermostat(String location, int startTemp) {
        this.location = location;

        if (startTemp >= MIN && startTemp <= MAX) {
            temperature = startTemp;
        } else {
            temperature = 22;
        }

        activeCount++;
    }

    // Constructor with only location
    Thermostat(String location) {
        this(location, 22);
    }

    // Increase temperature
    void raise() {
        if (temperature < MAX) {
            temperature++;
        } else {
            System.out.println("Already at maximum (30)");
        }
    }

    // Decrease temperature
    void lower() {
        if (temperature > MIN) {
            temperature--;
        } else {
            System.out.println("Already at minimum (16)");
        }
    }

    // Getter
    int getTemperature() {
        return temperature;
    }

    // Static getter
    static int getActiveCount() {
        return activeCount;
    }

    // Main method
    public static void main(String[] args) {

        // Create two thermostats
        Thermostat t1 = new Thermostat("Living Room");
        Thermostat t2 = new Thermostat("Bedroom", 25);

        System.out.println("Initial Temperature = " + t1.getTemperature());

        // Raise temperature 10 times
        System.out.println("\nIncreasing Temperature:");
        for (int i = 1; i <= 10; i++) {
            t1.raise();
            System.out.println("Temperature = " + t1.getTemperature());
        }

        // Lower temperature 20 times
        System.out.println("\nDecreasing Temperature:");
        for (int i = 1; i <= 20; i++) {
            t1.lower();
            System.out.println("Temperature = " + t1.getTemperature());
        }

        // Print active thermostat count
        System.out.println("\nActive Thermostats = " + Thermostat.getActiveCount());
    }
}