package net.thumbtack.forums.dto.response.information;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserRatingDto {
    private long id;
    private String name;
    private double rating;

    public UserRatingDto(long id, String name, double rating) {
        this.id = id;
        this.name = name;
        this.rating = rating;
    }
}
