class Account {
    String name;

    Account(String name) {
        this.name = name;
    }
}

public class DeadlockExample {
    static Account a = new Account("A");
    static Account b = new Account("B");

    static void deadlock() {
        Thread t1 = new Thread(() -> {
            synchronized (a) {
                System.out.println("Thread 1 locked A");

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                synchronized (b) {
                    System.out.println("Thread 1 locked B");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (b) {
                System.out.println("Thread 2 locked B");

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                synchronized (a) {
                    System.out.println("Thread 2 locked A");
                }
            }
        });

        t1.start();
        t2.start();
    }

    static void safeTransfer() {
        Thread t1 = new Thread(() -> transfer(a, b));
        Thread t2 = new Thread(() -> transfer(b, a));

        t1.start();
        t2.start();
    }

    static void transfer(Account x, Account y) {
        Account first = a;
        Account second = b;

        synchronized (first) {
            synchronized (second) {
                System.out.println(Thread.currentThread().getName()
                        + " completed transfer");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Deadlock demonstration:");
        deadlock();
    }
}