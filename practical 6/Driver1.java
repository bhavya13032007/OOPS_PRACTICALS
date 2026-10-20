@FunctionalInterface
interface Notifier {
    void send(String message);
}

interface Urgent {
}

class UrgentNotifier implements Notifier, Urgent {
    private Notifier notifier;

    UrgentNotifier(Notifier notifier) {
        this.notifier = notifier;
    }

    public void send(String message) {
        notifier.send(message);
    }
}

public class Driver1 {
    public static void main(String[] args) {

        Notifier email = message ->
            System.out.println("Email: " + message);

        Notifier sms = message ->
            System.out.println("SMS: " + message);

        Notifier urgentEmail = new UrgentNotifier(email);

        Notifier[] senders = { email, sms, urgentEmail };

        String message = "Meeting at 10 AM";

        for (Notifier n : senders) {
            n.send(message);

            if (n instanceof Urgent) {
                n.send(message);
            }
        }
    }
}