package threads;

import java.util.ArrayList;
import java.util.Random;

//В основном потоке создать ArrayList<Integer>. Запустить 2 потока на базе разных классов,
// один поток 10000 раз добавляет в список случайное целое число, другой 10000 раз удаляет элемент.
public class Task4 {

    class AdderThread extends Thread {
        private final ArrayList list;

        public AdderThread(ArrayList list) {
            this.list = list;
        }

        public void add() {
            synchronized (list) {
                Random random = new Random();
                int randInt = random.nextInt() & Integer.MAX_VALUE;
                System.out.println("Adding: " + randInt);
                list.add(randInt);
            }
        }

        public void run() {
            for (int i = 0; i < 10000; i++) {
                add();
            }
        }
    }

    class DeleterThread extends Thread {
        private final ArrayList list;

        public DeleterThread(ArrayList list) {
            this.list = list;
        }

        private void delete() {
            synchronized (list) {
                if (list.isEmpty()) {
                    System.out.println("List is empty!");
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
            }
        }

        public void run() {
            for (int i = 0; i < 10000; i++) {
                delete();
            }
        }
    }

    public void startArrayOperations() {
        ArrayList<Integer> list = new ArrayList<>();
        AdderThread adderThread = new AdderThread(list);
        DeleterThread deleterThread = new DeleterThread(list);
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
