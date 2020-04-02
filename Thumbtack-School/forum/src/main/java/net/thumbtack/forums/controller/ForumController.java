package net.thumbtack.forums.controller;

import net.thumbtack.forums.dto.request.forum.CreateForumDto;
import net.thumbtack.forums.dto.response.forum.CreateForumResponseDto;
import net.thumbtack.forums.dto.response.forum.ForumInfoDto;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/api/forums")
@Validated
public class ForumController {

    private ForumService forumService;

    @Autowired
    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CreateForumResponseDto createForum(
            @RequestBody @Valid CreateForumDto createDto,
            @CookieValue("JAVASESSIONID") String cookie
    ) throws ServerException {
        return forumService.createForum(createDto, cookie);
    }

    @DeleteMapping(path = "/{forumId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteForum(
            @CookieValue("JAVASESSIONID") String cookie,
            @PathVariable("forumId") @Min(1) int forumId
    ) throws ServerException {
        forumService.deleteForum(forumId, cookie);
        return "{}";
    }

    @GetMapping(path = "/{forumId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ForumInfoDto getForumInfo(
            @CookieValue("JAVASESSIONID") String cookie,
            @PathVariable("forumId") @Min(1) int forumId
    ) throws ServerException {
        return forumService.getForumById(forumId, cookie);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ForumInfoDto> getForumsInfo(@CookieValue("JAVASESSIONID") String cookie) throws ServerException {
        return forumService.getAllForums(cookie);
    }

}
