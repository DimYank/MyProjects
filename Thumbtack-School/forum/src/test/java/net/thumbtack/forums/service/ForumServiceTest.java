package net.thumbtack.forums.service;

import net.thumbtack.forums.database.dao.iface.ForumDao;
import net.thumbtack.forums.database.dao.iface.SessionDao;
import net.thumbtack.forums.dto.request.forum.CreateForumDto;
import net.thumbtack.forums.dto.response.forum.CreateForumResponseDto;
import net.thumbtack.forums.dto.response.forum.ForumInfoDto;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.*;
import net.thumbtack.forums.model.enums.BanStatus;
import net.thumbtack.forums.model.enums.ForumType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ForumServiceTest {
    @MockBean
    private SessionDao sessionDao;
    @MockBean
    private ForumDao forumDao;

    @Autowired
    private ForumService forumService;

    @Test
    void testCreateForum() throws ServerException {
        User user = new User("dima", "pass");
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        CreateForumDto createForumDto = new CreateForumDto("forum", ForumType.UNMODERATED);
        Forum forum = new Forum(createForumDto.getName(), user, createForumDto.getType());
        when(forumDao.insert(forum)).thenReturn(forum);
        CreateForumResponseDto responseDto = forumService.createForum(createForumDto, session.getCookie());
        assertAll(
                () -> assertEquals(forum.getName(), responseDto.getName()),
                () -> assertEquals(forum.getType(), responseDto.getType())
        );
    }

    @Test
    void testFailCreateForumNoSession() throws ServerException {
        User user = new User("dima", "pass");
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(null);
        CreateForumDto createForumDto = new CreateForumDto("forum", ForumType.UNMODERATED);
        assertThrows(ServerException.class, () -> forumService.createForum(createForumDto, session.getCookie()));
    }

    @Test
    void testFailCreateForumBanned() throws ServerException {
        User user = new User("dima", "pass");
        user.setStatus(BanStatus.LIMITED);
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        CreateForumDto createForumDto = new CreateForumDto("forum", ForumType.UNMODERATED);
        assertThrows(ServerException.class, () -> forumService.createForum(createForumDto, session.getCookie()));
    }

    @Test
    void testGetForum() throws ServerException {
        User user = new User("dima", "pass");
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        Forum forum = new Forum(1, "for", user, ForumType.UNMODERATED, false);
        when(forumDao.getById(forum.getId())).thenReturn(forum);
        ForumInfoDto infoDto = forumService.getForumById(forum.getId(), session.getCookie());
        assertAll(
                () -> assertEquals(forum.getName(), infoDto.getName()),
                () -> assertEquals(forum.getType(), infoDto.getType()),
                () -> assertEquals(forum.getId(), infoDto.getId()),
                () -> assertEquals(forum.getCreator().getName(), infoDto.getCreator()),
                () -> assertEquals(forum.isReadOnly(), infoDto.isReadonly()),
                () -> assertEquals(0, infoDto.getMessageCount()),
                () -> assertEquals(0, infoDto.getCommentCount())
        );
    }

    @Test
    void testDeleteForum() throws ServerException {
        User user = new User("dima", "pass");
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        forumService.deleteForum(0, session.getCookie());
        verify(forumDao, times(1)).delete(0);
    }

    @Test
    void testGetAllForums() throws ServerException {
        User user = new User("dima", "pass");
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);

        Forum forum1 = new Forum(1, "for1", user, ForumType.UNMODERATED, false);
        Forum forum2 = new Forum(2, "for2", user, ForumType.MODERATED, false);
        MessageHistory messageHistory1 = new MessageHistory("test");
        Message message = new Message(user, Collections.singletonList(messageHistory1));
        MessageTree messageTree = new MessageTree(forum1, "subj", message);
        MessageHistory messageHistory2 = new MessageHistory("comment1");
        Message comment1 = new Message(user, Collections.singletonList(messageHistory2));
        MessageHistory messageHistory3 = new MessageHistory("comment2");
        Message comment2 = new Message(user, Collections.singletonList(messageHistory3));
        message.setComments(Arrays.asList(comment1, comment2));
        forum1.setMessages(Collections.singletonList(messageTree));

        when(forumDao.getAll()).thenReturn(Arrays.asList(forum1, forum2));
        List<ForumInfoDto> infoDtoList = forumService.getAllForums(session.getCookie());
        assertAll(
                () -> assertEquals(forum1.getName(), infoDtoList.get(0).getName()),
                () -> assertEquals(forum2.getName(), infoDtoList.get(1).getName()),
                () -> assertEquals(1, infoDtoList.get(0).getMessageCount()),
                () -> assertEquals(2, infoDtoList.get(0).getCommentCount()),
                () -> assertEquals(0, infoDtoList.get(1).getCommentCount())
        );
    }
}
