package net.thumbtack.forums.model.view;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserRating {
    private long id;
    private String name;
    private double rating;

    public UserRating() {
    }

    public UserRating(long id, String name, double rating) {
        this.id = id;
        this.name = name;
        this.rating = rating;
    }
}
