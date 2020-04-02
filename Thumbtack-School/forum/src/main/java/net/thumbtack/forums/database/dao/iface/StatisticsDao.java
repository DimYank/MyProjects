package net.thumbtack.forums.database.dao.iface;

import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.view.MessageRating;
import net.thumbtack.forums.model.view.UserRating;

import java.util.List;

public interface StatisticsDao {
    Long getMessagesCount(int forumId) throws ServerException;

    Long getCommentsCount(int forumId) throws ServerException;

    List<MessageRating> getRating(int forumId, boolean forMessages, long offset, int limit) throws ServerException;

    List<UserRating> getUsersRating(int forumId, long offset, int limit) throws ServerException;
}
