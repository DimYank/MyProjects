package net.thumbtack.forums.controller;

import net.thumbtack.forums.dto.request.message.*;
import net.thumbtack.forums.dto.response.message.CreateTreeResponseDto;
import net.thumbtack.forums.dto.response.message.EditMessageResponseDto;
import net.thumbtack.forums.dto.response.message.MessageInfoDto;
import net.thumbtack.forums.dto.response.message.PostMessageResponseDto;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.enums.OrderValue;
import net.thumbtack.forums.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
public class MessageController {

    private MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping(path = "/forums/{forumId}/messages", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PostMessageResponseDto postMessage(
            @CookieValue("JAVASESSIONID") String cookie,
            @PathVariable("forumId") @Min(1) int forumId,
            @RequestBody @Valid PostMessageDto messageDto
    ) throws ServerException {
        return messageService.postMessage(messageDto, forumId, cookie);
    }

    @PostMapping(path = "/messages/{messageId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PostMessageResponseDto postComment(
            @CookieValue("JAVASESSIONID") String cookie,
            @PathVariable("messageId") @Min(1) long messageId,
            @RequestBody @Valid MessageBodyDto messageDto
    ) throws ServerException {
        return messageService.postComment(messageDto, messageId, cookie);
    }

    @DeleteMapping(path = "/messages/{messageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteMessage(
            @CookieValue("JAVASESSIONID") String cookie,
            @PathVariable("messageId") @Min(1) long messageId
    ) throws ServerException {
        messageService.deleteMessage(messageId, cookie);
        return "{}";
    }

    @PutMapping(path = "/messages/{messageId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public EditMessageResponseDto editMessage(
            @CookieValue("JAVASESSIONID") String cookie,
            @PathVariable("messageId") @Min(1) long messageId,
            @RequestBody @Valid MessageBodyDto messageDto
    ) throws ServerException {
        return messageService.editMessage(messageId, messageDto, cookie);
    }

    @PutMapping(path = "/messages/{messageId}/priority", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String editPriority(
            @CookieValue("JAVASESSIONID") String cookie,
            @PathVariable("messageId") @Min(1) long messageId,
            @RequestBody @Valid ChangePriorityDto priorityDto
    ) throws ServerException {
        messageService.changePriority(messageId, priorityDto, cookie);
        return "{}";
    }

    @PutMapping(path = "/messages/{messageId}/up", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CreateTreeResponseDto createTree(
            @CookieValue("JAVASESSIONID") String cookie,
            @PathVariable("messageId") @Min(1) long messageId,
            @RequestBody @Valid CreateTreeDto treeDto
    ) throws ServerException {
        return messageService.createTree(messageId, treeDto, cookie);
    }

    @PutMapping(path = "/messages/{messageId}/publish", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String publishMessage(
            @CookieValue("JAVASESSIONID") String cookie,
            @PathVariable("messageId") @Min(1) long messageId,
            @RequestBody @Valid PublishMessageDto publishDto
    ) throws ServerException {
        messageService.publishMessage(messageId, publishDto, cookie);
        return "{}";
    }

    @PostMapping(path = "/messages/{messageId}/rating", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String rateMessage(
            @CookieValue("JAVASESSIONID") String cookie,
            @PathVariable("messageId") @Min(1) long messageId,
            @RequestBody @Valid MessageRatingDto ratingDto
    ) throws ServerException {
        messageService.rateMessage(messageId, ratingDto, cookie);
        return "{}";
    }

    @GetMapping(path = "/messages/{messageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public MessageInfoDto getMessage(
            @CookieValue("JAVASESSIONID") String cookie,
            @PathVariable("messageId") @Min(1) long messageId,
            @RequestParam(name = "allversions", defaultValue = "false") boolean allVersions,
            @RequestParam(name = "nocomments", defaultValue = "false") boolean noComments,
            @RequestParam(name = "unpublished", defaultValue = "false") boolean unpublished,
            @RequestParam(name = "order", defaultValue = "DESC") OrderValue order
    ) throws ServerException {
        return messageService.getMessageInfo(messageId, allVersions, noComments, unpublished, order, cookie);
    }

    @GetMapping(path = "/forums/{forumId}/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MessageInfoDto> getForumMessages(
            @CookieValue("JAVASESSIONID") String cookie,
            @PathVariable("forumId") @Min(1) int forumId,
            @RequestParam(name = "allversions", defaultValue = "false") boolean allVersions,
            @RequestParam(name = "nocomments", defaultValue = "false") boolean noComments,
            @RequestParam(name = "unpublished", defaultValue = "false") boolean unpublished,
            @RequestParam(name = "tags", required = false) List<String> tags,
            @RequestParam(name = "order", defaultValue = "DESC") OrderValue order,
            @RequestParam(name = "offset", defaultValue = "0") long offset,
            @RequestParam(name = "limit", defaultValue = "1000") int limit
    ) throws ServerException {
        return messageService.getAllMessagesInfo(forumId, allVersions, noComments, unpublished, tags, order, offset, limit, cookie);
    }
}
