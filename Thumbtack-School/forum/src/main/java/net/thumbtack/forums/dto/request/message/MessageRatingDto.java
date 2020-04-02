package net.thumbtack.forums.dto.request.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
@ToString
public class MessageRatingDto {
    @Min(value = 0, message = "Rating must be in range of 0 to 5!")
    @Max(value = 5, message = "Rating must be in range of 0 to 5!")
    private short rating;

    public MessageRatingDto() {
    }

    public MessageRatingDto(short rating) {
        this.rating = rating;
    }
}
