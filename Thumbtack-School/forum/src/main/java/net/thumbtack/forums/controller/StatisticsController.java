package net.thumbtack.forums.controller;

import net.thumbtack.forums.dto.response.information.MessageCountDto;
import net.thumbtack.forums.dto.response.information.MessagesRatingDto;
import net.thumbtack.forums.dto.response.information.ServerSettingsDto;
import net.thumbtack.forums.dto.response.information.UserRatingDto;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
public class StatisticsController {

    private StatisticsService statisticsService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("settings")
    public ServerSettingsDto getSettings(@CookieValue(name = "JAVASESSIONID", required = false) String cookie) throws ServerException {
        return statisticsService.getServerSettings(cookie);
    }

    @GetMapping("/stats/messages")
    public MessageCountDto getMessageAndCommentCount(
            @CookieValue("JAVASESSIONID") String cookie,
            @RequestParam(name = "forumId", defaultValue = "0") @Min(0) int forumId
    ) throws ServerException {
        return statisticsService.getMessageAndCommentCount(forumId, cookie);
    }

    @GetMapping("/stats/messages/rating")
    public List<MessagesRatingDto> getMessagesRating(
            @CookieValue("JAVASESSIONID") String cookie,
            @RequestParam(name = "forumId", defaultValue = "0") @Min(0) int forumId,
            @RequestParam(name = "forMessages", defaultValue = "true") boolean forMessage,
            @RequestParam(name = "offset", defaultValue = "0") @Min(0) long offset,
            @RequestParam(name = "limit", defaultValue = "1000") @Min(0) int limit
    ) throws ServerException {
        return statisticsService.getRating(forumId, forMessage, offset, limit, cookie);
    }

    @GetMapping("/stats/users/rating")
    public List<UserRatingDto> getUsersRating(
            @CookieValue("JAVASESSIONID") String cookie,
            @RequestParam(name = "forumId", defaultValue = "0") @Min(0) int forumId,
            @RequestParam(name = "offset", defaultValue = "0") @Min(0) long offset,
            @RequestParam(name = "limit", defaultValue = "1000") @Min(0) int limit
    ) throws ServerException {
        return statisticsService.getUsersRating(forumId, offset, limit, cookie);
    }
}
