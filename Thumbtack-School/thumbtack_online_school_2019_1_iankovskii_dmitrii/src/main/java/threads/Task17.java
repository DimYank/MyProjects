package threads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

class MultiTask implements Executable {

    private List<Executable> tasks;

    public MultiTask(List<Executable> tasks) {
        this.tasks = tasks;
    }

    public List<Executable> getTasks() {
        return tasks;
    }

    public void execute() {
        tasks.get(0).execute();
        tasks.remove(0);
    }
}

class MultiDeveloper extends Thread {
    private LinkedBlockingDeque<MultiTask> queue;
    private int runAmount;
    private LinkedBlockingDeque<Action> controlQueue;

    public MultiDeveloper(LinkedBlockingDeque<MultiTask> queue, int runAmount, LinkedBlockingDeque<Action> controlQueue) {
        this.queue = queue;
        this.runAmount = runAmount;
        this.controlQueue = controlQueue;
    }

    @Override
    public void run() {
        Executable ex1 = () -> System.out.println("Executed 1 task from list!");
        Executable ex2 = () -> System.out.println("Executed 2 task from list!");
        for (int i = 0; i < runAmount; i++) {
            queue.add(new MultiTask(new ArrayList<>(Arrays.asList(ex1, ex2))));
            controlQueue.add(Action.TASK_ADDED);
        }
        controlQueue.add(Action.DEVELOPER_FINISHED);
    }
}

class MultiExecutor extends Thread {
    private LinkedBlockingDeque<MultiTask> queue;
    private LinkedBlockingDeque<Action> controlQueue;

    public MultiExecutor(LinkedBlockingDeque<MultiTask> queue, LinkedBlockingDeque<Action> controlQueue) {
        this.queue = queue;
        this.controlQueue = controlQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                MultiTask nextTask = queue.take();
                if (nextTask.getTasks() == null) {
                    break;
                }
                nextTask.execute();
                if (nextTask.getTasks().size() > 0) {
                    queue.put(nextTask);
                }else {
                    controlQueue.add(Action.TASK_COMPLETE);
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}

enum Action {
    TASK_ADDED,
    TASK_COMPLETE,
    DEVELOPER_FINISHED
}

public class Task17 {
    public static void main(String[] args) throws InterruptedException {

        final int devCount = 2;
        final int execCount = 2;

        LinkedBlockingDeque<MultiTask> queue = new LinkedBlockingDeque<>();
        LinkedBlockingDeque<Action> controlQueue = new LinkedBlockingDeque<>();
        List<MultiDeveloper> developers = new ArrayList<>();
        List<MultiExecutor> executors = new ArrayList<>();
        for (int i = 0; i < devCount; i++) {
            MultiDeveloper developer = new MultiDeveloper(queue, 5, controlQueue);
            developer.start();
            developers.add(developer);
        }
        for (int i = 0; i < execCount; i++) {
            MultiExecutor executor = new MultiExecutor(queue, controlQueue);
            executor.start();
            executors.add(executor);
        }

        int tasksInQueue = 0;
        int devFinishedCount = 0;
        while (true) {
            Action action = controlQueue.take();
            switch (action) {
                case TASK_ADDED:
                    tasksInQueue++;
                    break;
                case TASK_COMPLETE:
                    tasksInQueue--;
                    break;
                case DEVELOPER_FINISHED:
                    devFinishedCount++;
                    break;
            }
            if (devFinishedCount == devCount && tasksInQueue == 0) {
                for (int i = 0; i < execCount; i++){
                    queue.add(new MultiTask(null));
                }
                break;
            }
        }

        try {
            for (MultiDeveloper developer : developers) {
                developer.join();
            }
            for (MultiExecutor executor : executors) {
                executor.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Done.");
    }
}
