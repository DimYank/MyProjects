package net.thumbtack.forums.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(exclude = {"tree", "parent", "comments"})
@ToString(exclude = {"tree", "parent", "comments"})
public class Message {
    private long id;
    private User creator;
    private List<MessageHistory> history;
    private LocalDateTime created;
    private Message parent;
    private List<Message> comments;
    private List<Rating> rating;
    private MessageTree tree;

    public Message() {
    }

    public Message(User creator, List<MessageHistory> history) {
        this.creator = creator;
        setHistoryMessages(history);
        this.history = history;
        this.created = LocalDateTime.now();
    }

    public void setHistory(List<MessageHistory> history) {
        setHistoryMessages(history);
        this.history = history;
    }

    public void addHistory(MessageHistory messageHistory){
        messageHistory.setMessage(this);
        history.add(messageHistory);
    }

    private void setHistoryMessages(List<MessageHistory> history){
        for(MessageHistory messageHistory : history){
            messageHistory.setMessage(this);
        }
    }
}
