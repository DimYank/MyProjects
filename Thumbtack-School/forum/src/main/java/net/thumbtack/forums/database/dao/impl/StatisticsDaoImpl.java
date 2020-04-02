package net.thumbtack.forums.database.dao.impl;

import net.thumbtack.forums.database.dao.iface.StatisticsDao;
import net.thumbtack.forums.database.mappers.StatisticsMapper;
import net.thumbtack.forums.error.ServerError;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.view.MessageRating;
import net.thumbtack.forums.model.view.UserRating;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StatisticsDaoImpl implements StatisticsDao {
    private final Logger LOGGER = LoggerFactory.getLogger(StatisticsDaoImpl.class);

    private StatisticsMapper statisticsMapper;

    @Autowired
    public StatisticsDaoImpl(StatisticsMapper statisticsMapper) {
        this.statisticsMapper = statisticsMapper;
    }

    @Override
    public Long getMessagesCount(int forumId) throws ServerException {
        LOGGER.debug("Selecting messages count by forum id={} ", forumId);
        try {
            return statisticsMapper.getMessageCount(forumId);
        } catch (DataAccessException e) {
            LOGGER.info("Can't select messages count by forum id={}! Cause: {}", forumId, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public Long getCommentsCount(int forumId) throws ServerException {
        LOGGER.debug("Selecting comments count by forum id={} ", forumId);
        try {
            return statisticsMapper.getCommentCount(forumId);
        } catch (DataAccessException e) {
            LOGGER.info("Can't select comments count by forum id={}! Cause: {}", forumId, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public List<MessageRating> getRating(int forumId, boolean forMessages, long offset, int limit) throws ServerException {
        LOGGER.debug("Selecting messages rating by forum id={} with params: forMessages[{}], offset[{}], limit[{}] ",
                forumId, forMessages, offset, limit);
        try {
            return statisticsMapper.getRating(forumId, forMessages, offset, limit);
        } catch (DataAccessException e) {
            LOGGER.info("Can't select messages rating by forum id={}! Cause: {}", forumId, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public List<UserRating> getUsersRating(int forumId, long offset, int limit) throws ServerException {
        LOGGER.debug("Selecting users rating by forum id={} with params: offset[{}], limit[{}] ",
                forumId, offset, limit);
        try {
            return statisticsMapper.getUsersRating(forumId, offset, limit);
        } catch (DataAccessException e) {
            LOGGER.info("Can't select users rating by forum id={}! Cause: {}", forumId, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }
}
