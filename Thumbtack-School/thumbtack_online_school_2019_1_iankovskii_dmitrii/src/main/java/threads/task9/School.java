package threads.task9;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class School {

    private String name;
    private int year;
    private Set<Group> groups;

    public School(String name, int year){
        setName(name);
        setYear(year);
        groups = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }

    public synchronized Set<Group> getGroups() {
        return groups;
    }

    public void setName(String name){
        if(name != null){
            if(name.isEmpty()){
                throw new TrainingException(TrainingErrorCode.SCHOOL_WRONG_NAME);
            }
            else {
                this.name = name;
            }
        }
        else {
            throw new TrainingException(TrainingErrorCode.SCHOOL_WRONG_NAME);
        }
    }

    public void setYear(int year) {
        this.year = year;
    }

    public synchronized void addGroup(Group group){
        for(Group gr : groups){
            if(gr.getName().equals(group.getName())){
                throw new TrainingException(TrainingErrorCode.DUPLICATE_GROUP_NAME);
            }
        }
        groups.add(group);
    }

    public synchronized void removeGroup(Group group){
        if(!groups.contains(group)){
            throw new TrainingException(TrainingErrorCode.GROUP_NOT_FOUND);
        }
        groups.remove(group);
    }

    public synchronized void removeGroup(String name){
        for (Group gr : groups){
            if(gr.getName().equals(name)){
                groups.remove(gr);
                return;
            }
        }
        throw new TrainingException(TrainingErrorCode.GROUP_NOT_FOUND);
    }

    public boolean containsGroup(Group group){
        return groups.contains(group);
    }
}
