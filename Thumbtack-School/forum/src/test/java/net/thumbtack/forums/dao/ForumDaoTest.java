package net.thumbtack.forums.dao;

import net.thumbtack.forums.database.dao.iface.CommonDao;
import net.thumbtack.forums.database.dao.iface.ForumDao;
import net.thumbtack.forums.database.dao.iface.UserDao;
import net.thumbtack.forums.database.dao.impl.CommonDaoImpl;
import net.thumbtack.forums.database.dao.impl.ForumDaoImpl;
import net.thumbtack.forums.database.dao.impl.UserDaoImpl;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.Forum;
import net.thumbtack.forums.model.User;
import net.thumbtack.forums.model.enums.ForumType;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({CommonDaoImpl.class, UserDaoImpl.class, ForumDaoImpl.class})
public class ForumDaoTest extends DaoTestBase {

    private ForumDao forumDao;

    @Autowired
    public ForumDaoTest(CommonDao commonDao, UserDao userDao, ForumDao forumDao) {
        super(commonDao, userDao);
        this.forumDao = forumDao;
    }

    @Test
    void testInsert() throws ServerException {
        User user = insertUser("dima");
        Forum forum = forumDao.insert(new Forum("test", user, ForumType.UNMODERATED));

        assertNotEquals(0, forum.getId());
    }

    @Test
    void testFailInsertSameName() throws ServerException {
        User user = insertUser("dima");
        Forum forum = new Forum("test", user, ForumType.UNMODERATED);
        forumDao.insert(forum);

        assertThrows(ServerException.class, () -> forumDao.insert(forum));
    }

    @Test
    void testGetById() throws ServerException {
        User user = insertUser("dima");
        Forum insertedForum = forumDao.insert(new Forum("test", user, ForumType.UNMODERATED));
        Forum forumFromDB = forumDao.getById(insertedForum.getId());

        assertEquals(insertedForum.getId(), forumFromDB.getId());
    }

    @Test
    void testGetAll() throws ServerException {
        User user = insertUser("dima");
        Forum insertedForum1 = forumDao.insert(new Forum("test1", user, ForumType.UNMODERATED));
        Forum insertedForum2 = forumDao.insert(new Forum("test2", user, ForumType.UNMODERATED));
        Forum insertedForum3 = forumDao.insert(new Forum("test3", user, ForumType.UNMODERATED));

        List<Forum> forums = forumDao.getAll();
        assertAll(
                () -> assertEquals(insertedForum1.getName(), forums.get(0).getName()),
                () -> assertEquals(insertedForum2.getName(), forums.get(1).getName()),
                () -> assertEquals(insertedForum3.getName(), forums.get(2).getName())
        );
    }

    @Test
    void testUpdateToReadOnly() throws ServerException {
        User user = insertUser("dima");
        Forum insertedForum = forumDao.insert(new Forum("test", user, ForumType.UNMODERATED));
        forumDao.makeReadOnly(user);
        Forum forumFromDB = forumDao.getById(insertedForum.getId());

        assertEquals(insertedForum.getId(), forumFromDB.getId());
    }

    @Test
    void testDelete() throws ServerException {
        User user = insertUser("dima");
        Forum insertedForum = forumDao.insert(new Forum("test", user, ForumType.UNMODERATED));
        forumDao.delete(insertedForum.getId());

        assertNull(forumDao.getById(insertedForum.getId()));
    }

    @Test
    void testDeleteAll() throws ServerException {
        User user = insertUser("dima");
        forumDao.insert(new Forum("test", user, ForumType.UNMODERATED));
        forumDao.deleteAll();

        List<Forum> forums = forumDao.getAll();
        assertEquals(0, forums.size());
    }
}
