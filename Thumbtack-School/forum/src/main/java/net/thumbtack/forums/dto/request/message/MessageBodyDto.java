package net.thumbtack.forums.dto.request.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
public class MessageBodyDto {
    @NotEmpty(message = "Message cant't be empty string or null!!")
    private String body;

    public MessageBodyDto() {
    }

    public MessageBodyDto(String body) {
        this.body = body;
    }
}
