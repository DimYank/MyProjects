package net.thumbtack.forums.dao;

import net.thumbtack.forums.database.dao.iface.CommonDao;
import net.thumbtack.forums.database.dao.iface.UserDao;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.User;
import org.junit.jupiter.api.BeforeEach;

public abstract class DaoTestBase {

    protected UserDao userDao;
    private CommonDao commonDao;

    DaoTestBase(CommonDao commonDao, UserDao userDao) {
        this.commonDao = commonDao;
        this.userDao = userDao;
    }

    protected User insertUser(String name) throws ServerException {
        return userDao.insert(new User(name, "em@em.ru", "pass123456"));
    }

    @BeforeEach
    public void clearDB() throws ServerException {
        commonDao.clearDB();
    }
}
