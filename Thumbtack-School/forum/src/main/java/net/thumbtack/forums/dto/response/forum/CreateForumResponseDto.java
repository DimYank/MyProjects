package net.thumbtack.forums.dto.response.forum;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.thumbtack.forums.model.enums.ForumType;

@Getter
@Setter
@ToString
public class CreateForumResponseDto {
    private int id;
    private String name;
    private ForumType type;

    public CreateForumResponseDto(int id, String name, ForumType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
}
