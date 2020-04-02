package net.thumbtack.forums.dao;

import net.thumbtack.forums.database.dao.iface.CommonDao;
import net.thumbtack.forums.database.dao.iface.UserDao;
import net.thumbtack.forums.database.dao.impl.CommonDaoImpl;
import net.thumbtack.forums.database.dao.impl.UserDaoImpl;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.User;
import net.thumbtack.forums.model.enums.BanStatus;
import net.thumbtack.forums.model.enums.UserType;
import net.thumbtack.forums.model.view.UserInfo;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({CommonDaoImpl.class, UserDaoImpl.class})
public class UserDaoTest extends DaoTestBase {

    @Autowired
    public UserDaoTest(CommonDao commonDao, UserDao userDao) {
        super(commonDao, userDao);
    }

    @Test
    void testInsert() throws ServerException {
        User user = insertUser("Dima");
        assertNotEquals(0, user.getId());
    }

    @Test
    void testFailInsertSameName() throws ServerException {
        insertUser("dima");
        assertThrows(ServerException.class, () -> insertUser("dima"));
    }

    @Test
    void testGetById() throws ServerException {
        User insertedUser = insertUser("dima");
        User userFromDB = userDao.getById(insertedUser.getId());
        assertEquals(insertedUser.getId(), userFromDB.getId());
    }

    @Test
    void testGetByName() throws ServerException {
        User insertedUser = insertUser("dima");
        User userFromDB = userDao.getByName(insertedUser.getName());
        assertEquals(insertedUser.getId(), userFromDB.getId());
    }

    @Test
    void testBanUser() throws ServerException {
        User insertedUser = insertUser("dima");
        insertedUser.setTimeBanExit(LocalDateTime.of(2019, 1, 1, 0, 0));
        userDao.ban(insertedUser);
        User userFromDB = userDao.getByName(insertedUser.getName());
        assertEquals(BanStatus.LIMITED, userFromDB.getStatus());
    }

    @Test
    void testMakeSuper() throws ServerException {
        User insertedUser = insertUser("dima");
        userDao.makeSuper(insertedUser);
        User userFromDB = userDao.getById(insertedUser.getId());
        assertEquals(UserType.SUPER, userFromDB.getUserType());
    }

    @Test
    void testMakeSuperFromBannedUser() throws ServerException {
        User insertedUser = insertUser("dima");
        insertedUser.setTimeBanExit( LocalDateTime.of(2019, 1, 1, 0, 0));
        userDao.ban(insertedUser);
        userDao.makeSuper(insertedUser);
        User userFromDB = userDao.getByName(insertedUser.getName());
        assertAll(
                () -> assertEquals(BanStatus.FULL, userFromDB.getStatus()),
                () -> assertEquals(UserType.SUPER, userFromDB.getUserType())
        );
    }

    @Test
    void testChangePass() throws ServerException {
        String newPass = "new_password";
        User insertedUser = insertUser("dima");
        insertedUser.setPassword(newPass);
        userDao.changePass(insertedUser);
        User userFromDB = userDao.getById(insertedUser.getId());
        assertEquals(newPass, userFromDB.getPassword());
    }

    @Test
    void testGetInfoRegular() throws ServerException {
        User insertedUser1 = insertUser("dima");
        User insertedUser2 = insertUser("vova");
        User insertedUser3 = insertUser("kolya");
        List<UserInfo> info = userDao.getInfo(UserType.REGULAR);
        assertAll(
                () -> assertEquals("Admin", info.get(0).getName()),
                () -> assertEquals(insertedUser1.getName(), info.get(1).getName()),
                () -> assertEquals(insertedUser2.getName(), info.get(2).getName()),
                () -> assertEquals(insertedUser3.getName(), info.get(3).getName()),
                () -> assertNull(info.get(0).getEmail()),
                () -> assertNull(info.get(1).getEmail()),
                () -> assertNull(info.get(2).getEmail()),
                () -> assertNull(info.get(0).getUserType()),
                () -> assertNull(info.get(0).getUserType()),
                () -> assertNull(info.get(0).getUserType())
        );
    }

    @Test
    void testGetInfoSuper() throws ServerException {
        User insertedUser1 = insertUser("dima");
        User insertedUser2 = insertUser("vova");
        User insertedUser3 = insertUser("kolya");
        List<UserInfo> info = userDao.getInfo(UserType.SUPER);
        assertAll(
                () -> assertEquals("Admin", info.get(0).getName()),
                () -> assertEquals(insertedUser1.getName(), info.get(1).getName()),
                () -> assertEquals(insertedUser2.getName(), info.get(2).getName()),
                () -> assertEquals(insertedUser3.getName(), info.get(3).getName()),
                () -> assertNotNull(info.get(0).getEmail()),
                () -> assertNotNull(info.get(1).getEmail()),
                () -> assertNotNull(info.get(2).getEmail()),
                () -> assertNotNull(info.get(0).getUserType()),
                () -> assertNotNull(info.get(0).getUserType()),
                () -> assertNotNull(info.get(0).getUserType())
        );
    }
}
