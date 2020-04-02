package net.thumbtack.forums.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Session {
    private String cookie;
    private User user;

    public Session() {
    }

    public Session(String cookie, User user) {
        this.cookie = cookie;
        this.user = user;
    }
}

