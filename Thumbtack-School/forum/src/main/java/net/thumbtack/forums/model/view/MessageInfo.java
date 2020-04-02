package net.thumbtack.forums.model.view;

import lombok.Getter;
import lombok.Setter;
import net.thumbtack.forums.model.MessageHistory;
import net.thumbtack.forums.model.User;
import net.thumbtack.forums.model.enums.MessagePriority;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class MessageInfo {
    private long id;
    private User creator;
    private String subject;
    private List<MessageHistory> history;
    private MessagePriority priority;
    private List<String> tags;
    private LocalDateTime created;
    private double rating;
    private int rated;
    private List<CommentInfo> comments;

    public MessageInfo() {
    }

    public void setRating(Double rating) {
        if (rating == null) {
            this.rating = 0;
        } else {
            this.rating = rating;
        }
    }
}
