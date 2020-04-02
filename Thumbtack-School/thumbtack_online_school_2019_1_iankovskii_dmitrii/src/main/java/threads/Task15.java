package threads;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingDeque;

class Data {
    private int[] numbers;

    public Data(int[] numbers) {
        this.numbers = numbers;
    }

    public int[] get() {
        return numbers;
    }
}

class Producer extends Thread {
    private LinkedBlockingDeque<Data> queue;
    private int runAmount;

    public Producer(LinkedBlockingDeque<Data> queue, int runAmount) {
        this.queue = queue;
        this.runAmount = runAmount;
    }

    @Override
    public void run() {
        for (int i = 0; i < runAmount; i++) {
            queue.add(new Data(new int[]{i}));
        }
    }
}

class Consumer extends Thread {
    private LinkedBlockingDeque<Data> queue;

    public Consumer(LinkedBlockingDeque<Data> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Data taken = queue.take();
                if(taken.get() == null){
                    break;
                }
                System.out.println("Taken: " + Arrays.toString(taken.get()));
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}

public class Task15 {
    public static void main(String[] args) {
        LinkedBlockingDeque<Data> queue = new LinkedBlockingDeque<>();
        Thread cons1 = new Consumer(queue);
        Thread prod1 = new Producer(queue, 5);
        Thread prod2 = new Producer(queue, 5);
        Thread cons3 = new Consumer(queue);
        Thread cons2 = new Consumer(queue);
        prod1.start();
        prod2.start();
        cons1.start();
        cons2.start();
        cons3.start();

        try {
            prod1.join();
            prod2.join();
            queue.add(new Data(null));
            queue.add(new Data(null));
            queue.add(new Data(null));
            cons1.join();
            cons2.join();
            cons3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Done.");
    }
}
