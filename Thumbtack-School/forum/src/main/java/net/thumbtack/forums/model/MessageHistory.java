package net.thumbtack.forums.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.thumbtack.forums.model.enums.MessageState;

@Getter
@Setter
@EqualsAndHashCode(exclude = "message")
@ToString(exclude = "message")
public class MessageHistory {

    private int id;
    private String body;
    private MessageState state;
    private Message message;

    public MessageHistory() {
        this.state = MessageState.UNPUBLISHED;
    }

    public MessageHistory(String body) {
        this();
        this.body = body;
    }
}
