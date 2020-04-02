package threads;

public class Task1and2 {

    //Напечатать все свойства текущего потока
    public void printProperties(){
        Thread currentThread = Thread.currentThread();

        System.out.println("Current thread properties: " + currentThread + "\n");
    }

    //Создать новый поток и дождаться его окончания в первичном потоке.
    public void createThread(){
        Runnable runnable = () -> {
            System.out.println("New Thread Created!");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("New Thread Finished!");
        };
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Parent Thread Finished!\n");
    }
}
