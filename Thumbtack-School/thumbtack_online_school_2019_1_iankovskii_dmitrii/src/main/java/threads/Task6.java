package threads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

//Можно ли корректно решить задачу 4 , используя Collections.synchronizedList и не используя synchronized явно ?
//Если да - напишите программу.
public class Task6 {

    class AdderThread extends Thread{
        private List syncList;
        private Random random;
        public AdderThread(List syncList){
            this.syncList = syncList;
            random = new Random();
        }

        public void run() {
            for (int i = 0; i < 10000; i++) {
                int randInt = random.nextInt() & Integer.MAX_VALUE;
                System.out.println("Adding: " + randInt);
                syncList.add(randInt);
            }
        }
    }

    class DeleterThread extends Thread{
        private List syncList;
        private Random random;

        public DeleterThread(List syncList) {
            this.syncList = syncList;
            random = new Random();
        }

        public void run(){
            for (int i = 0; i < 10000; i++) {
                if (syncList.isEmpty()) {
                    System.out.println("List is empty!");
                    continue;
                }
                int randInt;
                if (syncList.size() < 2) {
                    randInt = 0;
                } else {
                    randInt = random.nextInt(syncList.size() - 1);
                }
                System.out.println("Deleting: " + randInt);
                syncList.remove(randInt);
            }
        }
    }

    public void start(){
        List<Integer> list = Collections.synchronizedList(new ArrayList<Integer>());
        Thread adder = new AdderThread(list);
        Thread deleter = new DeleterThread(list);
        deleter.start();
        adder.start();
        try {
            adder.join();
            deleter.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
}
