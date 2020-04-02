package net.thumbtack.forums.dao;

import net.thumbtack.forums.database.dao.iface.*;
import net.thumbtack.forums.database.dao.impl.*;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.*;
import net.thumbtack.forums.model.enums.ForumType;
import net.thumbtack.forums.model.enums.MessagePriority;
import net.thumbtack.forums.model.view.MessageRating;
import net.thumbtack.forums.model.view.UserRating;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({CommonDaoImpl.class, UserDaoImpl.class, ForumDaoImpl.class, MessageDaoImpl.class, StatisticsDaoImpl.class})
public class StatisticsDaoTest extends DaoTestBase {

    private ForumDao forumDao;
    private MessageDao messageDao;
    private StatisticsDao statisticsDao;

    @Autowired
    public StatisticsDaoTest(CommonDao commonDao, UserDao userDao, ForumDao forumDao, MessageDao messageDao, StatisticsDao statisticsDao) {
        super(commonDao, userDao);
        this.forumDao = forumDao;
        this.messageDao = messageDao;
        this.statisticsDao = statisticsDao;
    }

    @Test
    void testGetMessagesCount() throws ServerException {
        User user = insertUser("dima");
        Forum forum1 = forumDao.insert(new Forum("forum1", user, ForumType.UNMODERATED));
        Forum forum2 = forumDao.insert(new Forum("forum2", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum1, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        messageDao.insert(tree);
        message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        tree = new MessageTree(forum2, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        messageDao.insert(tree);

        Long count = statisticsDao.getMessagesCount(0);
        assertEquals(2, count);
    }

    @Test
    void testGetMessagesCountInForum() throws ServerException {
        User user = insertUser("dima");
        Forum forum1 = forumDao.insert(new Forum("forum1", user, ForumType.UNMODERATED));
        Forum forum2 = forumDao.insert(new Forum("forum2", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum1, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        messageDao.insert(tree);
        message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        tree = new MessageTree(forum2, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        messageDao.insert(tree);

        Long count = statisticsDao.getMessagesCount(forum2.getId());
        assertEquals(1, count);
    }

    @Test
    void testGetCommentsCount() throws ServerException {
        User user = insertUser("dima");
        Forum forum1 = forumDao.insert(new Forum("forum1", user, ForumType.UNMODERATED));
        Forum forum2 = forumDao.insert(new Forum("forum2", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum1, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree1 = messageDao.insert(tree);
        Message comment = new Message(user, Collections.singletonList(new MessageHistory("comment1")));
        comment.setTree(insertedTree1);
        comment.setParent(message);
        messageDao.insertComment(comment);
        message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        tree = new MessageTree(forum2, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree2 = messageDao.insert(tree);
        comment = new Message(user, Collections.singletonList(new MessageHistory("comment1")));
        comment.setTree(insertedTree2);
        comment.setParent(message);
        messageDao.insertComment(comment);
        comment = new Message(user, Collections.singletonList(new MessageHistory("comment1")));
        comment.setTree(insertedTree2);
        comment.setParent(message);
        messageDao.insertComment(comment);

        Long count = statisticsDao.getCommentsCount(0);
        assertEquals(3, count);
    }

    @Test
    void testGetCommentsCountInForum() throws ServerException {
        User user = insertUser("dima");
        Forum forum1 = forumDao.insert(new Forum("forum1", user, ForumType.UNMODERATED));
        Forum forum2 = forumDao.insert(new Forum("forum2", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum1, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree1 = messageDao.insert(tree);
        Message comment = new Message(user, Collections.singletonList(new MessageHistory("comment1")));
        comment.setTree(insertedTree1);
        comment.setParent(message);
        messageDao.insertComment(comment);
        message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        tree = new MessageTree(forum2, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree2 = messageDao.insert(tree);
        comment = new Message(user, Collections.singletonList(new MessageHistory("comment1")));
        comment.setTree(insertedTree2);
        comment.setParent(message);
        messageDao.insertComment(comment);

        Long count = statisticsDao.getCommentsCount(forum2.getId());
        assertEquals(1, count);
    }

    @Test
    void testGetRating() throws ServerException {
        User user = insertUser("dima");
        Forum forum1 = forumDao.insert(new Forum("forum1", user, ForumType.UNMODERATED));
        Forum forum2 = forumDao.insert(new Forum("forum2", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum1, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree1 = messageDao.insert(tree);
        message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        tree = new MessageTree(forum2, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree2 = messageDao.insert(tree);

        messageDao.putRating(new Rating(user, (short) 3), insertedTree1.getRootMessage());
        messageDao.putRating(new Rating(user, (short) 4), insertedTree2.getRootMessage());

        List<MessageRating> rating = statisticsDao.getRating(0, true, 0, 100);
        assertAll(
                () -> assertEquals(3, rating.get(1).getRating()),
                () -> assertEquals(4, rating.get(0).getRating())
        );
    }

    @Test
    void testGetRatingInForum() throws ServerException {
        User user = insertUser("dima");
        Forum forum1 = forumDao.insert(new Forum("forum1", user, ForumType.UNMODERATED));
        Forum forum2 = forumDao.insert(new Forum("forum2", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum1, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree1 = messageDao.insert(tree);
        message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        tree = new MessageTree(forum2, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree2 = messageDao.insert(tree);

        messageDao.putRating(new Rating(user, (short) 3), insertedTree1.getRootMessage());
        messageDao.putRating(new Rating(user, (short) 4), insertedTree2.getRootMessage());

        List<MessageRating> rating = statisticsDao.getRating(forum1.getId(), true, 0, 100);
        assertAll(
                () -> assertEquals(1, rating.size()),
                () -> assertEquals(3, rating.get(0).getRating())
        );
    }

    @Test
    void testGetRatingForComments() throws ServerException {
        User user = insertUser("dima");
        Forum forum1 = forumDao.insert(new Forum("forum1", user, ForumType.UNMODERATED));
        Forum forum2 = forumDao.insert(new Forum("forum2", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum1, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree1 = messageDao.insert(tree);
        Message comment = new Message(user, Collections.singletonList(new MessageHistory("comment1")));
        comment.setTree(insertedTree1);
        comment.setParent(message);
        Message insertedComment1 = messageDao.insertComment(comment);
        message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        tree = new MessageTree(forum2, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree2 = messageDao.insert(tree);
        comment = new Message(user, Collections.singletonList(new MessageHistory("comment1")));
        comment.setTree(insertedTree2);
        comment.setParent(message);
        Message insertedComment2 = messageDao.insertComment(comment);

        messageDao.putRating(new Rating(user, (short) 3), insertedComment1);
        messageDao.putRating(new Rating(user, (short) 4), insertedComment2);

        List<MessageRating> rating = statisticsDao.getRating(0, false, 0, 100);
        assertAll(
                () -> assertEquals(3, rating.get(1).getRating()),
                () -> assertEquals(4, rating.get(0).getRating())
        );
    }

    @Test
    void testGetRatingWithOffsetAndLimit() throws ServerException {
        User user = insertUser("dima");
        Forum forum1 = forumDao.insert(new Forum("forum1", user, ForumType.UNMODERATED));
        Forum forum2 = forumDao.insert(new Forum("forum2", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum1, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree1 = messageDao.insert(tree);
        message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        tree = new MessageTree(forum2, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree2 = messageDao.insert(tree);
        message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        tree = new MessageTree(forum2, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree3 = messageDao.insert(tree);
        message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        tree = new MessageTree(forum2, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree4 = messageDao.insert(tree);

        messageDao.putRating(new Rating(user, (short) 5), insertedTree1.getRootMessage());
        messageDao.putRating(new Rating(user, (short) 4), insertedTree2.getRootMessage());
        messageDao.putRating(new Rating(user, (short) 3), insertedTree3.getRootMessage());
        messageDao.putRating(new Rating(user, (short) 1), insertedTree4.getRootMessage());

        List<MessageRating> rating = statisticsDao.getRating(0, true,
                insertedTree2.getRootMessage().getId() - 1, 2);

        assertAll(
                () -> assertEquals(2, rating.size()),
                () -> assertEquals(4, rating.get(0).getRating()),
                () -> assertEquals(3, rating.get(1).getRating())
        );
    }

    @Test
    void testGetRatingForCommentsWithOffsetAndLimit() throws ServerException {
        User user = insertUser("dima");
        Forum forum1 = forumDao.insert(new Forum("forum1", user, ForumType.UNMODERATED));
        Forum forum2 = forumDao.insert(new Forum("forum2", user, ForumType.UNMODERATED));
        Message message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum1, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree1 = messageDao.insert(tree);
        Message comment = new Message(user, Collections.singletonList(new MessageHistory("comment1")));
        comment.setTree(insertedTree1);
        comment.setParent(message);
        Message insertedComment1 = messageDao.insertComment(comment);
        message = new Message(user, Collections.singletonList(new MessageHistory("text")));
        tree = new MessageTree(forum2, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree2 = messageDao.insert(tree);
        comment = new Message(user, Collections.singletonList(new MessageHistory("comment1")));
        comment.setTree(insertedTree2);
        comment.setParent(message);
        Message insertedComment2 = messageDao.insertComment(comment);
        comment = new Message(user, Collections.singletonList(new MessageHistory("comment1")));
        comment.setTree(insertedTree2);
        comment.setParent(message);
        Message insertedComment3 = messageDao.insertComment(comment);

        messageDao.putRating(new Rating(user, (short) 3), insertedComment1);
        messageDao.putRating(new Rating(user, (short) 4), insertedComment2);
        messageDao.putRating(new Rating(user, (short) 5), insertedComment3);

        List<MessageRating> rating = statisticsDao.getRating(0, false, insertedComment1.getId() - 1, 2);

        assertAll(
                () -> assertEquals(2, rating.size()),
                () -> assertEquals(5, rating.get(0).getRating()),
                () -> assertEquals(4, rating.get(1).getRating())
        );
    }

    @Test
    void testGetUsersRating() throws ServerException {
        User user1 = insertUser("dima");
        User user2 = insertUser("vova");
        User user3 = insertUser("vasya");
        Forum forum1 = forumDao.insert(new Forum("forum1", user1, ForumType.UNMODERATED));
        Forum forum2 = forumDao.insert(new Forum("forum2", user1, ForumType.UNMODERATED));
        Message message = new Message(user1, Collections.singletonList(new MessageHistory("text")));
        MessageTree tree = new MessageTree(forum1, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree1 = messageDao.insert(tree);
        message = new Message(user2, Collections.singletonList(new MessageHistory("text")));
        tree = new MessageTree(forum2, "subj", MessagePriority.HIGH, message, Arrays.asList("tag1", "tag2"));
        MessageTree insertedTree2 = messageDao.insert(tree);

        messageDao.putRating(new Rating(user1, (short) 3), insertedTree1.getRootMessage());
        messageDao.putRating(new Rating(user2, (short) 4), insertedTree2.getRootMessage());
        messageDao.putRating(new Rating(user3, (short) 2), insertedTree1.getRootMessage());

        List<UserRating> rating = statisticsDao.getUsersRating(0, user1.getId() - 1, 2);
        assertAll(
                () -> assertEquals(2, rating.size()),
                () -> assertEquals(4, rating.get(0).getRating()),
                () -> assertEquals(2.5, rating.get(1).getRating()),
                () -> assertEquals("vova", rating.get(0).getName()),
                () -> assertEquals("dima", rating.get(1).getName())
        );
    }
}
