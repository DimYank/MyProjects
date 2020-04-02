package net.thumbtack.forums.dao;

import net.thumbtack.forums.database.dao.iface.CommonDao;
import net.thumbtack.forums.database.dao.iface.ForumDao;
import net.thumbtack.forums.database.dao.iface.MessageDao;
import net.thumbtack.forums.database.dao.iface.UserDao;
import net.thumbtack.forums.database.dao.impl.CommonDaoImpl;
import net.thumbtack.forums.database.dao.impl.ForumDaoImpl;
import net.thumbtack.forums.database.dao.impl.MessageDaoImpl;
import net.thumbtack.forums.database.dao.impl.UserDaoImpl;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.*;
import net.thumbtack.forums.model.enums.ForumType;
import net.thumbtack.forums.model.enums.MessagePriority;
import net.thumbtack.forums.model.enums.MessageState;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({CommonDaoImpl.class, UserDaoImpl.class, ForumDaoImpl.class, MessageDaoImpl.class})
public class MessageDaoTest extends DaoTestBase {

    private MessageDao messageDao;
    private ForumDao forumDao;

    @Autowired
    public MessageDaoTest(CommonDao commonDao, UserDao userDao, MessageDao messageDao, ForumDao forumDao) {
        super(commonDao, userDao);
        this.messageDao = messageDao;
        this.forumDao = forumDao;
    }

    @Test
    void testInsert() throws ServerException {
        User user = insertUser("dima");
        Forum forum = forumDao.insert(new Forum("forum", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum, "subj", message);

        MessageTree insertedTree = messageDao.insert(tree);
        assertAll(
                () -> assertNotEquals(0, insertedTree.getId()),
                () -> assertNotEquals(0, insertedTree.getRootMessage().getId()),
                () -> assertNotEquals(0, insertedTree.getRootMessage().getHistory().get(0).getId())
        );
    }

    @Test
    void testInsertComment() throws ServerException {
        User user = insertUser("dima");
        Forum forum = forumDao.insert(new Forum("forum", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum, "subj", message);
        MessageTree insertedTree = messageDao.insert(tree);
        Message comment = new Message(user, Collections.singletonList(new MessageHistory("comment")));
        comment.setTree(insertedTree);
        comment.setParent(message);

        Message insertedComment = messageDao.insertComment(comment);
        assertAll(
                () -> assertNotEquals(0, insertedComment.getId()),
                () -> assertNotEquals(0, insertedComment.getHistory().get(0).getId())
        );
    }

    @Test
    void testFailInsertCommentWrongMessage() throws ServerException {
        User user = insertUser("dima");
        Forum forum = forumDao.insert(new Forum("forum", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum, "subj", message);
        MessageTree insertedTree = messageDao.insert(tree);
        Message comment = new Message(user, Collections.singletonList(new MessageHistory("comment")));
        comment.setTree(insertedTree);
        message.setId(0);
        comment.setParent(message);

        assertThrows(ServerException.class, () -> messageDao.insertComment(comment));
    }

    @Test
    void testGetMessageTree() throws ServerException {
        User user = insertUser("dima");
        Forum forum = forumDao.insert(new Forum("forum", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree = messageDao.insert(tree);

        MessageTree treeFromDB = messageDao.getMessageTree(insertedTree.getRootMessage().getId());
        assertAll(
                () -> assertEquals(insertedTree.getId(), treeFromDB.getId()),
                () -> assertEquals(insertedTree.getTags(), treeFromDB.getTags()),
                () -> assertEquals(insertedTree.getSubject(), treeFromDB.getSubject()),
                () -> assertEquals(insertedTree.getPriority(), treeFromDB.getPriority())
        );
    }

    @Test
    void testGetMessage() throws ServerException {
        User user = insertUser("dima");
        Forum forum = forumDao.insert(new Forum("forum", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        Message insertedMessage = messageDao.insert(tree).getRootMessage();

        Message messageFromDB = messageDao.getMessage(insertedMessage.getId());
        assertAll(
                () -> assertEquals(insertedMessage.getId(), messageFromDB.getId()),
                () -> assertEquals(insertedMessage.getHistory(), messageFromDB.getHistory()),
                () -> assertEquals(insertedMessage.getCreated().getYear(), messageFromDB.getCreated().getYear())
        );
    }

    @Test
    void testEditPublished() throws ServerException {
        User user = insertUser("dima");
        Forum forum = forumDao.insert(new Forum("forum", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        Message insertedMessage = messageDao.insert(tree).getRootMessage();

        insertedMessage.setHistory(Arrays.asList(new MessageHistory("text"), new MessageHistory("new")));
        messageDao.editPublished(insertedMessage);
        Message messageFromDB = messageDao.getMessage(insertedMessage.getId());
        assertAll(
                () -> assertEquals("new", messageFromDB.getHistory().get(messageFromDB.getHistory().size() - 1).getBody()),
                () -> assertEquals(2, messageFromDB.getHistory().size())
        );
    }

    @Test
    void testEditUnpublished() throws ServerException {
        User user = insertUser("dima");
        Forum forum = forumDao.insert(new Forum("forum", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        Message insertedMessage = messageDao.insert(tree).getRootMessage();

        MessageHistory newHistory = new MessageHistory("new");
        newHistory.setId(insertedMessage.getHistory().get(insertedMessage.getHistory().size() - 1).getId());
        insertedMessage.setHistory(Arrays.asList(new MessageHistory("text"), newHistory));
        messageDao.editUnpublished(insertedMessage);
        Message messageFromDB = messageDao.getMessage(insertedMessage.getId());
        assertAll(
                () -> assertEquals("new", messageFromDB.getHistory().get(messageFromDB.getHistory().size() - 1).getBody()),
                () -> assertEquals(1, messageFromDB.getHistory().size())
        );

    }

    @Test
    void testDeleteMessage() throws ServerException {
        User user = insertUser("dima");
        Forum forum = forumDao.insert(new Forum("forum", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        Message insertedMessage = messageDao.insert(tree).getRootMessage();

        messageDao.deleteMessage(insertedMessage);
        Message messageFromDB = messageDao.getMessage(insertedMessage.getId());
        MessageTree treeFromDB = messageDao.getMessageTree(insertedMessage.getId());
        assertAll(
                () -> assertNull(messageFromDB),
                () -> assertNull(treeFromDB)
        );
    }

    @Test
    void testDeleteComment() throws ServerException {
        User user = insertUser("dima");
        Forum forum = forumDao.insert(new Forum("forum", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree = messageDao.insert(tree);
        Message comment = new Message(user, Collections.singletonList(new MessageHistory("comment")));
        comment.setTree(insertedTree);
        Message insertedComment = messageDao.insertComment(comment);
        comment.setParent(message);

        messageDao.deleteComment(insertedComment);
        Message commentFromDB = messageDao.getMessage(insertedComment.getId());
        assertNull(commentFromDB);
    }

    @Test
    void testPutRating() throws ServerException {
        User user = insertUser("dima");
        Forum forum = forumDao.insert(new Forum("forum", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        Message insertedMessage = messageDao.insert(tree).getRootMessage();

        Rating rating = new Rating(user, (short) 3);
        messageDao.putRating(rating, insertedMessage);
        Message messageFromDB = messageDao.getMessage(insertedMessage.getId());
        assertEquals(3, messageFromDB.getRating().get(0).getRating());
    }

    @Test
    void testFailPutRatingWrongMessage() throws ServerException {
        User user = insertUser("dima");
        Rating rating = new Rating(user, (short) 3);
        assertThrows(ServerException.class, () -> messageDao.putRating(rating, new Message()));
    }

    @Test
    void testDeleteRating() throws ServerException {
        User user = insertUser("dima");
        Forum forum = forumDao.insert(new Forum("forum", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        Message insertedMessage = messageDao.insert(tree).getRootMessage();
        Rating rating = new Rating(user, (short) 3);
        messageDao.putRating(rating, insertedMessage);

        messageDao.deleteRating(user, insertedMessage);
        Message messageFromDB = messageDao.getMessage(insertedMessage.getId());
        assertEquals(0, messageFromDB.getRating().size());
    }

    @Test
    void testUpdatePriority() throws ServerException {
        User user = insertUser("dima");
        Forum forum = forumDao.insert(new Forum("forum", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        Message insertedMessage = messageDao.insert(tree).getRootMessage();

        messageDao.updatePriority(MessagePriority.LOW, insertedMessage);
        Message messageFromDB = messageDao.getMessage(insertedMessage.getId());
        assertEquals(MessagePriority.LOW, messageFromDB.getTree().getPriority());
    }

    @Test
    void testCreateTreeFromComment() throws ServerException {
        User user = insertUser("dima");
        Forum forum = forumDao.insert(new Forum("forum", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree = messageDao.insert(tree);
        Message comment = new Message(user, Collections.singletonList(new MessageHistory("comment")));
        comment.setTree(insertedTree);
        comment.setParent(message);
        Message insertedComment = messageDao.insertComment(comment);

        MessageTree newTree = new MessageTree(forum, tree.getSubject(), insertedComment);
        messageDao.createTree(newTree);
        MessageTree treeFromDB = messageDao.getMessageTree(insertedComment.getId());
        assertNotEquals(insertedTree.getId(), treeFromDB.getId());
    }

    @Test
    void testUpdateToPublic() throws ServerException {
        User user = insertUser("dima");
        Forum forum = forumDao.insert(new Forum("forum", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        Message insertedMessage = messageDao.insert(tree).getRootMessage();

        messageDao.makePublic(insertedMessage);
        Message messageFromDB = messageDao.getMessage(insertedMessage.getId());
        assertEquals(MessageState.PUBLISHED, messageFromDB.getHistory().get(0).getState());
    }

    @Test
    void testDeleteUnpublished() throws ServerException {
        User user = insertUser("dima");
        Forum forum = forumDao.insert(new Forum("forum", user, ForumType.MODERATED));
        MessageHistory messageHistory = new MessageHistory("text");
        messageHistory.setState(MessageState.PUBLISHED);
        Message message = new Message(user, Collections.singletonList(messageHistory));
        MessageTree tree = new MessageTree(forum, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        Message insertedMessage = messageDao.insert(tree).getRootMessage();
        insertedMessage.setHistory(Arrays.asList(messageHistory, new MessageHistory("new")));
        messageDao.editPublished(insertedMessage);

        messageDao.deleteUnpublished(insertedMessage);
        Message messageFromDB = messageDao.getMessage(insertedMessage.getId());
        assertEquals("text", messageFromDB.getHistory().get(0).getBody());
    }
}