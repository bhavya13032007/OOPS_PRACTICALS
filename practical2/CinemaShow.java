public class CinemaShow {

    // Instance variables
    private String title;
    private int seatsAvailable;
    private final int capacity;

    // Static variable
    private static int totalBooked = 0;

    // Constructor with title and capacity
    CinemaShow(String title, int capacity) {
        this.title = title;
        this.capacity = capacity;
        seatsAvailable = capacity;
    }

    // Constructor with only title
    CinemaShow(String title) {
        this(title, 100);
    }

    // Book seats
    boolean book(int n) {
        if (n <= seatsAvailable) {
            seatsAvailable = seatsAvailable - n;
            totalBooked = totalBooked + n;
            return true;
        } else {
            return false;
        }
    }

    // Cancel seats
    void cancel(int n) {
        seatsAvailable = seatsAvailable + n;

        if (seatsAvailable > capacity) {
            seatsAvailable = capacity;
        }
    }

    // Getter
    int getSeatsAvailable() {
        return seatsAvailable;
    }

    // Static getter
    static int getTotalBooked() {
        return totalBooked;
    }

    // Main method
    public static void main(String[] args) {

        CinemaShow show = new CinemaShow("Avengers", 50);

        System.out.println("Available Seats = " + show.getSeatsAvailable());

        System.out.println("\nBook 20 Seats");
        System.out.println("Booking Successful: " + show.book(20));
        System.out.println("Available Seats = " + show.getSeatsAvailable());

        System.out.println("\nBook 25 Seats");
        System.out.println("Booking Successful: " + show.book(25));
        System.out.println("Available Seats = " + show.getSeatsAvailable());

        System.out.println("\nBook 10 Seats");
        System.out.println("Booking Successful: " + show.book(10));
        System.out.println("Available Seats = " + show.getSeatsAvailable());

        System.out.println("\nCancel 15 Seats");
        show.cancel(15);
        System.out.println("Available Seats = " + show.getSeatsAvailable());

        System.out.println("\nCancel 30 Seats");
        show.cancel(30);
        System.out.println("Available Seats = " + show.getSeatsAvailable());

        System.out.println("\nTotal Booked Seats = " + CinemaShow.getTotalBooked());
    }
}