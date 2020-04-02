package net.thumbtack.forums.database.dao.iface;

import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.MessageHistory;
import net.thumbtack.forums.model.enums.OrderValue;
import net.thumbtack.forums.model.view.CommentInfo;
import net.thumbtack.forums.model.view.MessageInfo;

import java.util.List;

public interface MessageInfoDao {
    MessageInfo getById(long id) throws ServerException;

    List<CommentInfo> getCommentInfo(long id, OrderValue order) throws ServerException;

    List<MessageHistory> getHistory(long id, boolean allVersions, boolean unpublished) throws ServerException;

    List<MessageInfo> getAllByForumId(int forumId, List<String> tags, OrderValue order, long offset, int limit) throws ServerException;
}
