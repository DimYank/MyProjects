package net.thumbtack.forums.dto.request.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.thumbtack.forums.dto.validation.MessageTags;
import net.thumbtack.forums.model.enums.MessagePriority;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
public class PostMessageDto {
    @NotEmpty(message = "Subject cant't be empty string or null!!")
    private String subject;
    @NotEmpty(message = "Message cant't be empty string or null!!")
    private String body;
    @NotNull(message = "Priority can't be null!")
    private MessagePriority priority;
    @MessageTags
    private List<String> tags;

    public PostMessageDto() {
        this.priority = MessagePriority.NORMAL;
    }

    public PostMessageDto(String subject, String body) {
        this();
        this.subject = subject;
        this.body = body;
    }

    public PostMessageDto(String subject, String body, MessagePriority priority, List<String> tags) {
        this(subject, body);
        this.priority = priority;
        this.tags = tags;
    }
}
