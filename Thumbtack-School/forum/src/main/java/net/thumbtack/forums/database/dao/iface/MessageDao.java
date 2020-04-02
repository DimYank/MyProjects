package net.thumbtack.forums.database.dao.iface;

import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.Message;
import net.thumbtack.forums.model.MessageTree;
import net.thumbtack.forums.model.Rating;
import net.thumbtack.forums.model.User;
import net.thumbtack.forums.model.enums.MessagePriority;

public interface MessageDao {
    MessageTree insert(MessageTree messageTree) throws ServerException;

    Message insertComment(Message message) throws ServerException;

    MessageTree getMessageTree(long messageId) throws ServerException;

    Message getMessage(long id) throws ServerException;

    void editPublished(Message message) throws ServerException;

    void editUnpublished(Message message) throws ServerException;

    void deleteMessage(Message message) throws ServerException;

    void deleteComment(Message message) throws ServerException;

    void putRating(Rating rating, Message message) throws ServerException;

    void deleteRating(User user, Message message) throws ServerException;

    void updatePriority(MessagePriority priority, Message message) throws ServerException;

    MessageTree createTree(MessageTree messageTree) throws ServerException;

    void makePublic(Message message) throws ServerException;

    void deleteUnpublished(Message message) throws ServerException;
}
