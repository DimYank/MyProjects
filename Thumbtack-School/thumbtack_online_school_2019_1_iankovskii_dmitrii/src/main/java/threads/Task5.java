package threads;

import java.util.ArrayList;
import java.util.Random;

//Jба потока на базе одного и того же класса, использовать synchronized методы.
public class Task5 {
    enum Action{
        ADD,
        DELETE;
    }

    class ArrayThread extends Thread{
        private ArrayList list;
        private Action action;

        public ArrayThread(ArrayList list, Action action){
            this.list = list;
            this.action = action;
        }

        public synchronized void delete(){
            if (list.isEmpty()) {
                System.out.println("List is empty!");
                return;
            }
            int randInt;
            if(list.size() < 2){
                randInt = 0;
            }else {
                Random random = new Random();
                randInt = random.nextInt(list.size() - 1);
            }
            System.out.println("Deleting: " + randInt);
            list.remove(randInt);
        }

        public synchronized void add(){
            Random random = new Random();
            int randInt = random.nextInt() & Integer.MAX_VALUE;
            System.out.println("Adding: " + randInt);
            list.add(randInt);
        }

        public void run() {
            if(action == Action.ADD){
                for (int i = 0; i < 10000; i++) {
                    add();
                }
            }else {
                for (int i = 0; i < 10000; i++) {
                    delete();
                }
            }
        }
    }

    public void startArrayOperations(){
        ArrayList<Integer> list = new ArrayList<>();
        Thread threadOne = new ArrayThread(list, Action.ADD);
        Thread threadTwo = new ArrayThread(list, Action.DELETE);
        threadOne.start();
        threadTwo.start();
        try {
            threadOne.join();
            threadTwo.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
}
