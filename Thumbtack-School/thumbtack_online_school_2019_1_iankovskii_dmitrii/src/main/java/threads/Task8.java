package threads;


import java.util.concurrent.Semaphore;

//Система читатель-писатель
public class Task8 {
    class Shop{
        private final Semaphore semWrite = new Semaphore(1);
        private final Semaphore semRead = new Semaphore(0);

        public void write(){
            try {
                semWrite.acquire();
                System.out.println("WROTE");
            }catch (InterruptedException e){
                e.printStackTrace();
            }finally {
                semRead.release();
            }
        }

        public void read(){
            try {
                semRead.acquire();
                System.out.println("READ");
            }catch (InterruptedException e){
                e.printStackTrace();
            }finally {
                semWrite.release();
            }
        }
    }

    class Writer extends Thread{
        private Shop shop;

        public Writer(Shop shop) {
            this.shop = shop;
        }

        public void run() {
            for (int i = 0; i < 100; i++) {
                shop.write();
            }
        }
    }

    class Reader extends Thread{
        private Shop shop;

        public Reader(Shop shop) {
            this.shop = shop;
        }

        public void run() {
            for (int i = 0; i < 100; i++){
                shop.read();
            }
        }
    }

    public void start(){
        Shop shop = new Shop();
        Thread reader = new Reader(shop);
        Thread writer = new Writer(shop);
        reader.start();
        writer.start();
        try {
            reader.join();
            writer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
}
