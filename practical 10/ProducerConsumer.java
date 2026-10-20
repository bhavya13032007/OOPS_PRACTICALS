import java.util.LinkedList;
import java.util.Queue;

class Buffer {
    Queue<Integer> queue = new LinkedList<>();
    int capacity = 3;

    synchronized void produce(int value) throws InterruptedException {
        while (queue.size() == capacity) {
            wait();
        }

        queue.add(value);
        System.out.println("Produced: " + value);
        notify();
    }

    synchronized int consume() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }

        int value = queue.remove();
        System.out.println("Consumed: " + value);
        notify();
        return value;
    }
}

public class ProducerConsumer {
    public static void main(String[] args) {
        Buffer buffer = new Buffer();

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    buffer.produce(i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    buffer.consume();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
    }
}