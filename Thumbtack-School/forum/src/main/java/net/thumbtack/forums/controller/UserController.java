package net.thumbtack.forums.controller;

import net.thumbtack.forums.dto.request.user.ChangePassDto;
import net.thumbtack.forums.dto.request.user.RegisterDto;
import net.thumbtack.forums.dto.response.user.UserActionResponseDto;
import net.thumbtack.forums.dto.response.user.UserInfoDto;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserActionResponseDto registerUser(
            @RequestBody @Valid RegisterDto registerDto,
            HttpServletResponse response
    ) throws ServerException {
        String cookie = UUID.randomUUID().toString();
        UserActionResponseDto dto = userService.registerUser(registerDto, cookie);
        response.addCookie(new Cookie("JAVASESSIONID", cookie));
        return dto;
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteUser(@CookieValue("JAVASESSIONID") String cookie) throws ServerException {
        userService.deleteUser(cookie);
        return "{}";
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserActionResponseDto changePassword(
            @CookieValue("JAVASESSIONID") String cookie,
            @RequestBody @Valid ChangePassDto passDto,
            HttpServletResponse response
    ) throws ServerException {
        UserActionResponseDto dto = userService.changePassword(passDto, cookie);
        response.addCookie(new Cookie("JAVASESSIONID", cookie));
        return dto;
    }

    @PutMapping(path = "/{userId}/super", produces = MediaType.APPLICATION_JSON_VALUE)
    public String updateToSuper(
            @CookieValue("JAVASESSIONID") String cookie,
            @PathVariable("userId") @Min(1) long userId
    ) throws ServerException {
        userService.updateToSuper(userId, cookie);
        return "{}";
    }

    @PostMapping(path = "/{userId}/restrict")
    public String banUser(
            @CookieValue("JAVASESSIONID") String cookie,
            @PathVariable("userId") @Min(1) long userId
    ) throws ServerException {
        userService.banUser(userId, cookie);
        return "{}";
    }

    @GetMapping
    public List<UserInfoDto> getUsersList(@CookieValue("JAVASESSIONID") String cookie) throws ServerException {
        return userService.getUsersInfo(cookie);
    }
}
