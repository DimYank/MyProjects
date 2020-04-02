package threads;

import java.util.concurrent.LinkedBlockingDeque;

interface Executable {
    void execute();
}

class Task implements Executable {

    private boolean last;

    public Task(boolean last) {
        this.last = last;
    }

    public boolean isLast() {
        return last;
    }

    public void execute() {
        System.out.println("TASK EXECUTED!");
    }
}

class Developer extends Thread {
    private LinkedBlockingDeque<Task> queue;
    private int runAmount;

    public Developer(LinkedBlockingDeque<Task> queue, int runAmount) {
        this.queue = queue;
        this.runAmount = runAmount;
    }

    @Override
    public void run() {
        for (int i = 0; i < runAmount; i++) {
            queue.add(new Task(false));
        }
    }
}

class Executor extends Thread {
    private LinkedBlockingDeque<Task> queue;

    public Executor(LinkedBlockingDeque<Task> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Task nextTask = queue.take();
                if(nextTask.isLast()){
                    break;
                }
                nextTask.execute();
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}

public class Task16 {
    public static void main(String[] args) {
        LinkedBlockingDeque<Task> queue = new LinkedBlockingDeque<>();
        Thread dev1 = new Developer(queue, 4);
        Thread dev2 = new Developer(queue, 5);
        Thread exec1 = new Executor(queue);
        Thread exec2 = new Executor(queue);
        Thread exec3 = new Executor(queue);
        dev1.start();
        dev2.start();
        exec1.start();
        exec2.start();
        exec3.start();
        try {
            dev1.join();
            dev2.join();
            queue.add(new Task(true));
            queue.add(new Task(true));
            queue.add(new Task(true));
            exec1.join();
            exec2.join();
            exec3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Done.");
    }
}
