package net.thumbtack.forums.service;

import net.thumbtack.forums.database.dao.iface.ForumDao;
import net.thumbtack.forums.database.dao.iface.MessageDao;
import net.thumbtack.forums.database.dao.iface.MessageInfoDao;
import net.thumbtack.forums.database.dao.iface.SessionDao;
import net.thumbtack.forums.dto.request.message.*;
import net.thumbtack.forums.dto.response.message.CreateTreeResponseDto;
import net.thumbtack.forums.dto.response.message.EditMessageResponseDto;
import net.thumbtack.forums.dto.response.message.MessageInfoDto;
import net.thumbtack.forums.dto.response.message.PostMessageResponseDto;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.*;
import net.thumbtack.forums.model.enums.*;
import net.thumbtack.forums.model.view.CommentInfo;
import net.thumbtack.forums.model.view.MessageInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MessageServiceTest {

    @MockBean
    private MessageDao messageDao;
    @MockBean
    private MessageInfoDao messageInfoDao;
    @MockBean
    private ForumDao forumDao;
    @MockBean
    private SessionDao sessionDao;

    @Autowired
    private MessageService messageService;

    @Test
    void testPostMessage() throws ServerException {
        User user = new User("dima", "pass");
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        Forum forum = new Forum("forum", user, ForumType.UNMODERATED);
        when(forumDao.getById(anyInt())).thenReturn(forum);
        Message message = new Message(user, Collections.singletonList(new MessageHistory("test")));
        MessageTree messageTree = new MessageTree(forum, "subj", message);
        when(messageDao.insert(any(MessageTree.class))).thenReturn(messageTree);

        PostMessageDto postMessageDto = new PostMessageDto("subj", "test");
        PostMessageResponseDto responseDto = messageService.postMessage(postMessageDto, 1, session.getCookie());
        assertEquals(MessageState.PUBLISHED , responseDto.getState());
        verify(messageDao, times(1)).insert(any(MessageTree.class));
    }

    @Test
    void testFailPostMessageBanned() throws ServerException {
        User user = new User("dima", "pass");
        user.setStatus(BanStatus.LIMITED);
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);

        PostMessageDto postMessageDto = new PostMessageDto("subj", "test");
        assertThrows(ServerException.class, ()->messageService.postMessage(postMessageDto, 1, session.getCookie()));
    }

    @Test
    void testPostComment() throws ServerException{
        User user = new User("dima", "pass");
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        Forum forum = new Forum("forum", user, ForumType.UNMODERATED);
        when(forumDao.getById(anyInt())).thenReturn(forum);
        MessageHistory messageHistory = new MessageHistory("test");
        messageHistory.setState(MessageState.PUBLISHED);
        Message message = new Message(user, Collections.singletonList(messageHistory));
        MessageTree messageTree = new MessageTree(forum, "subj", message);
        when(messageDao.getMessageTree(anyLong())).thenReturn(messageTree);
        Message comment = new Message(user, Collections.singletonList(new MessageHistory("test")));
        when(messageDao.getMessage(anyLong())).thenReturn(message);
        when(messageDao.insertComment(any(Message.class))).thenReturn(comment);

        MessageBodyDto messageBodyDto = new MessageBodyDto("comment");
        PostMessageResponseDto responseDto = messageService.postComment(messageBodyDto, 1, session.getCookie());
        assertEquals(MessageState.PUBLISHED , responseDto.getState());
        verify(messageDao, times(1)).insertComment(any(Message.class));
    }

    @Test
    void testDeleteMessageAsMessage() throws ServerException{
        User user = new User("dima", "pass");
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        Forum forum = new Forum("forum", user, ForumType.UNMODERATED);
        when(forumDao.getById(anyInt())).thenReturn(forum);
        MessageHistory messageHistory = new MessageHistory("test");
        messageHistory.setState(MessageState.PUBLISHED);
        Message message = new Message(user, Collections.singletonList(messageHistory));
        MessageTree messageTree = new MessageTree(forum, "subj", message);
        message.setTree(messageTree);
        message.setParent(message);
        when(messageDao.getMessage(anyLong())).thenReturn(message);

        messageService.deleteMessage(1, session.getCookie());
        verify(messageDao, times(1)).deleteMessage(any(Message.class));
        verify(messageDao, never()).deleteComment(any(Message.class));
    }

    @Test
    void testDeleteMessageAsComment() throws ServerException{
        User user = new User("dima", "pass");
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        Forum forum = new Forum("forum", user, ForumType.UNMODERATED);
        when(forumDao.getById(anyInt())).thenReturn(forum);
        MessageHistory messageHistory = new MessageHistory("test");
        messageHistory.setState(MessageState.PUBLISHED);
        Message message = new Message(user, Collections.singletonList(messageHistory));
        MessageTree messageTree = new MessageTree(forum, "subj", message);
        message.setTree(messageTree);
        Message comment = new Message(user, Collections.singletonList(new MessageHistory("test")));
        comment.setId(1);
        comment.setTree(messageTree);
        message.setComments(Collections.singletonList(comment));
        when(messageDao.getMessage(anyLong())).thenReturn(comment);

        messageService.deleteMessage(1, session.getCookie());
        verify(messageDao, never()).deleteMessage(any(Message.class));
        verify(messageDao, times(1)).deleteComment(any(Message.class));
    }

    @Test
    void testFailDeleteMessageNotAuthor() throws ServerException {
        User user = new User("dima", "pass");
        Forum forum = new Forum("forum", user, ForumType.UNMODERATED);
        MessageHistory messageHistory = new MessageHistory("test");
        Message message = new Message(user, Collections.singletonList(messageHistory));
        MessageTree messageTree = new MessageTree(forum, "subj", message);
        message.setTree(messageTree);
        when(messageDao.getMessage(anyLong())).thenReturn(message);
        User anotherUser = new User("vova", "pass");
        anotherUser.setId(1);
        Session anotherSession = new Session(UUID.randomUUID().toString(), anotherUser);
        when(sessionDao.getByCookie(anotherSession.getCookie())).thenReturn(anotherSession);

        assertThrows(ServerException.class, ()->messageService.deleteMessage(1, anotherSession.getCookie()));
    }

    @Test
    void testEditMessageUnpublished() throws ServerException {
        User user = new User("dima", "pass");
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        Forum forum = new Forum("forum", user, ForumType.UNMODERATED);
        when(forumDao.getById(anyInt())).thenReturn(forum);
        List<MessageHistory> historyList = new ArrayList<>();
        historyList.add(new MessageHistory("test"));
        Message message = new Message(user, historyList);
        MessageTree messageTree = new MessageTree(forum, "subj", message);
        message.setTree(messageTree);
        when(messageDao.getMessage(anyLong())).thenReturn(message);

        MessageBodyDto messageBodyDto = new MessageBodyDto("edit");
        EditMessageResponseDto responseDto = messageService.editMessage(1, messageBodyDto, session.getCookie());
        assertEquals(MessageState.UNPUBLISHED, responseDto.getState());
        verify(messageDao, times(1)).editUnpublished(any(Message.class));
        verify(messageDao, never()).editPublished(any(Message.class));
    }

    @Test
    void testEditMessagePublished() throws ServerException {
        User user = new User("dima", "pass");
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        Forum forum = new Forum("forum", user, ForumType.UNMODERATED);
        when(forumDao.getById(anyInt())).thenReturn(forum);
        MessageHistory history = new MessageHistory("test");
        history.setState(MessageState.PUBLISHED);
        List<MessageHistory> historyList = new ArrayList<>();
        historyList.add(history);
        Message message = new Message(user, historyList);
        MessageTree messageTree = new MessageTree(forum, "subj", message);
        message.setTree(messageTree);
        when(messageDao.getMessage(anyLong())).thenReturn(message);

        MessageBodyDto messageBodyDto = new MessageBodyDto("edit");
        EditMessageResponseDto responseDto = messageService.editMessage(1, messageBodyDto, session.getCookie());
        assertEquals(MessageState.PUBLISHED, responseDto.getState());
        verify(messageDao, never()).editUnpublished(any(Message.class));
        verify(messageDao, times(1)).editPublished(any(Message.class));
    }

    @Test
    void testRateMessagePut() throws ServerException {
        User user = new User("dima", "pass");
        Forum forum = new Forum("forum", user, ForumType.UNMODERATED);
        when(forumDao.getById(anyInt())).thenReturn(forum);
        MessageHistory history = new MessageHistory("test");
        history.setState(MessageState.PUBLISHED);
        List<MessageHistory> historyList = new ArrayList<>();
        historyList.add(history);
        Message message = new Message(user, historyList);
        MessageTree messageTree = new MessageTree(forum, "subj", message);
        message.setTree(messageTree);
        when(messageDao.getMessage(anyLong())).thenReturn(message);
        User anotherUser = new User("vova", "pass");
        anotherUser.setId(1);
        Session session = new Session(UUID.randomUUID().toString(), anotherUser);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);

        MessageRatingDto messageRatingDto = new MessageRatingDto((short)3);
        messageService.rateMessage(1, messageRatingDto, session.getCookie());
        verify(messageDao, times(1)).putRating(any(Rating.class), any(Message.class));
        verify(messageDao, never()).deleteRating(any(User.class), any(Message.class));
    }

    @Test
    void testRateMessageDelete() throws ServerException {
        User user = new User("dima", "pass");
        Forum forum = new Forum("forum", user, ForumType.UNMODERATED);
        when(forumDao.getById(anyInt())).thenReturn(forum);
        MessageHistory history = new MessageHistory("test");
        history.setState(MessageState.PUBLISHED);
        List<MessageHistory> historyList = new ArrayList<>();
        historyList.add(history);
        Message message = new Message(user, historyList);
        MessageTree messageTree = new MessageTree(forum, "subj", message);
        message.setTree(messageTree);
        when(messageDao.getMessage(anyLong())).thenReturn(message);
        User anotherUser = new User("vova", "pass");
        anotherUser.setId(1);
        Session session = new Session(UUID.randomUUID().toString(), anotherUser);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);

        MessageRatingDto messageRatingDto = new MessageRatingDto((short)0);
        messageService.rateMessage(1, messageRatingDto, session.getCookie());
        verify(messageDao, never()).putRating(any(Rating.class), any(Message.class));
        verify(messageDao, times(1)).deleteRating(any(User.class), any(Message.class));
    }

    @Test
    void testFailRateMessageNotPublished() throws ServerException {
        User user = new User("dima", "pass");
        Forum forum = new Forum("forum", user, ForumType.UNMODERATED);
        when(forumDao.getById(anyInt())).thenReturn(forum);
        MessageHistory history = new MessageHistory("test");
        List<MessageHistory> historyList = new ArrayList<>();
        historyList.add(history);
        Message message = new Message(user, historyList);
        MessageTree messageTree = new MessageTree(forum, "subj", message);
        message.setTree(messageTree);
        when(messageDao.getMessage(anyLong())).thenReturn(message);
        User anotherUser = new User("vova", "pass");
        anotherUser.setId(1);
        Session session = new Session(UUID.randomUUID().toString(), anotherUser);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);

        MessageRatingDto messageRatingDto = new MessageRatingDto((short)0);
        assertThrows(ServerException.class, ()->messageService.rateMessage(1, messageRatingDto, session.getCookie()));
    }

    @Test
    void testChangePriority() throws ServerException {
        User user = new User("dima", "pass");
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        Forum forum = new Forum("forum", user, ForumType.UNMODERATED);
        when(forumDao.getById(anyInt())).thenReturn(forum);
        MessageHistory history = new MessageHistory("test");
        history.setState(MessageState.PUBLISHED);
        List<MessageHistory> historyList = new ArrayList<>();
        historyList.add(history);
        Message message = new Message(user, historyList);
        MessageTree messageTree = new MessageTree(forum, "subj", message);
        message.setTree(messageTree);
        when(messageDao.getMessage(anyLong())).thenReturn(message);

        ChangePriorityDto changePriorityDto = new ChangePriorityDto(MessagePriority.HIGH);
        messageService.changePriority(1, changePriorityDto, session.getCookie());
        verify(messageDao, times(1)).updatePriority(eq(MessagePriority.HIGH), any(Message.class));
    }

    @Test
    void getCreateTree() throws ServerException {
        User user = new User("dima", "pass");
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        Forum forum = new Forum("forum", user, ForumType.UNMODERATED);
        when(forumDao.getById(anyInt())).thenReturn(forum);
        MessageHistory history = new MessageHistory("test");
        history.setState(MessageState.PUBLISHED);
        List<MessageHistory> historyList = new ArrayList<>();
        historyList.add(history);
        Message message = new Message(user, historyList);
        message.setId(3);
        MessageTree messageTree = new MessageTree(forum, "subj", message);
        message.setTree(messageTree);
        Message comment = new Message(user, Collections.singletonList(new MessageHistory("comment")));
        comment.setId(1);
        when(messageDao.getMessage(1L)).thenReturn(comment);
        when(messageDao.createTree(any(MessageTree.class))).thenReturn(new MessageTree(forum, "subj", comment));
        comment.setTree(messageTree);
        comment.setParent(message);

        CreateTreeDto createTreeDto = new CreateTreeDto("subj", MessagePriority.HIGH, Arrays.asList("tag1", "tag2"));
        CreateTreeResponseDto treeResponseDto = messageService.createTree(1, createTreeDto, session.getCookie());
        assertEquals(1, treeResponseDto.getId());
        verify(messageDao, times(1)).createTree(any(MessageTree.class));
    }

    @Test
    void testPublishMessageNO() throws ServerException {
        User user = new User("dima", "pass");
        user.setTimeBanExit(LocalDateTime.now());
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        Forum forum = new Forum("forum", user, ForumType.UNMODERATED);
        when(forumDao.getById(anyInt())).thenReturn(forum);
        MessageHistory history = new MessageHistory("test");
        List<MessageHistory> historyList = new ArrayList<>();
        historyList.add(history);
        Message message = new Message(user, historyList);
        MessageTree messageTree = new MessageTree(forum, "subj", message);
        message.setTree(messageTree);
        when(messageDao.getMessage(anyLong())).thenReturn(message);

        PublishMessageDto publishMessageDto = new PublishMessageDto(PublishDecision.NO);
        messageService.publishMessage(0, publishMessageDto, session.getCookie());
        verify(messageDao, times(1)).deleteMessage(any(Message.class));
    }

    @Test
    void testPublishMessageNOComment() throws ServerException {
        User user = new User("dima", "pass");
        user.setTimeBanExit(LocalDateTime.now());
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        Forum forum = new Forum("forum", user, ForumType.UNMODERATED);
        when(forumDao.getById(anyInt())).thenReturn(forum);
        MessageHistory history = new MessageHistory("test");
        List<MessageHistory> historyList = new ArrayList<>();
        historyList.add(history);
        Message message = new Message(user, historyList);
        MessageTree messageTree = new MessageTree(forum, "subj", message);
        message.setTree(messageTree);
        Message comment = new Message(user, Collections.singletonList(new MessageHistory("comment")));
        comment.setId(1);
        comment.setTree(messageTree);
        comment.setParent(message);
        when(messageDao.getMessage(1L)).thenReturn(comment);

        PublishMessageDto publishMessageDto = new PublishMessageDto(PublishDecision.NO);
        messageService.publishMessage(1, publishMessageDto, session.getCookie());
        verify(messageDao, times(1)).deleteComment(any(Message.class));
    }

    @Test
    void testPublishMessageNOMultipleHistories() throws ServerException {
        User user = new User("dima", "pass");
        user.setTimeBanExit(LocalDateTime.now());
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        Forum forum = new Forum("forum", user, ForumType.UNMODERATED);
        when(forumDao.getById(anyInt())).thenReturn(forum);
        MessageHistory history1 = new MessageHistory("test");
        history1.setState(MessageState.PUBLISHED);
        MessageHistory history2 = new MessageHistory("test");
        List<MessageHistory> historyList = new ArrayList<>();
        historyList.add(history1);
        historyList.add(history2);
        Message message = new Message(user, historyList);
        MessageTree messageTree = new MessageTree(forum, "subj", message);
        message.setTree(messageTree);
        when(messageDao.getMessage(0)).thenReturn(message);

        PublishMessageDto publishMessageDto = new PublishMessageDto(PublishDecision.NO);
        messageService.publishMessage(0, publishMessageDto, session.getCookie());
        verify(messageDao, times(1)).deleteUnpublished(any(Message.class));
    }

    @Test
    void testPublishMessageYES() throws ServerException {
        User user = new User("dima", "pass");
        user.setTimeBanExit(LocalDateTime.now());
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        Forum forum = new Forum("forum", user, ForumType.UNMODERATED);
        when(forumDao.getById(anyInt())).thenReturn(forum);
        MessageHistory history = new MessageHistory("test");
        List<MessageHistory> historyList = new ArrayList<>();
        historyList.add(history);
        Message message = new Message(user, historyList);
        MessageTree messageTree = new MessageTree(forum, "subj", message);
        message.setTree(messageTree);
        when(messageDao.getMessage(anyLong())).thenReturn(message);

        PublishMessageDto publishMessageDto = new PublishMessageDto(PublishDecision.YES);
        messageService.publishMessage(0, publishMessageDto, session.getCookie());
        verify(messageDao, times(1)).makePublic(any(Message.class));
    }

    @Test
    void testGetMessageInfo() throws ServerException {
        User user = new User("dima", "pass");
        user.setTimeBanExit(LocalDateTime.now());
        User user2 = new User("dima", "pass");
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        Forum forum = new Forum("forum", user2, ForumType.MODERATED);
        when(forumDao.getById(anyInt())).thenReturn(forum);
        MessageHistory history = new MessageHistory("test");
        List<MessageHistory> historyList = new ArrayList<>();
        historyList.add(history);
        Message message = new Message(user, historyList);
        MessageTree messageTree = new MessageTree(forum, "subj", message);
        message.setTree(messageTree);
        when(messageDao.getMessage(1)).thenReturn(message);

        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setId(1);
        MessageHistory messageHistory1 = new MessageHistory("sda");
        List<MessageHistory> historyList1 = new ArrayList<>();
        historyList1.add(messageHistory1);
        messageInfo.setHistory(historyList1);

        CommentInfo commentInfo1 = new CommentInfo();
        commentInfo1.setId(2);
        MessageHistory messageHistory2 = new MessageHistory("sda");
        List<MessageHistory> historyList2 = new ArrayList<>();
        historyList2.add(messageHistory2);
        commentInfo1.setHistory(historyList2);

        CommentInfo commentInfo2 = new CommentInfo();
        commentInfo2.setId(3);
        MessageHistory messageHistory3 = new MessageHistory("sda");
        messageHistory3.setState(MessageState.PUBLISHED);
        List<MessageHistory> historyList3 = new ArrayList<>();
        historyList3.add(messageHistory3);
        commentInfo2.setHistory(historyList3);

        messageInfo.setComments(Arrays.asList(commentInfo1, commentInfo2));
        when(messageInfoDao.getById(anyLong())).thenReturn(messageInfo);
        when(messageInfoDao.getHistory(messageInfo.getId(), true, true)).thenReturn(historyList1);
        when(messageInfoDao.getHistory(commentInfo1.getId(), true, true)).thenReturn(historyList2);
        when(messageInfoDao.getHistory(commentInfo2.getId(), true, true)).thenReturn(historyList3);
        when(messageInfoDao.getCommentInfo(messageInfo.getId(), OrderValue.DESC)).thenReturn(Arrays.asList(commentInfo1, commentInfo2));

        MessageInfoDto messageInfoDto = messageService.getMessageInfo(1, true, false,
                true, OrderValue.DESC, session.getCookie());
        assertAll(
                ()->assertEquals(2, messageInfoDto.getComments().size()),
                ()->assertEquals(0, messageInfoDto.getBody().get(0).indexOf("[UNPUBLISHED]")),
                ()->assertEquals(0, messageInfoDto.getComments().get(0).getBody().get(0).indexOf("[UNPUBLISHED]")),
                ()->assertEquals(-1, messageInfoDto.getComments().get(1).getBody().get(0).indexOf("[UNPUBLISHED]"))
        );
    }

    @Test
    void testGetAllMessageInfo() throws ServerException {
        User user = new User("dima", "pass");
        user.setTimeBanExit(LocalDateTime.now());
        User user2 = new User("dima", "pass");
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        Forum forum = new Forum("forum", user2, ForumType.MODERATED);
        when(forumDao.getById(anyInt())).thenReturn(forum);
        MessageHistory history = new MessageHistory("test");
        List<MessageHistory> historyList = new ArrayList<>();
        historyList.add(history);
        Message message = new Message(user, historyList);
        MessageTree messageTree = new MessageTree(forum, "subj", message);
        message.setTree(messageTree);
        when(messageDao.getMessage(1)).thenReturn(message);

        MessageInfo messageInfo1 = new MessageInfo();
        messageInfo1.setId(1);
        MessageHistory messageHistory1 = new MessageHistory("sda");
        List<MessageHistory> historyList1 = new ArrayList<>();
        historyList1.add(messageHistory1);
        messageInfo1.setHistory(historyList1);

        MessageInfo messageInfo2 = new MessageInfo();
        messageInfo2.setId(2);
        MessageHistory messageHistory4 = new MessageHistory("sda");
        List<MessageHistory> historyList4 = new ArrayList<>();
        historyList4.add(messageHistory4);
        messageInfo2.setHistory(historyList4);

        CommentInfo commentInfo1 = new CommentInfo();
        commentInfo1.setId(3);
        MessageHistory messageHistory2 = new MessageHistory("sda");
        List<MessageHistory> historyList2 = new ArrayList<>();
        historyList2.add(messageHistory2);
        commentInfo1.setHistory(historyList2);

        CommentInfo commentInfo2 = new CommentInfo();
        commentInfo2.setId(4);
        MessageHistory messageHistory3 = new MessageHistory("sda");
        messageHistory3.setState(MessageState.PUBLISHED);
        List<MessageHistory> historyList3 = new ArrayList<>();
        historyList3.add(messageHistory3);
        commentInfo2.setHistory(historyList3);

        messageInfo1.setComments(Arrays.asList(commentInfo1, commentInfo2));
        when(messageInfoDao.getAllByForumId(1, null, OrderValue.DESC, 0 ,0)).thenReturn(Arrays.asList(messageInfo1, messageInfo2));
        when(messageInfoDao.getHistory(messageInfo1.getId(), true, true)).thenReturn(historyList1);
        when(messageInfoDao.getHistory(messageInfo2.getId(), true, true)).thenReturn(historyList4);
        when(messageInfoDao.getHistory(commentInfo1.getId(), true, true)).thenReturn(historyList2);
        when(messageInfoDao.getHistory(commentInfo2.getId(), true, true)).thenReturn(historyList3);
        when(messageInfoDao.getCommentInfo(messageInfo1.getId(), OrderValue.DESC)).thenReturn(Arrays.asList(commentInfo1, commentInfo2));

        List<MessageInfoDto> messageInfoDto = messageService.getAllMessagesInfo(1, true, false,
                true, null, OrderValue.DESC, 0, 0, session.getCookie());
        assertAll(
                ()->assertEquals(2, messageInfoDto.size()),
                ()->assertEquals(2, messageInfoDto.get(0).getComments().size()),
                ()->assertEquals(0, messageInfoDto.get(0).getBody().get(0).indexOf("[UNPUBLISHED]")),
                ()->assertEquals(0, messageInfoDto.get(1).getBody().get(0).indexOf("[UNPUBLISHED]")),
                ()->assertEquals(0, messageInfoDto.get(0).getComments().get(0).getBody().get(0).indexOf("[UNPUBLISHED]")),
                ()->assertEquals(-1, messageInfoDto.get(0).getComments().get(1).getBody().get(0).indexOf("[UNPUBLISHED]"))
        );
    }
}
