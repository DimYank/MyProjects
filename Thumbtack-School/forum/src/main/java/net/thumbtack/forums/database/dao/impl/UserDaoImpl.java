package net.thumbtack.forums.database.dao.impl;

import net.thumbtack.forums.database.dao.iface.UserDao;
import net.thumbtack.forums.database.mappers.UserMapper;
import net.thumbtack.forums.error.ServerError;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.User;
import net.thumbtack.forums.model.enums.UserType;
import net.thumbtack.forums.model.view.UserInfo;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class UserDaoImpl implements UserDao {

    private final Logger LOGGER = LoggerFactory.getLogger(UserDaoImpl.class);

    private UserMapper userMapper;

    @Autowired
    public UserDaoImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User insert(User user) throws ServerException {
        LOGGER.debug("Inserting new user {}", user);
        try {
            userMapper.insert(user);
        } catch (DataAccessException e) {
            if (e.getClass() == DuplicateKeyException.class) {
                ServerError er = ServerError.USER_SAME_NAME;
                er.setMessage(String.format(er.getMessage(), user.getName()));
                throw new ServerException(ServerError.USER_SAME_NAME);
            }
            LOGGER.info("Can't insert user {}! Cause: {}", user, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
        return user;
    }

    @Override
    public User getById(long id) throws ServerException {
        LOGGER.debug("Selecting user by id={} ", id);
        try {
            return userMapper.getById(id);
        } catch (DataAccessException e) {
            LOGGER.info("Can't select user by id={}! Cause: {}", id, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public User getByName(String name) throws ServerException {
        LOGGER.debug("Selecting user by name={} ", name);
        try {
            return userMapper.getByName(name);
        } catch (DataAccessException e) {
            LOGGER.info("Can't select user by name={}! Cause: {}", name, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public void makeSuper(User user) throws ServerException {
        LOGGER.debug("Making superuser from {}", user);
        try {
            userMapper.makeSuper(user);
        } catch (DataAccessException e) {
            LOGGER.info("Can't make superuser from {}! Cause: {}", user, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public void changePass(User user) throws ServerException {
        LOGGER.debug("Changing password for user {}", user);
        try {
            userMapper.changePass(user);
        } catch (DataAccessException e) {
            LOGGER.info("Can't change password for user {}! Cause: {}", user, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public void delete(User user) throws ServerException {
        LOGGER.debug("Deleting user {}", user);
        try {
            userMapper.delete(user);
        } catch (DataAccessException e) {
            LOGGER.info("Can't delete user {}! Cause: {}", user, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public void ban(User user) throws ServerException {
        LOGGER.debug("Banning user {}", user);
        try {
            userMapper.ban(user);
        } catch (DataAccessException e) {
            LOGGER.info("Can't ban user {}! Cause: {}", user, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public void unbanUsers(LocalDateTime dateTime) throws ServerException {
        LOGGER.debug("Unbanning all users for {}!", dateTime);
        try {
            userMapper.unbanAll(dateTime);
        } catch (DataAccessException e) {
            LOGGER.info("Can't unban users! Cause: {}", ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public List<UserInfo> getInfo(UserType userType) throws ServerException {
        LOGGER.debug("Getting info about all users...");
        try {
            return userMapper.getInfo(userType);
        } catch (DataAccessException e) {
            LOGGER.info("Can't get info about all users! Cause: {}", ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }
}
