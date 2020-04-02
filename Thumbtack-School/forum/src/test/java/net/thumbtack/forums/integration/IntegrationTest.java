package net.thumbtack.forums.integration;

import net.thumbtack.forums.debug.DebugService;
import net.thumbtack.forums.dto.request.forum.CreateForumDto;
import net.thumbtack.forums.dto.request.message.*;
import net.thumbtack.forums.dto.request.user.LoginDto;
import net.thumbtack.forums.dto.request.user.RegisterDto;
import net.thumbtack.forums.dto.response.information.MessageCountDto;
import net.thumbtack.forums.dto.response.information.MessagesRatingDto;
import net.thumbtack.forums.dto.response.information.UserRatingDto;
import net.thumbtack.forums.dto.response.message.MessageInfoDto;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.enums.ForumType;
import net.thumbtack.forums.model.enums.MessagePriority;
import net.thumbtack.forums.model.enums.OrderValue;
import net.thumbtack.forums.service.ForumService;
import net.thumbtack.forums.service.StatisticsService;
import net.thumbtack.forums.service.MessageService;
import net.thumbtack.forums.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class IntegrationTest extends IntegrationTestBase {

    private UserService userService;
    private ForumService forumService;
    private MessageService messageService;
    private StatisticsService statisticsService;

    @Autowired
    public IntegrationTest(DebugService debugService, UserService userService, ForumService forumService, MessageService messageService, StatisticsService statisticsService) {
        super(debugService);
        this.userService = userService;
        this.forumService = forumService;
        this.messageService = messageService;
        this.statisticsService = statisticsService;
    }

    @Test
    void testOne() throws ServerException, InterruptedException {
        //User registration
        RegisterDto registerDto1 = new RegisterDto("dima", "em@em.com", "pass");
        RegisterDto registerDto2 = new RegisterDto("vova", "em@em.com", "pass");
        RegisterDto registerDto3 = new RegisterDto("vasya", "em@em.com", "pass");
        LoginDto loginDto = new LoginDto("Admin", "administrator");
        String cookie1 = UUID.randomUUID().toString();
        String cookie2 = UUID.randomUUID().toString();
        String cookie3 = UUID.randomUUID().toString();
        String adminCookie = UUID.randomUUID().toString();
        userService.registerUser(registerDto1, cookie1);
        userService.registerUser(registerDto2, cookie2);
        long userId = userService.registerUser(registerDto3, cookie3).getId();
        userService.login(loginDto, adminCookie);

        //Forum creation
        CreateForumDto createForumDto1 = new CreateForumDto("Forum1", ForumType.MODERATED);
        CreateForumDto createForumDto2 = new CreateForumDto("Forum2", ForumType.UNMODERATED);
        int moderatedID = forumService.createForum(createForumDto1, cookie1).getId();
        int unmoderatedID = forumService.createForum(createForumDto2, cookie2).getId();

        //Messages creation
        PostMessageDto postMessageDto1 = new PostMessageDto("subj1", "body1", MessagePriority.HIGH, Arrays.asList("tag1", "tag2"));
        PostMessageDto postMessageDto2 = new PostMessageDto("subj2", "body2");
        PostMessageDto postMessageDto3 = new PostMessageDto("subj3", "body3", MessagePriority.LOW, Collections.singletonList("tag3"));
        PostMessageDto postMessageDto4 = new PostMessageDto("subj4", "body4", MessagePriority.NORMAL, Arrays.asList("tag4", "tag2"));
        PostMessageDto postMessageDto5 = new PostMessageDto("subj5", "body5", MessagePriority.HIGH, null);
        long messageID1 = messageService.postMessage(postMessageDto1, moderatedID, cookie1).getId();
        long messageID2 = messageService.postMessage(postMessageDto2, moderatedID, cookie2).getId();
        long messageID3 = messageService.postMessage(postMessageDto3, unmoderatedID, cookie2).getId();
        long messageID4 = messageService.postMessage(postMessageDto4, unmoderatedID, cookie3).getId();
        long messageID5 = messageService.postMessage(postMessageDto5, unmoderatedID, cookie3).getId();

        //Message publication
        PublishMessageDto publishMessageDto = new PublishMessageDto(PublishDecision.YES);
        messageService.publishMessage(messageID2, publishMessageDto, cookie1);

        //Messages operations
        MessageRatingDto messageRatingDto = new MessageRatingDto((short) 1);
        messageService.rateMessage(messageID1, messageRatingDto, cookie2);
        messageRatingDto.setRating((short) 5);
        messageService.rateMessage(messageID2, messageRatingDto, cookie1);
        messageRatingDto.setRating((short) 4);
        messageService.rateMessage(messageID2, messageRatingDto, cookie3);
        messageRatingDto.setRating((short) 2);
        messageService.rateMessage(messageID1, messageRatingDto, cookie3);
        messageRatingDto.setRating((short) 3);
        messageService.rateMessage(messageID3, messageRatingDto, cookie3);
        messageRatingDto.setRating((short) 4);
        messageService.rateMessage(messageID1, messageRatingDto, adminCookie);
        messageRatingDto.setRating((short) 1);
        messageService.rateMessage(messageID4, messageRatingDto, cookie2);
        messageRatingDto.setRating((short) 1);
        messageService.rateMessage(messageID5, messageRatingDto, cookie2);

        MessageBodyDto editDto = new MessageBodyDto("edit1");
        messageService.editMessage(messageID1, editDto, cookie1);
        editDto.setBody("edit2");
        messageService.editMessage(messageID5, editDto, cookie3);

        //logout/login
        userService.logout(cookie2);
        userService.logout(cookie3);
        LoginDto loginDto1 = new LoginDto("dima", "pass");
        userService.login(loginDto1, cookie1);
        LoginDto loginDto2 = new LoginDto("vova", "pass");
        userService.login(loginDto2, cookie2);
        LoginDto loginDto3 = new LoginDto("vasya", "pass");
        userService.login(loginDto3, cookie3);

        //Comment creation
        MessageBodyDto commentDto = new MessageBodyDto("commentFor1-1");
        long comment1_1Id = messageService.postComment(commentDto, messageID1, cookie3).getId();
        commentDto.setBody("comment1-2");
        long comment1_2Id = messageService.postComment(commentDto, messageID1, adminCookie).getId();
        commentDto.setBody("comment2-1");
        long comment2_1Id = messageService.postComment(commentDto, messageID2, cookie1).getId();
        commentDto.setBody("comment2-1-1");
        long comment2_1_1Id = messageService.postComment(commentDto, comment2_1Id, cookie3).getId();
        commentDto.setBody("comment3-1");
        long comment3_1Id = messageService.postComment(commentDto, messageID3, cookie2).getId();
        commentDto.setBody("comment4-1");
        long comment4_1Id = messageService.postComment(commentDto, messageID4, cookie2).getId();

        //Comments operations
        PublishMessageDto publishCommentDto = new PublishMessageDto(PublishDecision.YES);
        messageService.publishMessage(comment1_1Id, publishCommentDto, cookie1);
        messageService.publishMessage(comment1_2Id, publishCommentDto, cookie1);
        messageService.publishMessage(comment2_1_1Id, publishCommentDto, cookie1);

        editDto.setBody("comment edit 1");
        messageService.editMessage(comment3_1Id, editDto, cookie2);
        editDto.setBody("comment edit 2");
        messageService.editMessage(comment4_1Id, editDto, cookie2);
        editDto.setBody("comment edit 3");
        messageService.editMessage(comment2_1_1Id, editDto, cookie3);

        //Users operations
        userService.banUser(userId, adminCookie);
        userService.deleteUser(cookie3);

        //assertions
        List<MessageInfoDto> infoDtoListModerated = messageService.getAllMessagesInfo(moderatedID, true,
                false, true, null, OrderValue.ASC, 0, 100, adminCookie);
        List<MessageInfoDto> infoDtoListUnmoderated = messageService.getAllMessagesInfo(unmoderatedID, true,
                false, true, null, OrderValue.ASC, 0, 100, adminCookie);
        List<MessagesRatingDto> messagesRatingDtoList = statisticsService.getRating(0, true,
                0, 100, adminCookie);
        List<UserRatingDto> userRatingDtoList = statisticsService.getUsersRating(0, 0, 100, adminCookie);
        MessageCountDto messageCountDto = statisticsService.getMessageAndCommentCount(0, adminCookie);

        assertAll(
                () -> assertEquals(messageID1, infoDtoListModerated.get(0).getId()),
                () -> assertEquals(messageID2, infoDtoListModerated.get(1).getId()),
                () -> assertEquals(messageID5, infoDtoListUnmoderated.get(0).getId()),
                () -> assertEquals(messageID4, infoDtoListUnmoderated.get(1).getId()),
                () -> assertEquals(messageID3, infoDtoListUnmoderated.get(2).getId()),
                () -> assertEquals("comment edit 1", infoDtoListUnmoderated.get(2).getComments().get(0).getBody().get(0)),
                () -> assertEquals("comment edit 2", infoDtoListUnmoderated.get(1).getComments().get(0).getBody().get(0)),
                () -> assertEquals("[UNPUBLISHED] comment edit 3", infoDtoListModerated.get(1).getComments()
                        .get(0).getComments().get(0).getBody().get(0)),
                () -> assertEquals(4.5, messagesRatingDtoList.get(0).getRating(), 0.001),
                () -> assertEquals(3, messagesRatingDtoList.get(1).getRating(), 0.001),
                () -> assertEquals(2.333, messagesRatingDtoList.get(2).getRating(), 0.001),
                () -> assertEquals(1, messagesRatingDtoList.get(3).getRating(), 0.001),
                () -> assertEquals(1, messagesRatingDtoList.get(4).getRating(), 0.001),
                () -> assertEquals(4, userRatingDtoList.get(0).getRating(), 0.001),
                () -> assertEquals(2.333, userRatingDtoList.get(1).getRating(), 0.001),
                () -> assertEquals(1, userRatingDtoList.get(2).getRating(), 0.001),
                () -> assertEquals(0, userRatingDtoList.get(3).getRating(), 0.001),
                () -> assertEquals(5, messageCountDto.getMessageCount()),
                () -> assertEquals(6, messageCountDto.getCommentCount())
        );
    }
}
