package net.thumbtack.forums.dto.request.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.thumbtack.forums.dto.validation.Password;
import net.thumbtack.forums.dto.validation.UserName;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ChangePassDto {
    @UserName
    private String name;
    @Password
    private String oldPassword;
    @Password
    private String password;

    public ChangePassDto() {
    }

    public ChangePassDto(String name, String oldPassword, String password) {
        this.name = name;
        this.oldPassword = oldPassword;
        this.password = password;
    }
}
