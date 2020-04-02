package net.thumbtack.forums.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Rating {
    private long id;
    private User user;
    private short rating;

    public Rating() {
    }

    public Rating(User user, short rating) {
        this.user = user;
        this.rating = rating;
    }
}
