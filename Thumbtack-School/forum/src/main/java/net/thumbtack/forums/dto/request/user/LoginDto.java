package net.thumbtack.forums.dto.request.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.thumbtack.forums.dto.validation.Password;
import net.thumbtack.forums.dto.validation.UserName;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class LoginDto {

    @NotNull
    private String name;
    @NotNull
    private String password;

    public LoginDto() {
    }

    public LoginDto(String name, String password) {
        this.name = name;
        this.password = password;
    }
}
