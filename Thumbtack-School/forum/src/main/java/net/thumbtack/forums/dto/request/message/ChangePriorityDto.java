package net.thumbtack.forums.dto.request.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.thumbtack.forums.model.enums.MessagePriority;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class ChangePriorityDto {
    @NotNull(message = "Priority can't be null!")
    private MessagePriority priority;

    public ChangePriorityDto() {
    }

    public ChangePriorityDto(MessagePriority priority) {
        this.priority = priority;
    }
}
