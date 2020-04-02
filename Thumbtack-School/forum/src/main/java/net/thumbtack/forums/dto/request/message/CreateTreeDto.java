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
public class CreateTreeDto {
    @NotEmpty(message = "Subject cant't be empty string or null!!")
    private String subject;
    @NotNull(message = "Priority can't be null!")
    private MessagePriority priority;
    @MessageTags
    private List<String> tags;

    public CreateTreeDto() {
        this.priority = MessagePriority.NORMAL;
    }

    public CreateTreeDto(String subject) {
        this();
        this.subject = subject;
    }

    public CreateTreeDto(String subject, MessagePriority priority, List<String> tags) {
        this(subject);
        this.priority = priority;
        this.tags = tags;
    }
}
