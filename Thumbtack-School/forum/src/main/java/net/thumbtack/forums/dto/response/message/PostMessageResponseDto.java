package net.thumbtack.forums.dto.response.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.thumbtack.forums.model.enums.MessageState;

@Getter
@Setter
@ToString
public class PostMessageResponseDto {
    private long id;
    private MessageState state;

    public PostMessageResponseDto(long id, MessageState state) {
        this.id = id;
        this.state = state;
    }
}
