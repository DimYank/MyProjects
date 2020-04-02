package threads.task14;

import java.io.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {
    public static void main(String[] args) {

        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(new File("src/main/resources/emails.txt")));
            Queue<String> queue = new ConcurrentLinkedQueue<>();
            String email;
            do {
                email = bufferedReader.readLine();
                if (email != null) {
                    queue.add(email);
                }
            } while (email != null);

            Transport transport = new Transport(queue);
            Transport transport2 = new Transport(queue);
            Transport transport3 = new Transport(queue);
            transport.start();
            transport2.start();
            transport3.start();
            try {
                transport.join();
                transport2.join();
                transport3.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
