package threads;

//Создать 3 новых потока и дождаться окончания их всех в первичном потоке.
//Для каждого потока создать свой собственный класс.
public class Task3 {

    class ThreadOne extends Thread{
        public void run(){
            System.out.println("Thread One Created!");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Thread One Finished!");
        }
    }

    class ThreadTwo extends Thread{
        public void run(){
            System.out.println("Thread Two Created!");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Thread Two Finished!");
        }
    }

    class ThreadThree extends Thread{
        public void run(){
            System.out.println("Thread Three Created!");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Thread Three Finished!");
        }
    }

    public void createThreeThreads(){
        ThreadOne threadOne = new ThreadOne();
        ThreadTwo threadTwo = new ThreadTwo();
        ThreadThree threadThree = new ThreadThree();
        threadOne.start();
        threadTwo.start();
        threadThree.start();
        try {
            threadOne.join();
            threadTwo.join();
            threadThree.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        System.out.println("Parent Thread Finished!\n");
    }
}
