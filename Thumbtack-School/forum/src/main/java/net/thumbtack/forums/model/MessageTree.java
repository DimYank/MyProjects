package net.thumbtack.forums.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.thumbtack.forums.model.enums.MessagePriority;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class MessageTree {
    private long id;
    private Forum forum;
    private String subject;
    private MessagePriority priority;
    private Message rootMessage;
    private List<String> tags;

    public MessageTree() {
    }

    public MessageTree(Forum forum, String subject, Message rootMessage) {
        this.forum = forum;
        this.subject = subject;
        this.rootMessage = rootMessage;
        this.priority = MessagePriority.NORMAL;
        this.rootMessage.setTree(this);
    }

    public MessageTree(Forum forum, String subject, MessagePriority priority, Message rootMessage, List<String> tags) {
        this(forum, subject, rootMessage);
        if (priority != null) {
            this.priority = priority;
        }
        this.tags = tags;
    }

    public void setRootMessage(Message rootMessage) {
        this.rootMessage = rootMessage;
        rootMessage.setTree(this);
    }

    @Override
    public String toString() {
        return "MessageTree{" +
                "id=" + id +
                ", forum=" + forum.getId() +
                ", subject='" + subject + '\'' +
                ", priority=" + priority +
                ", tags=" + tags +
                '}';
    }
}
