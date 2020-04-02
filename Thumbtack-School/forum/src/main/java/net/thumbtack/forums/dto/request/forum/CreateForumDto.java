package net.thumbtack.forums.dto.request.forum;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.thumbtack.forums.dto.validation.ForumName;
import net.thumbtack.forums.model.enums.ForumType;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class CreateForumDto {
    @ForumName
    private String name;
    @NotNull
    private ForumType type;

    public CreateForumDto() {
    }

    public CreateForumDto(String name, ForumType type) {
        this.name = name;
        this.type = type;
    }
}
