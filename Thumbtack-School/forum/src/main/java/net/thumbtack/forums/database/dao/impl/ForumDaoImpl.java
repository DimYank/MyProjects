package net.thumbtack.forums.database.dao.impl;


import net.thumbtack.forums.database.dao.iface.ForumDao;
import net.thumbtack.forums.database.mappers.ForumMapper;
import net.thumbtack.forums.error.ServerError;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.Forum;
import net.thumbtack.forums.model.User;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ForumDaoImpl implements ForumDao {

    private final Logger LOGGER = LoggerFactory.getLogger(ForumDaoImpl.class);

    private ForumMapper forumMapper;

    @Autowired
    public ForumDaoImpl(ForumMapper forumMapper) {
        this.forumMapper = forumMapper;
    }

    @Override
    public Forum insert(Forum forum) throws ServerException {
        LOGGER.debug("Inserting new forum {}", forum);
        try {
            forumMapper.insert(forum);
        } catch (DataAccessException e) {
            if (e.getClass() == DuplicateKeyException.class)
                throw new ServerException(ServerError.FORUM_SAME_NAME);
            LOGGER.info("Can't insert forum {}! Cause: {}", forum, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
        return forum;
    }

    @Override
    public List<Forum> getAll() throws ServerException {
        LOGGER.debug("Selecting all forums ");
        try {
            return forumMapper.getAll();
        } catch (DataAccessException e) {
            LOGGER.info("Can't select all forums! Cause: {}", ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public Forum getById(int id) throws ServerException {
        LOGGER.debug("Selecting forum by id={} ", id);
        try {
            return forumMapper.getById(id);
        } catch (DataAccessException e) {
            LOGGER.info("Can't select forum by id={}! Cause: {}", id, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public void makeReadOnly(User user) throws ServerException {
        LOGGER.debug("Updating forum to read only status by user id={} ", user.getId());
        try {
            forumMapper.updateToReadOnly(user);
        } catch (DataAccessException e) {
            LOGGER.info("Can't update forum to read only status by user id={}! Cause: {}", user.getId(), ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public void delete(int id) throws ServerException {
        LOGGER.debug("Deleting forum by id={} ", id);
        try {
            forumMapper.delete(id);
        } catch (DataAccessException e) {
            LOGGER.info("Can't delete forum by id={}! Cause: {}", id, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteAll() throws ServerException {
        LOGGER.debug("Deleting all forums ");
        try {
            forumMapper.deleteAll();
        } catch (DataAccessException e) {
            LOGGER.info("Can't delete all forums! Cause: {}", ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }
}
