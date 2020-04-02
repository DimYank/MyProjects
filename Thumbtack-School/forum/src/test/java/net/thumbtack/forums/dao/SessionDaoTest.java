package net.thumbtack.forums.dao;

import net.thumbtack.forums.database.dao.iface.CommonDao;
import net.thumbtack.forums.database.dao.iface.SessionDao;
import net.thumbtack.forums.database.dao.iface.UserDao;
import net.thumbtack.forums.database.dao.impl.CommonDaoImpl;
import net.thumbtack.forums.database.dao.impl.SessionDaoImpl;
import net.thumbtack.forums.database.dao.impl.UserDaoImpl;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.Session;
import net.thumbtack.forums.model.User;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({CommonDaoImpl.class, UserDaoImpl.class, SessionDaoImpl.class})
public class SessionDaoTest extends DaoTestBase {

    private SessionDao sessionDao;

    @Autowired
    public SessionDaoTest(CommonDao commonDao, UserDao userDao, SessionDao sessionDao) {
        super(commonDao, userDao);
        this.sessionDao = sessionDao;
    }

    private String getUUID() {
        return UUID.randomUUID().toString();
    }

    @Test
    void testInsert() throws ServerException {
        User insertedUser = insertUser("dima");
        String cookie = getUUID();
        sessionDao.insertSession(new Session(cookie, insertedUser));
    }

    @Test
    void testGetSession() throws ServerException {
        User insertedUser = insertUser("dima");
        String cookie = getUUID();
        sessionDao.insertSession(new Session(cookie, insertedUser));
        Session session = sessionDao.getByCookie(cookie);

        assertEquals(insertedUser.getName(), session.getUser().getName());
    }

    @Test
    void testInsertSame() throws ServerException {
        User insertedUser = insertUser("dima");
        String cookie = getUUID();
        sessionDao.insertSession(new Session(cookie, insertedUser));
        sessionDao.insertSession(new Session(cookie, insertedUser));
        //Throws exception if mapper gets more than 1 row
        sessionDao.getByCookie(cookie);
    }

    @Test
    void testDeleteSession() throws ServerException {
        User insertedUser = insertUser("dima");
        String cookie = getUUID();
        sessionDao.insertSession(new Session(cookie, insertedUser));
        sessionDao.deleteSession(cookie);

        assertNull(sessionDao.getByCookie(cookie));
    }
}
