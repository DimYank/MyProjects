package threads.task9;

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
