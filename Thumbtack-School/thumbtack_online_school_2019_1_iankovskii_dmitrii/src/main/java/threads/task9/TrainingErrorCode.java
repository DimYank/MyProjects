package threads.task9;

public enum TrainingErrorCode {
    TRAINEE_WRONG_FIRSTNAME("Wrong first name!"),
    TRAINEE_WRONG_LASTNAME("Wrong last name!"),
    TRAINEE_WRONG_RATING("Wrong rating!"),
    GROUP_WRONG_NAME("Wrong group name!"),
    GROUP_WRONG_ROOM("Wrong group name!"),
    TRAINEE_NOT_FOUND("Trainee not found!"),
    SCHOOL_WRONG_NAME("Wrong school name!"),
    DUPLICATE_GROUP_NAME("Duplicate group name!"),
    GROUP_NOT_FOUND("Group not found!"),
    DUPLICATE_TRAINEE("Duplicate trainee!"),
    EMPTY_TRAINEE_QUEUE("Empty queue!");
    private String errorString;

    TrainingErrorCode(String errorString){
        this.errorString = errorString;
    }

    public String getErrorString(){
        return errorString;
    }
}
