package net.thumbtack.forums.dto.response.message;

import lombok.Getter;
import lombok.Setter;
import net.thumbtack.forums.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CommentInfoDto {
    private long id;
    private User creator;
    private List<String> body;
    private LocalDateTime created;
    private double rating;
    private int rated;
    private List<CommentInfoDto> comments;

    public CommentInfoDto(long id, User creator, List<String> body, LocalDateTime created, double rating, int rated,
                          List<CommentInfoDto> comments) {
        this.id = id;
        this.creator = creator;
        this.body = body;
        this.created = created;
        this.rating = rating;
        this.rated = rated;
        this.comments = comments;
    }
}
