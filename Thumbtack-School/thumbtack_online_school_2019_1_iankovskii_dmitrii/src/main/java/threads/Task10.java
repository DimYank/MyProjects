package threads;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

class AdderThread extends Thread {
    private final List<Integer> list;
    private final ReentrantLock lock;

    public AdderThread(List<Integer> list, ReentrantLock lock) {
        this.list = list;
        this.lock = lock;
    }

    public void add() {
        try {
            lock.lock();
            Random random = new Random();
            int randInt = random.nextInt() & Integer.MAX_VALUE;
            System.out.println("Adding: " + randInt);
            list.add(randInt);
            lock.unlock();
        } finally {
            lock.unlock();
        }
    }

    public void run() {
        for (int i = 0; i < 10000; i++) {
            add();
        }
    }
}

class DeleterThread extends Thread {
    private final List<Integer> list;
    private final ReentrantLock lock;

    public DeleterThread(List<Integer> list, ReentrantLock lock) {
        this.list = list;
        this.lock = lock;
    }

    private void delete() {
        try {
            lock.lock();
            if (list.isEmpty()) {
                System.out.println("List is empty!");
                lock.unlock();
                return;
            }
            int randInt;
            if (list.size() < 2) {
                randInt = 0;
            } else {
                Random random = new Random();
                randInt = random.nextInt(list.size() - 1);
            }
            System.out.println("Deleting: " + randInt);
            list.remove(randInt);
            lock.unlock();
        } finally {
            lock.unlock();
        }
    }

    public void run() {
        for (int i = 0; i < 10000; i++) {
            delete();
        }
    }
}

public class Task10 {
    public void startArrayOperations() {
        ArrayList<Integer> list = new ArrayList<>();
        ReentrantLock lock = new ReentrantLock();
        AdderThread adderThread = new AdderThread(list, lock);
        DeleterThread deleterThread = new DeleterThread(list, lock);
        adderThread.start();
        deleterThread.start();
        try {
            deleterThread.join();
            adderThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

}
