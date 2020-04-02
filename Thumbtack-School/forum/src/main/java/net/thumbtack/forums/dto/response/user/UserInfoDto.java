package net.thumbtack.forums.dto.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.thumbtack.forums.model.enums.BanStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoDto {
    private long id;
    private String name;
    private LocalDateTime timeRegistered;
    private boolean online;
    private boolean deleted;
    private BanStatus status;
    private LocalDateTime timeBanExit;
    private int banCount;
    private String email;
    private Boolean superr;

    public UserInfoDto(long id, String name, LocalDateTime timeRegistered, boolean online, boolean deleted, BanStatus status, LocalDateTime timeBanExit, int banCount, String email, Boolean superr) {
        this.id = id;
        this.name = name;
        this.timeRegistered = timeRegistered;
        this.online = online;
        this.deleted = deleted;
        this.status = status;
        this.timeBanExit = timeBanExit;
        this.banCount = banCount;
        this.email = email;
        this.superr = superr;
    }
}
