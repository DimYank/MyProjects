package net.thumbtack.forums.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.thumbtack.forums.model.enums.BanStatus;
import net.thumbtack.forums.model.enums.UserType;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class User {

    private long id;
    private String name;
    private String email;
    private String password;
    private LocalDateTime timeRegistered;
    private boolean deleted;
    private UserType userType;
    private BanStatus status;
    private LocalDateTime timeBanExit;
    private int banCount;

    public User() {
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
        this.timeRegistered = LocalDateTime.now();
        this.userType = UserType.REGULAR;
    }

    public User(String name, String email, String password) {
        this(name, password);
        this.email = email;
    }

    public User(long id, String name, String email, String password, LocalDateTime timeRegistered,
                boolean deleted, UserType userType, BanStatus status, LocalDateTime timeBanExit, int banCount) {
        this(name, email, password);
        this.id = id;
        this.timeRegistered = timeRegistered;
        this.deleted = deleted;
        this.userType = userType;
        this.status = status;
        this.timeBanExit = timeBanExit;
        this.banCount = banCount;
    }
}
