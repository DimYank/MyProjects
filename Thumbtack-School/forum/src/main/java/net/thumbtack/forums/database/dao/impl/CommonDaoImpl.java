package net.thumbtack.forums.database.dao.impl;

import net.thumbtack.forums.database.dao.iface.CommonDao;
import net.thumbtack.forums.database.mappers.ForumMapper;
import net.thumbtack.forums.database.mappers.MessageMapper;
import net.thumbtack.forums.database.mappers.SessionMapper;
import net.thumbtack.forums.database.mappers.UserMapper;
import net.thumbtack.forums.error.ServerError;
import net.thumbtack.forums.error.ServerException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Component
public class CommonDaoImpl implements CommonDao {

    private final Logger LOGGER = LoggerFactory.getLogger(CommonDaoImpl.class);

    private UserMapper userMapper;
    private ForumMapper forumMapper;
    private MessageMapper messageMapper;
    private SessionMapper sessionMapper;

    @Autowired
    public CommonDaoImpl(UserMapper userMapper, ForumMapper forumMapper, MessageMapper messageMapper,
                         SessionMapper sessionMapper) {
        this.userMapper = userMapper;
        this.forumMapper = forumMapper;
        this.messageMapper = messageMapper;
        this.sessionMapper = sessionMapper;
    }

    @Override
    public void clearDB() throws ServerException {
        LOGGER.debug("CLEARING WHOLE DB!");
        try {
            forumMapper.deleteAll();
            userMapper.deleteAll();
            messageMapper.deleteAll();
            sessionMapper.deleteAll();
        } catch (DataAccessException e) {
            LOGGER.info("Can't clear DB! Cause: {}", ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }
}
