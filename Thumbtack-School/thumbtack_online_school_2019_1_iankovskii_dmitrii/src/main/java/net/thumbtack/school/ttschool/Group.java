package net.thumbtack.school.ttschool;

import java.util.*;

public class Group {

    private String name, room;
    private List<Trainee> trainees = new ArrayList<>();

    public Group(String name, String room){
        setName(name);
        setRoom(room);
    }

    public String getName() {
        return name;
    }

    public String getRoom() {
        return room;
    }

    public List<Trainee> getTrainees() {
        return trainees;
    }

    public void setName(String name) {
        if(name != null){
            if (name.isEmpty()){
                throw new TrainingException(TrainingErrorCode.GROUP_WRONG_NAME);
            }
            else{
                this.name = name;
            }
        }
        else{
            throw new TrainingException(TrainingErrorCode.GROUP_WRONG_NAME);
        }
    }

    public void setRoom(String room) {
        if(room != null){
            if (room.isEmpty()){
                throw new TrainingException(TrainingErrorCode.GROUP_WRONG_ROOM);
            }
            else{
                this.room = room;
            }
        }
        else{
            throw new TrainingException(TrainingErrorCode.GROUP_WRONG_ROOM);
        }
    }

    public void addTrainee(Trainee trainee){
        trainees.add(trainee);
    }

    public void removeTrainee(Trainee trainee){
        if(!trainees.contains(trainee)){
            throw new TrainingException(TrainingErrorCode.TRAINEE_NOT_FOUND);
        }
        trainees.remove(trainee);
    }

    public void removeTrainee(int index){
        try {
            trainees.remove(index);
        }
        catch (IndexOutOfBoundsException ex){
            throw new TrainingException(TrainingErrorCode.TRAINEE_NOT_FOUND);
        }
    }

    public Trainee getTraineeByFirstName(String firstName){
        for (Trainee trainee : trainees) {
            if (trainee.getFirstName().equals(firstName)) {
                return trainee;
            }
        }
        throw new TrainingException(TrainingErrorCode.TRAINEE_NOT_FOUND);
    }

    public Trainee getTraineeByFullName(String fullName){
        for (Trainee trainee : trainees) {
            if (trainee.getFullName().equals(fullName)) {
                return trainee;
            }
        }
        throw new TrainingException(TrainingErrorCode.TRAINEE_NOT_FOUND);
    }

    public void sortTraineeListByFirstNameAscendant(){
        trainees.sort(Comparator.comparing(Trainee::getFirstName));
    }

    public void sortTraineeListByRatingDescendant(){
        trainees.sort((trainee1, trainee2) -> trainee2.getRating() - trainee1.getRating());
    }

    public void reverseTraineeList(){
        Collections.reverse(trainees);
    }

    public void rotateTraineeList(int positions){
        Collections.rotate(trainees, positions);
    }

    public List<Trainee> getTraineesWithMaxRating(){
        List<Trainee> sublist = new ArrayList<>();
        Iterator<Trainee> iterator = trainees.iterator();
        int maxRating = 0;
        while (iterator.hasNext()){
            Trainee trainee = iterator.next();
            if (maxRating < trainee.getRating()){
                maxRating = trainee.getRating();
                sublist.clear();
                sublist.add(trainee);
            }
            else if(maxRating == trainee.getRating()){
                sublist.add(trainee);
            }
        }

        if(sublist.isEmpty()){
            throw new TrainingException(TrainingErrorCode.TRAINEE_NOT_FOUND);
        }
        return sublist;
    }

    public boolean hasDuplicates(){
        Set<Trainee> testSet = new HashSet<>(trainees);
        return !(testSet.size() == trainees.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        if (name != null ? !name.equals(group.name) : group.name != null) return false;
        if (room != null ? !room.equals(group.room) : group.room != null) return false;
        return trainees != null ? trainees.equals(group.trainees) : group.trainees == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (room != null ? room.hashCode() : 0);
        result = 31 * result + (trainees != null ? trainees.hashCode() : 0);
        return result;
    }
}
