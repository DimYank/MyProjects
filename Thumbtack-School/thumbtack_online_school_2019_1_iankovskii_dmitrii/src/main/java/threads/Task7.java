package threads;

import java.util.concurrent.Semaphore;

//“Ping Pong”, задача заключается в том чтобы бесконечно выводить на консоль
//сообщения “ping” или “pong” из двух разных потоков.
public class Task7 {

    class Court{
        private final Semaphore semPing = new Semaphore(1);
        private final Semaphore semPong = new Semaphore(0);

        public void ping(){
            try {
                semPing.acquire();
                System.out.println("PING | == O |");
            }catch (InterruptedException e){
                e.printStackTrace();
            }finally {
                semPong.release();
            }
        }

        public void pong(){
            try {
                semPong.acquire();
                System.out.println("PONG | O == |");
            }catch (InterruptedException e){
                e.printStackTrace();
            }finally {
                semPing.release();
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
