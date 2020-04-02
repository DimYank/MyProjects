package net.thumbtack.school.ttschool;

public class TrainingException extends RuntimeException{

    private TrainingErrorCode errorCode;

    public TrainingException(TrainingErrorCode errorCode){
        super();
        this.errorCode = errorCode;
    }

    public TrainingErrorCode getErrorCode(){
        return errorCode;
    }

}
