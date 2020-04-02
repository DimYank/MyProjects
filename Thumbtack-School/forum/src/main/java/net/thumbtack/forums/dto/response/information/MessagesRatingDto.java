package net.thumbtack.forums.dto.response.information;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MessagesRatingDto {
    private long id;
    private double rating;

    public MessagesRatingDto(long id, double rating) {
        this.id = id;
        this.rating = rating;
    }
}
