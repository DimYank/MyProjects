package net.thumbtack.forums.error;

public class ServerException extends Exception {

    private ServerError error;

    public ServerException(ServerError error) {
        this.error = error;
    }

    public ServerError getError() {
        return error;
    }
}
