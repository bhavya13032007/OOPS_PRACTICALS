interface Switchable {
    void on();
    void off();

    default void toggle() {
        on();
    }
}

class Fan implements Switchable {
    public void on() {
        System.out.println("Fan is ON");
    }

    public void off() {
        System.out.println("Fan is OFF");
    }
}

class Light implements Switchable {
    public void on() {
        System.out.println("Light is ON");
    }

    public void off() {
        System.out.println("Light is OFF");
    }
}

@FunctionalInterface
interface Permission {
    boolean maySwitchOn(Switchable device, int hour);
}

public class Driver {
    public static void main(String[] args) {

        Switchable[] devices = {
            new Fan(),
            new Light()
        };

        System.out.println("Toggle devices:");

        for (Switchable device : devices) {
            device.toggle();
        }

        Permission p1 = new Permission() {
            public boolean maySwitchOn(Switchable device, int hour) {
                return hour >= 6 && hour <= 22;
            }
        };

        Permission p2 = (device, hour) -> hour >= 8 && hour <= 20;

        System.out.println("Anonymous class at 10: " + p1.maySwitchOn(devices[0], 10));
        System.out.println("Lambda at 10: " + p2.maySwitchOn(devices[1], 10));
    }
}