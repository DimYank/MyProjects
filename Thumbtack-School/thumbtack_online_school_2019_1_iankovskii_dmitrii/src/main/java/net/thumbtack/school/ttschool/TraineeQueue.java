package net.thumbtack.school.ttschool;

import java.util.ArrayDeque;
import java.util.Queue;

public class TraineeQueue {

    private Queue<Trainee> coffeQue;

    public TraineeQueue(){
        coffeQue = new ArrayDeque<>();
    }

    public void addTrainee(Trainee trainee){
        coffeQue.offer(trainee);
    }

    public Trainee removeTrainee(){
        if(coffeQue.isEmpty()){
            throw new TrainingException(TrainingErrorCode.EMPTY_TRAINEE_QUEUE);
        }
        return coffeQue.poll();
    }
}
