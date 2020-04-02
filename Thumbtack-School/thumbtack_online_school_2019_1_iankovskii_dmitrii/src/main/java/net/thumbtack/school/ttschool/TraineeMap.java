package net.thumbtack.school.ttschool;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TraineeMap {

    private Map<Trainee, String> traineeMap;

    TraineeMap(){
        traineeMap = new HashMap<>();
    }

    public void addTraineeInfo(Trainee trainee, String institute){
        if(traineeMap.containsKey(trainee)){
            throw new TrainingException(TrainingErrorCode.DUPLICATE_TRAINEE);
        }
        traineeMap.put(trainee, institute);
    }

    public void replaceTraineeInfo(Trainee trainee, String institute){
        if(!traineeMap.containsKey(trainee)){
            throw new TrainingException(TrainingErrorCode.TRAINEE_NOT_FOUND);
        }
        traineeMap.replace(trainee, institute);
    }

    public void removeTraineeInfo(Trainee trainee){
        if(!traineeMap.containsKey(trainee)){
            throw new TrainingException(TrainingErrorCode.TRAINEE_NOT_FOUND);
        }
        traineeMap.remove(trainee);
    }

    public int getTraineesCount(){
        return traineeMap.size();
    }

    public String getInstituteByTrainee(Trainee trainee){
        if(!traineeMap.containsKey(trainee)){
            throw new TrainingException(TrainingErrorCode.TRAINEE_NOT_FOUND);
        }
        return traineeMap.get(trainee);
    }

    public Set<Trainee> getAllTrainees(){
        return traineeMap.keySet();
    }

    public Set<String> getAllInstitutes(){
        return new HashSet<>(traineeMap.values());
    }

    public boolean isAnyFromInstitute(String institute){
        for(String value : traineeMap.values()){
            if(value.equals(institute)){
                return true;
            }
        }
        return false;
    }
}
