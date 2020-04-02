package net.thumbtack.forums.dto.response.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserActionResponseDto {
    private long id;
    private String name;
    private String email;

    public UserActionResponseDto(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
