package net.thumbtack.forums.model.view;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.thumbtack.forums.model.enums.BanStatus;
import net.thumbtack.forums.model.enums.UserType;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class UserInfo {
    private long id;
    private String name;
    private LocalDateTime timeRegistered;
    private boolean online;
    private boolean deleted;
    private BanStatus status;
    private LocalDateTime timeBanExit;
    private int banCount;
    private String email;
    private UserType userType;

    public UserInfo() {
    }
}
