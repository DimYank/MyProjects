package net.thumbtack.forums.controller;

import net.thumbtack.forums.dto.request.user.LoginDto;
import net.thumbtack.forums.dto.response.user.UserActionResponseDto;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/session")
@CrossOrigin(origins = "*")
public class SessionController {

    private UserService userService;

    @Autowired
    public SessionController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserActionResponseDto loginUser(@RequestBody @Valid LoginDto loginDto, HttpServletResponse response) throws ServerException {
        String cookie = UUID.randomUUID().toString();
        UserActionResponseDto responseDto = userService.login(loginDto, cookie);
        response.addCookie(new Cookie("JAVASESSIONID", cookie));
        return responseDto;
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String logoutUser(@CookieValue("JAVASESSIONID") String cookie) throws ServerException {
        userService.logout(cookie);
        return "{}";
    }
}
