package net.thumbtack.forums.database.dao.impl;

import net.thumbtack.forums.database.dao.iface.MessageDao;
import net.thumbtack.forums.database.mappers.MessageMapper;
import net.thumbtack.forums.error.ServerError;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.Message;
import net.thumbtack.forums.model.MessageTree;
import net.thumbtack.forums.model.Rating;
import net.thumbtack.forums.model.User;
import net.thumbtack.forums.model.enums.MessagePriority;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class MessageDaoImpl implements MessageDao {
    private final Logger LOGGER = LoggerFactory.getLogger(MessageDaoImpl.class);

    private MessageMapper messageMapper;

    public MessageDaoImpl(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    @Override
    public MessageTree insert(MessageTree messageTree) throws ServerException {
        LOGGER.debug("Inserting new message {}", messageTree);
        try {
            messageMapper.insertMessageTree(messageTree);
            messageMapper.insertMessage(messageTree.getRootMessage());
            messageMapper.insertHistory(messageTree.getRootMessage().getHistory().get(0));
            if (messageTree.getTags() != null) {
                messageMapper.insertTags(messageTree);
            }
        } catch (DataAccessException e) {
            LOGGER.info("Can't insert message {}! Cause: {}", messageTree, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
        return messageTree;
    }

    @Override
    public Message insertComment(Message message) throws ServerException {
        LOGGER.debug("Inserting new comment {}", message);
        try {
            messageMapper.insertComment(message);
            messageMapper.insertHistory(message.getHistory().get(0));
        } catch (DataAccessException e) {
            if (e.getClass() == DataIntegrityViolationException.class) {
                throw new ServerException(ServerError.MESSAGE_WRONG_ID);
            }
            LOGGER.info("Can't insert comment {}! Cause: {}", message, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
        return message;
    }

    @Override
    public MessageTree getMessageTree(long messageId) throws ServerException {
        LOGGER.debug("Selecting tree by messageId={} ", messageId);
        try {
            return messageMapper.getMessageTree(messageId);
        } catch (DataAccessException e) {
            LOGGER.info("Can't select message tree by messageId={}! Cause: {}", messageId, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public Message getMessage(long id) throws ServerException {
        LOGGER.debug("Selecting message by id={} ", id);
        try {
            return messageMapper.getMessage(id);
        } catch (DataAccessException e) {
            LOGGER.info("Can't select message by id={}! Cause: {}", id, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public void editPublished(Message message) throws ServerException {
        LOGGER.debug("Editing message {}", message);
        try {
            messageMapper.insertHistory(message.getHistory().get(message.getHistory().size() - 1));
        } catch (DataAccessException e) {
            LOGGER.info("Can't insert comment {}! Cause: {}", message, ExceptionUtils.getStackTrace(e));

            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public void editUnpublished(Message message) throws ServerException {
        LOGGER.debug("Editing message {}", message);
        try {
            messageMapper.updateHistory(message.getHistory().get(message.getHistory().size() - 1));
        } catch (DataAccessException e) {
            LOGGER.info("Can't swap message history for {}! Cause: {}", message, ExceptionUtils.getStackTrace(e));

            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }


    @Override
    public void deleteMessage(Message message) throws ServerException {
        LOGGER.debug("Deleting message {} ", message);
        try {
            messageMapper.deleteMessage(message);
        } catch (DataAccessException e) {
            LOGGER.info("Can't delete message {}! Cause: {}", message, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteComment(Message message) throws ServerException {
        LOGGER.debug("Deleting comment {} ", message);
        try {
            messageMapper.deleteComment(message);
        } catch (DataAccessException e) {
            LOGGER.info("Can't delete comment {}! Cause: {}", message, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public void putRating(Rating rating, Message message) throws ServerException {
        LOGGER.debug("Inserting rating {} for message {}", rating, message);
        try {
            messageMapper.insertRating(rating, message);
        } catch (DataAccessException e) {
            if (e.getClass() == DataIntegrityViolationException.class) {
                throw new ServerException(ServerError.MESSAGE_WRONG_ID);
            }
            LOGGER.info("Can't insert rating {}! Cause: {}", rating, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteRating(User user, Message message) throws ServerException {
        LOGGER.debug("Deleting rating for {} and {}", user, message);
        try {
            messageMapper.deleteRating(user, message);
        } catch (DataAccessException e) {
            LOGGER.info("Can't delete rating for {} and {}! Cause: {}", user, message, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public void updatePriority(MessagePriority priority, Message message) throws ServerException {
        LOGGER.debug("Updating priority for message {}", message);
        try {
            messageMapper.updatePriority(priority, message);
        } catch (DataAccessException e) {
            LOGGER.info("Can't update priority! Cause: {}", ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public MessageTree createTree(MessageTree messageTree) throws ServerException {
        LOGGER.debug("Creating new branch for tree {} ", messageTree);
        try {
            messageMapper.insertMessageTree(messageTree);
            messageMapper.updateToRoot(messageTree.getRootMessage(), messageTree);
            if (messageTree.getTags() != null) {
                messageMapper.insertTags(messageTree);
            }
        } catch (DataAccessException e) {
            if (e.getClass() == DataIntegrityViolationException.class) {
                throw new ServerException(ServerError.MESSAGE_WRONG_ID);
            }
            LOGGER.info("Can't create new branch for tree {}! Cause: {} ", messageTree, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
        return messageTree;
    }

    @Override
    public void makePublic(Message message) throws ServerException {
        LOGGER.debug("Updating message to public {}", message);
        try {
            messageMapper.updateToPublic(message);
        } catch (DataAccessException e) {
            LOGGER.info("Can't update message to public {}! Cause: {}", message, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteUnpublished(Message message) throws ServerException {
        LOGGER.debug("Deleting unpublished message history. {}", message);
        try {
            messageMapper.deleteUnpublishedHistory(message);
        } catch (DataAccessException e) {
            LOGGER.info("Can't delete unpublished message history! {}. Cause: {}", message, ExceptionUtils.getStackTrace(e));
            throw new ServerException(ServerError.SQL_SERVER_ERROR);
        }
    }
}
