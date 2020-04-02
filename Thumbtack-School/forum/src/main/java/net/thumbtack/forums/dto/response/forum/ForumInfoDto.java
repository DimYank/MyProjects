package net.thumbtack.forums.dto.response.forum;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.thumbtack.forums.model.enums.ForumType;

@Getter
@Setter
@ToString
public class ForumInfoDto {
    private long id;
    private String name;
    private ForumType type;
    private String creator;
    private boolean readonly;
    private int messageCount;
    private int commentCount;

    public ForumInfoDto(long id, String name, ForumType type, String creator, boolean readonly, int messageCount, int commentCount) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.creator = creator;
        this.readonly = readonly;
        this.messageCount = messageCount;
        this.commentCount = commentCount;
    }
}
