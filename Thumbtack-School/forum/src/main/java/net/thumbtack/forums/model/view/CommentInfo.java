package net.thumbtack.forums.model.view;

import lombok.Getter;
import lombok.Setter;
import net.thumbtack.forums.model.MessageHistory;
import net.thumbtack.forums.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CommentInfo {
    private long id;
    private User creator;
    private List<MessageHistory> history;
    private LocalDateTime created;
    private double rating;
    private int rated;
    private List<CommentInfo> comments;

    public CommentInfo() {
    }

    public void setRating(Double rating) {
        this.rating = rating == null ? 0 : rating;
    }
}
