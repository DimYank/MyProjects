package net.thumbtack.forums.error;

public enum ServerError {

    USER_WRONG_CHARS("name", "Inappropriate chars used in the name %s!"),
    USER_WRONG_NAME("name", "Wrong username %s!"),
    USER_WRONG_PASSWORD("password", "Wrong password!"),
    USER_LONG_NAME("name", "The name %s is too long!"),
    USER_SAME_NAME("name", "User with name %s already exits!"),
    USER_EMPTY_NAME("name", "The name is empty!"),
    USER_NOT_SUPER("", "You are not a superuser!"),
    USER_DELETED("name", "User is deleted!"),
    USER_BANNED("cookie", "User is banned!"),
    USER_BANNED_FOREVER("cookie", "User is banned forever!"),
    USER_NOT_FOUND("id", "User not found!"),
    NO_VALID_SESSION("cookie", "User has no opened sessions!"),
    SHORT_PASS("pass", "The password is too short!"),
    EMPTY_PASS("pass", "The password is empty!"),
    WRONG_EMAIL("email", "Wrong email address %s!"),
    FORUM_WRONG_CHARS("name", "Wrong chars used in forum name %s!"),
    FORUM_EMPTY_NAME("name", "Forum name is empty!"),
    FORUM_LONG_NAME("name", "Forum name %s is too long!"),
    READ_ONLY_FORUM("message_id", "Message is in read only forum!"),
    FORUM_SAME_NAME("name", "Forum name %s already exists!"),
    SQL_SERVER_ERROR("", "Error in SQL syntax!"),
    MESSAGE_WRONG_ID("message_id", "No message with id %o!"),
    MESSAGE_HAS_COMMENTS("message_id", "Can't delete message that has comments!"),
    ANOTHER_USER_MESSAGE("message_id", "Message created by another user!"),
    RATE_OWN_MESSAGE("message_id", "Can't rate your own message!"),
    CANT_CREATE_TREE("message_id", "Can't create new branch!"),
    CANT_BAN_SUPER("user_id", "Can't ban a superuser!"),
    MESSAGE_PUBLISHED("message_id", "Message have already been published!"),
    MESSAGE_UNPUBLISHED("message_id", "Can't comment unpublished message!"),
    USER_NOT_MODERATOR("user_id", "User is not a moderator in this forum!");

    private String field;
    private String message;

    ServerError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
