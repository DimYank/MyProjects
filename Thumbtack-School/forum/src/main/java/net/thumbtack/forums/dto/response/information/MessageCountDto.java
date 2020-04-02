package net.thumbtack.forums.dto.response.information;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MessageCountDto {
    private long messageCount;
    private long commentCount;

    public MessageCountDto(long messageCount, long commentCount) {
        this.messageCount = messageCount;
        this.commentCount = commentCount;
    }
}
