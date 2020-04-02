package net.thumbtack.forums.database.mappers;

import net.thumbtack.forums.model.*;
import net.thumbtack.forums.model.enums.MessagePriority;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;

import java.util.List;

@Mapper
public interface MessageMapper {

    @Insert("INSERT INTO message_tree(forumId, subject, priority) VALUES (#{forum.id}, #{subject}, #{priority.ordinal})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertMessageTree(MessageTree messageTree);

    @Insert("INSERT INTO message(creatorId, created, treeId, numberInForum) SELECT #{message.creator.id}, #{message.created}, " +
            "#{message.tree.id}, COUNT(id) FROM message_tree WHERE forumId = (SELECT forumId FROM message_tree WHERE id=#{message.tree.id})")
    @Options(useGeneratedKeys = true, keyProperty = "message.id")
    void insertMessage(@Param("message") Message message);

    @Insert("INSERT INTO message(creatorId, created, treeId, parentId)" +
            " VALUES(#{message.creator.id}, #{message.created}, #{message.tree.id}, #{message.parent.id})")
    @Options(useGeneratedKeys = true, keyProperty = "message.id")
    void insertComment(@Param("message") Message message);

    @Insert("INSERT INTO message_history(messageId, body, state) VALUES (#{history.message.id}, #{history.body}, #{history.state})")
    @Options(useGeneratedKeys = true, keyProperty = "history.id")
    void insertHistory(@Param("history") MessageHistory history);

    @Insert({
            "<script>",
            "INSERT INTO message_tags(treeId, tag) VALUES",
            "<foreach item='tag' collection='tags' separator=','>",
            "(#{id}, #{tag})",
            "</foreach>",
            "</script>"
    })
    void insertTags(MessageTree messageTree);

    @Insert("INSERT INTO message_rating(userId, messageId, rating) VALUES (#{rating.user.id}, #{message.id}, #{rating.rating})" +
            "ON DUPLICATE KEY UPDATE rating=#{rating.rating}")
    @Options(useGeneratedKeys = true, keyProperty = "rating.id")
    void insertRating(@Param("rating") Rating rating, @Param("message") Message message);

    @Update("UPDATE message_history SET body=#{body} WHERE id=#{id}")
    void updateHistory(MessageHistory history);

    @Update("UPDATE message_tree SET priority=#{priority.ordinal} WHERE id=(SELECT treeId FROM message WHERE id=#{message.id})")
    void updatePriority(@Param("priority") MessagePriority priority, @Param("message") Message message);

    @Update("UPDATE message SET parentId=NULL, treeId=#{tree.id} WHERE id=#{message.id}")
    void updateToRoot(@Param("message") Message message, @Param("tree") MessageTree tree);

    @Update("UPDATE message_history SET state='PUBLISHED' WHERE messageId=#{id}")
    void updateToPublic(Message message);

    @Select("SELECT * FROM message_tree WHERE id=(SELECT treeId FROM message WHERE id=#{id})")
    @Results(id = "tree", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "forum", column = "forumId", javaType = Forum.class,
                    one = @One(select = "net.thumbtack.forums.database.mappers.ForumMapper.getById", fetchType = FetchType.LAZY)),
            @Result(property = "subject", column = "subject"),
            @Result(property = "priority", column = "priority", typeHandler = EnumOrdinalTypeHandler.class),
            @Result(property = "rootMessage", column = "id", javaType = Message.class,
                    one = @One(select = "net.thumbtack.forums.database.mappers.MessageMapper.getTreeRootMessage", fetchType = FetchType.LAZY)),
            @Result(property = "tags", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.forums.database.mappers.MessageMapper.getTreeTags", fetchType = FetchType.LAZY))
    })
    MessageTree getMessageTree(long messageId);

    @Select("SELECT id, creatorId, created FROM message WHERE id=#{id}")
    @Results(id = "getMessage", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "created", column = "created"),
            @Result(property = "creator", column = "creatorId", javaType = User.class,
                    one = @One(select = "net.thumbtack.forums.database.mappers.UserMapper.getById", fetchType = FetchType.LAZY)),
            @Result(property = "history", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.forums.database.mappers.MessageMapper.getMessageHistory", fetchType = FetchType.LAZY)),
            @Result(property = "rating", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.forums.database.mappers.MessageMapper.getMessageRating", fetchType = FetchType.LAZY)),
            @Result(property = "parent", column = "id", javaType = Message.class,
                    one = @One(select = "net.thumbtack.forums.database.mappers.MessageMapper.getMessageParent", fetchType = FetchType.LAZY)),
            @Result(property = "comments", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.forums.database.mappers.MessageMapper.getMessageComments", fetchType = FetchType.LAZY)),
            @Result(property = "tree", column = "id", javaType = MessageTree.class,
                    one = @One(select = "net.thumbtack.forums.database.mappers.MessageMapper.getMessageTree", fetchType = FetchType.LAZY))
    })
    Message getMessage(long id);

    @Select("SELECT id, creatorId, created FROM message WHERE id=(SELECT parentId FROM message WHERE id=#{id})")
    @ResultMap("getMessage")
    Message getMessageParent(long id);

    @Select("SELECT id, creatorId FROM message WHERE parentId=#{messageId}")
    @ResultMap("getMessage")
    List<Message> getMessageComments(long messageId);

    @Select("SELECT * FROM message_tree WHERE forumId = #{forumId}")
    @ResultMap("tree")
    List<MessageTree> getForumMessages(int forumId);

    @Select("SELECT tag FROM message_tags WHERE treeId=#{treeId}")
    List<String> getTreeTags();

    @Select("SELECT id, body, state FROM message_history WHERE messageId=#{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "body", column = "body"),
            @Result(property = "state", column = "state"),
            @Result(property = "message", column = "id", javaType = Message.class,
                    one = @One(select = "net.thumbtack.forums.database.mappers.MessageMapper.getHistoryMessage", fetchType = FetchType.LAZY))
    })
    List<MessageHistory> getMessageHistory(long id);

    @Select("SELECT id, creatorId, created FROM message WHERE id=(SELECT messageId FROM message_history WHERE id=#{historyId})")
    @ResultMap("getMessage")
    Message getHistoryMessage(long historyId);

    @Select("SELECT id, userId, rating FROM message_rating WHERE messageId=#{messageId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "user", column = "userId", javaType = User.class,
                    one = @One(select = "net.thumbtack.forums.database.mappers.UserMapper.getById", fetchType = FetchType.LAZY)),
            @Result(property = "rating", column = "rating")
    })
    List<Rating> getMessageRating(long messageId);

    @Select("SELECT id, creatorId FROM message WHERE treeId=#{treeId} AND parentId IS NULL")
    @ResultMap("getMessage")
    Message getTreeRootMessage(long treeId);

    @Delete("DELETE FROM message_tree WHERE id=(SELECT treeId FROM message WHERE id=#{id})")
    void deleteMessage(Message message);

    @Delete("DELETE FROM message WHERE id=#{id}")
    void deleteComment(Message message);

    @Delete("DELETE FROM message_rating WHERE messageId=#{message.id} AND userId=#{user.id}")
    void deleteRating(@Param("user") User user, @Param("message") Message message);

    @Delete("DELETE FROM message_history WHERE messageId=#{id} AND state='UNPUBLISHED'")
    void deleteUnpublishedHistory(Message message);

    @Delete("DELETE FROM message_tree")
    void deleteAll();
}
