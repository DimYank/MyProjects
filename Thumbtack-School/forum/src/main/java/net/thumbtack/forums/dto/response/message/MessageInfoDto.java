package net.thumbtack.forums.dto.response.message;

import lombok.Getter;
import lombok.Setter;
import net.thumbtack.forums.model.User;
import net.thumbtack.forums.model.enums.MessagePriority;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class MessageInfoDto {
    private long id;
    private User creator;
    private String subject;
    private List<String> body;
    private MessagePriority priority;
    private List<String> tags;
    private LocalDateTime created;
    private double rating;
    private int rated;
    private List<CommentInfoDto> comments;

    public MessageInfoDto(long id, User creator, String subject, List<String> body, MessagePriority priority,
                          List<String> tags, LocalDateTime created, double rating, int rated, List<CommentInfoDto> comments) {
        this.id = id;
        this.creator = creator;
        this.subject = subject;
        this.body = body;
        this.priority = priority;
        this.tags = tags;
        this.created = created;
        this.rating = rating;
        this.rated = rated;
        this.comments = comments;
    }
}
