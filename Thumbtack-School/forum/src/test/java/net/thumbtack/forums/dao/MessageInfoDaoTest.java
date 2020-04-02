package net.thumbtack.forums.dao;

import net.thumbtack.forums.database.dao.iface.*;
import net.thumbtack.forums.database.dao.impl.*;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.*;
import net.thumbtack.forums.model.enums.ForumType;
import net.thumbtack.forums.model.enums.MessagePriority;
import net.thumbtack.forums.model.enums.OrderValue;
import net.thumbtack.forums.model.view.CommentInfo;
import net.thumbtack.forums.model.view.MessageInfo;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({CommonDaoImpl.class, UserDaoImpl.class, ForumDaoImpl.class, MessageDaoImpl.class, MessageInfoDaoImpl.class})
public class MessageInfoDaoTest extends DaoTestBase {

    private ForumDao forumDao;
    private MessageDao messageDao;
    private MessageInfoDao messageInfoDao;

    @Autowired
    public MessageInfoDaoTest(CommonDao commonDao, UserDao userDao, ForumDao forumDao, MessageDao messageDao, MessageInfoDao messageInfoDao) {
        super(commonDao, userDao);
        this.forumDao = forumDao;
        this.messageDao = messageDao;
        this.messageInfoDao = messageInfoDao;
    }

    @Test
    void testGetById() throws ServerException {
        User user = insertUser("dima");
        Forum forum = forumDao.insert(new Forum("forum", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree = messageDao.insert(tree);

        MessageInfo info = messageInfoDao.getById(insertedTree.getRootMessage().getId());
        assertAll(
                () -> assertEquals(insertedTree.getRootMessage().getId(), info.getId()),
                () -> assertEquals(insertedTree.getRootMessage().getCreated().getYear(), info.getCreated().getYear()),
                () -> assertEquals(insertedTree.getTags(), info.getTags())
        );
    }

    @Test
    void testGetCommentInfoDesc() throws ServerException {
        User user = insertUser("dima");
        Forum forum = forumDao.insert(new Forum("forum", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree = messageDao.insert(tree);
        Message comment = new Message(user, Collections.singletonList(new MessageHistory("comment1")));
        comment.setTree(insertedTree);
        comment.setParent(message);
        Message insertedComment1 = messageDao.insertComment(comment);

        comment = new Message(user, Collections.singletonList(new MessageHistory("comment2")));
        comment.setTree(insertedTree);
        LocalDateTime instTime = LocalDateTime.now();
        comment.setCreated(instTime.withMinute(instTime.getMinute() + 1));
        comment.setParent(message);
        Message insertedComment2 = messageDao.insertComment(comment);

        List<CommentInfo> info = messageInfoDao.getCommentInfo(insertedTree.getRootMessage().getId(), OrderValue.DESC);
        assertAll(
                () -> assertEquals(insertedComment1.getId(), info.get(1).getId()),
                () -> assertEquals(insertedComment2.getId(), info.get(0).getId())
        );
    }

    @Test
    void testGetCommentInfoAsc() throws ServerException {
        User user = insertUser("dima");
        Forum forum = forumDao.insert(new Forum("forum", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree = messageDao.insert(tree);
        Message comment = new Message(user, Collections.singletonList(new MessageHistory("comment1")));
        comment.setTree(insertedTree);
        comment.setParent(message);
        Message insertedComment1 = messageDao.insertComment(comment);

        comment = new Message(user, Collections.singletonList(new MessageHistory("comment2")));
        comment.setTree(insertedTree);
        LocalDateTime instTime = LocalDateTime.now();
        comment.setCreated(instTime.withMinute(instTime.getMinute() + 1));
        comment.setParent(message);
        Message insertedComment2 = messageDao.insertComment(comment);

        List<CommentInfo> info = messageInfoDao.getCommentInfo(insertedTree.getRootMessage().getId(), OrderValue.ASC);
        assertAll(
                () -> assertEquals(insertedComment1.getId(), info.get(0).getId()),
                () -> assertEquals(insertedComment2.getId(), info.get(1).getId())
        );
    }

    @Test
    void testGetCommentInfoWithComments() throws ServerException {
        User user = insertUser("dima");
        Forum forum = forumDao.insert(new Forum("forum", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree = messageDao.insert(tree);
        Message comment = new Message(user, Collections.singletonList(new MessageHistory("comment1")));
        comment.setTree(insertedTree);
        comment.setParent(insertedTree.getRootMessage());
        Message insertedComment1 = messageDao.insertComment(comment);

        comment = new Message(user, Collections.singletonList(new MessageHistory("comment2")));
        comment.setTree(insertedTree);
        LocalDateTime instTime = LocalDateTime.now();
        comment.setCreated(instTime.withMinute(instTime.getMinute() + 1));
        comment.setParent(insertedTree.getRootMessage());
        Message insertedComment2 = messageDao.insertComment(comment);

        comment = new Message(user, Collections.singletonList(new MessageHistory("subcomment1-1")));
        comment.setTree(insertedTree);
        comment.setParent(insertedComment1);
        Message insertedSubComment1 = messageDao.insertComment(comment);
        comment = new Message(user, Collections.singletonList(new MessageHistory("subcomment1-2")));
        comment.setTree(insertedTree);
        comment.setParent(insertedComment1);
        Message insertedSubComment2 = messageDao.insertComment(comment);
        comment = new Message(user, Collections.singletonList(new MessageHistory("subComment2-1")));
        comment.setTree(insertedTree);
        comment.setParent(insertedComment2);
        Message insertedSubComment3 = messageDao.insertComment(comment);

        List<CommentInfo> info = messageInfoDao.getCommentInfo(insertedTree.getRootMessage().getId(), OrderValue.ASC);
        assertAll(
                () -> assertEquals(insertedComment1.getId(), info.get(0).getId()),
                () -> assertEquals(insertedComment2.getId(), info.get(1).getId()),
                () -> assertEquals(insertedSubComment1.getId(), info.get(0).getComments().get(0).getId()),
                () -> assertEquals(insertedSubComment2.getId(), info.get(0).getComments().get(1).getId()),
                () -> assertEquals(insertedSubComment3.getId(), info.get(1).getComments().get(0).getId())
        );
    }

    @Test
    void testGetHistoryAllVersions() throws ServerException {
        User user = insertUser("dima");
        Forum forum = forumDao.insert(new Forum("forum", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        Message insertedMessage = messageDao.insert(tree).getRootMessage();
        insertedMessage.setHistory(Arrays.asList(insertedMessage.getHistory().get(0), new MessageHistory("new")));
        messageDao.editPublished(insertedMessage);

        List<MessageHistory> history = messageInfoDao.getHistory(insertedMessage.getId(), true, true);
        assertAll(
                () -> assertEquals(insertedMessage.getHistory().get(0).getBody(), history.get(1).getBody()),
                () -> assertEquals(insertedMessage.getHistory().get(1).getBody(), history.get(0).getBody())
        );
    }

    @Test
    void testGetHistoryNotAllVersions() throws ServerException {
        User user = insertUser("dima");
        Forum forum = forumDao.insert(new Forum("forum", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        Message insertedMessage = messageDao.insert(tree).getRootMessage();
        insertedMessage.setHistory(Arrays.asList(insertedMessage.getHistory().get(0), new MessageHistory("new")));
        messageDao.editPublished(insertedMessage);

        List<MessageHistory> history = messageInfoDao.getHistory(insertedMessage.getId(), false, true);
        assertAll(
                () -> assertEquals(1, history.size()),
                () -> assertEquals(insertedMessage.getHistory().get(1).getBody(), history.get(0).getBody())
        );
    }

    @Test
    void testGetHistoryPublished() throws ServerException {
        User user = insertUser("dima");
        Forum forum = forumDao.insert(new Forum("forum", user, ForumType.MODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        Message insertedMessage = messageDao.insert(tree).getRootMessage();
        messageDao.makePublic(insertedMessage);
        insertedMessage.setHistory(Arrays.asList(insertedMessage.getHistory().get(0), new MessageHistory("new")));
        messageDao.editPublished(insertedMessage);

        List<MessageHistory> history = messageInfoDao.getHistory(insertedMessage.getId(), true, false);
        assertAll(
                () -> assertEquals(1, history.size()),
                () -> assertEquals(insertedMessage.getHistory().get(0).getBody(), history.get(0).getBody())
        );
    }

    @Test
    void testGetAllByForumIdNoOffsetAndLimit() throws ServerException {
        User user = insertUser("dima");
        Forum forum = forumDao.insert(new Forum("forum", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text1")));
        MessageTree tree1 = new MessageTree(forum, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree1 = messageDao.insert(tree1);
        message = new Message(user, Collections.singletonList(new MessageHistory("text2")));
        MessageTree tree2 = new MessageTree(forum, "subj", MessagePriority.NORMAL, message, Collections.singletonList("tag2"));
        MessageTree insertedTree2 = messageDao.insert(tree2);
        message = new Message(user, Collections.singletonList(new MessageHistory("text3")));
        MessageTree tree3 = new MessageTree(forum, "subj", MessagePriority.LOW, message, Collections.singletonList("tag1"));
        MessageTree insertedTree3 = messageDao.insert(tree3);
        message = new Message(user, Collections.singletonList(new MessageHistory("text4")));
        MessageTree tree4 = new MessageTree(forum, "subj", MessagePriority.LOW, message, null);
        MessageTree insertedTree4 = messageDao.insert(tree4);

        List<MessageInfo> info = messageInfoDao.getAllByForumId(forum.getId(), Arrays.asList("tag1", "tag2"),
                OrderValue.DESC, 0, 1000);
        assertAll(
                () -> assertEquals(3, info.size()),
                () -> assertEquals(insertedTree1.getRootMessage().getId(), info.get(0).getId()),
                () -> assertEquals(insertedTree2.getRootMessage().getId(), info.get(1).getId()),
                () -> assertEquals(insertedTree3.getRootMessage().getId(), info.get(2).getId())
        );
    }

    @Test
    void testGetAllByForumIdWithOffsetAndLimit() throws ServerException {
        User user = insertUser("dima");
        Forum forum = forumDao.insert(new Forum("forum", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text1")));
        MessageTree tree1 = new MessageTree(forum, "subj1", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree1 = messageDao.insert(tree1);
        message = new Message(user, Collections.singletonList(new MessageHistory("text2")));
        MessageTree tree2 = new MessageTree(forum, "subj2", MessagePriority.NORMAL, message, Collections.singletonList("tag2"));
        MessageTree insertedTree2 = messageDao.insert(tree2);
        message = new Message(user, Collections.singletonList(new MessageHistory("text3")));
        MessageTree tree3 = new MessageTree(forum, "subj3", MessagePriority.LOW, message, Collections.singletonList("tag1"));
        MessageTree insertedTree3 = messageDao.insert(tree3);
        message = new Message(user, Collections.singletonList(new MessageHistory("text4")));
        LocalDateTime instTime = LocalDateTime.now();
        message.setCreated(instTime.withSecond(instTime.getSecond() + 1));
        MessageTree tree4 = new MessageTree(forum, "subj4", MessagePriority.LOW, message, Collections.singletonList("tag1"));
        MessageTree insertedTree4 = messageDao.insert(tree4);

        List<MessageInfo> info = messageInfoDao.getAllByForumId(forum.getId(), Arrays.asList("tag1", "tag2"),
                OrderValue.DESC, 1, 2);
        assertAll(
                () -> assertEquals(2, info.size()),
                () -> assertEquals(insertedTree2.getRootMessage().getId(), info.get(0).getId()),
                () -> assertEquals(insertedTree4.getRootMessage().getId(), info.get(1).getId())
        );
    }
}
