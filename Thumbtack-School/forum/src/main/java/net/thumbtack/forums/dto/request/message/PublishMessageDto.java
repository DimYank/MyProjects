package net.thumbtack.forums.dto.request.message;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class PublishMessageDto {
    @NotNull(message = "Decision can't be null!")
    private PublishDecision decision;

    public PublishMessageDto() {
    }

    public PublishMessageDto(PublishDecision decision) {
        this.decision = decision;
    }
}
