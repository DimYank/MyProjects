package net.thumbtack.forums.dto.response.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.thumbtack.forums.model.enums.MessageState;

@Getter
@Setter
@ToString
public class EditMessageResponseDto {
    private MessageState state;

    public EditMessageResponseDto(MessageState state) {
        this.state = state;
    }
}
