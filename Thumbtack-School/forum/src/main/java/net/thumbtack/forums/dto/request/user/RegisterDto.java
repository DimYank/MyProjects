package net.thumbtack.forums.dto.request.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.thumbtack.forums.dto.validation.Email;
import net.thumbtack.forums.dto.validation.Password;
import net.thumbtack.forums.dto.validation.UserName;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class RegisterDto {
    @UserName
    private String name;
    @Email
    private String email;
    @Password
    private String password;

    public RegisterDto() {
    }

    public RegisterDto(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
