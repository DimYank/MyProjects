package threads.task14;

import java.util.Queue;

public class Transport extends Thread {
    private Queue<String> queue;

    public Transport(Queue<String> queue) {
        this.queue = queue;
    }

    public void send(Message message) {
        try {
            Thread.sleep(50);
            System.out.println(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String email;
        do {
            email = queue.poll();
            if (email != null) {
                send(new Message(email, "sender", "Subj", "Text"));
            }
        } while (email != null);
        System.out.println("Transport finished");
    }
}
