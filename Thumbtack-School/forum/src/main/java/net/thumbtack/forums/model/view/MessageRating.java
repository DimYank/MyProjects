package net.thumbtack.forums.model.view;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MessageRating {
    private long id;
    private double rating;

    public MessageRating() {
    }

    public MessageRating(long id, double rating) {
        this.id = id;
        this.rating = rating;
    }
}
