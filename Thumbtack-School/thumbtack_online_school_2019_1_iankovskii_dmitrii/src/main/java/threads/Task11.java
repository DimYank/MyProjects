package threads;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Task11 {

    class Court{
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition pinged = lock.newCondition();
        private final Condition ponged = lock.newCondition();
        private boolean ping = true;

        public void ping(){
            try {
                lock.lock();
                ponged.await();
                ping = false;
                System.out.println("PING | == O |");
                pinged.signal();
            }catch (InterruptedException e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }

        public void pong(){
            try {
                lock.lock();
                ponged.signal();
                pinged.await();
                ping = true;
                System.out.println("PONG | O == |");
                ponged.signal();
            }catch (InterruptedException e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }
    }

    class Pinger extends Thread{
        private Court court;

        public Pinger(Court court) {
            this.court = court;
        }

        public void run() {
            for (int i = 0; i < 100; i++) {
                court.ping();
            }
        }
    }

    class Ponger extends Thread{
        private Court court;

        public Ponger(Court court) {
            this.court = court;
        }

        public void run() {
            for (int i = 0; i < 100; i++){
                court.pong();
            }
        }
    }

    public void start(){
        Court court = new Court();
        Thread pinger = new Pinger(court);
        Thread ponger = new Ponger(court);
        pinger.start();
        ponger.start();
        try {
            pinger.join();
            ponger.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

}
