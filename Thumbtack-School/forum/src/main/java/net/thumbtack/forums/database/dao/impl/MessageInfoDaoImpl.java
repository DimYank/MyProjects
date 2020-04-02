package net.thumbtack.forums.database.dao.impl;

import net.thumbtack.forums.database.dao.iface.MessageInfoDao;
import net.thumbtack.forums.database.mappers.MessageInfoMapper;
import net.thumbtack.forums.error.ServerError;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.MessageHistory;
import net.thumbtack.forums.model.enums.OrderValue;
import net.thumbtack.forums.model.view.CommentInfo;
import net.thumbtack.forums.model.view.MessageInfo;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageInfoDaoImpl implements MessageInfoDao {

    private final Logger LOGGER = LoggerFactory.getLogger(MessageInfoDaoImpl.class);

    private MessageInfoMapper messageInfoMapper;

    @Autowired
    public MessageInfoDaoImpl(MessageInfoMapper messageInfoMapper) {
        this.messageInfoMapper = messageInfoMapper;
    }

    @Override
    public MessageInfo getById(long id) throws ServerException {
        LOGGER.debug("Getting message info with id={}", id);
        try {
            return messageInfoMapper.getById(id);
        } catch (DataAccessException e) {
            LOGGER.info("Can't get message info with id={}! Cause: {}", id, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public List<CommentInfo> getCommentInfo(long parentId, OrderValue order) throws ServerException {
        LOGGER.debug("Getting comment info with id={}", parentId);
        try {
            if (order == OrderValue.ASC) {
                return messageInfoMapper.getCommentInfoAsc(parentId);
            } else {
                return messageInfoMapper.getCommentInfoDesc(parentId);
            }
        } catch (DataAccessException e) {
            LOGGER.info("Can't get comment info with id={}! Cause: {}", parentId, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public List<MessageHistory> getHistory(long id, boolean allVersions, boolean unpublished) throws ServerException {
        LOGGER.debug("Getting message history with params id={}, allVersions={}, unpublished={}", id, allVersions, unpublished);
        try {
            return messageInfoMapper.getHistory(id, allVersions, unpublished);
        } catch (DataAccessException e) {
            LOGGER.info("Can't get message history with params id={}, allVersions={}, unpublished={}! Cause: {}", id,
                    allVersions, unpublished, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public List<MessageInfo> getAllByForumId(int forumId, List<String> tags, OrderValue order, long offset, int limit) throws ServerException {
        LOGGER.debug("Getting all messages for frumId={}", forumId);
        try {
            return messageInfoMapper.getAllByForumId(forumId, tags, order, offset, limit);
        } catch (DataAccessException e) {
            LOGGER.info("Can't get all messages for forumId={}! Cause: {}", forumId, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }
}
