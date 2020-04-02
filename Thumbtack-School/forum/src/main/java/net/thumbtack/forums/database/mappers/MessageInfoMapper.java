package net.thumbtack.forums.database.mappers;

import net.thumbtack.forums.model.MessageHistory;
import net.thumbtack.forums.model.User;
import net.thumbtack.forums.model.enums.OrderValue;
import net.thumbtack.forums.model.view.CommentInfo;
import net.thumbtack.forums.model.view.MessageInfo;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;

import java.util.List;

@Mapper
public interface MessageInfoMapper {

    @Select({"SELECT " +
            "message.id, creatorId, subject, priority, created, " +
            "AVG(message_rating.rating) AS rating, " +
            "COUNT(message_rating.rating) AS rated " +
            "FROM message " +
            "JOIN message_tree ON (message.treeId=message_tree.id) " +
            "JOIN message_rating ON (message.id=message_rating.messageId) " +
            "WHERE message.id=#{id}"
    })
    @Results(id = "message", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "creatorId", property = "creator", javaType = User.class,
                    one = @One(select = "net.thumbtack.forums.database.mappers.UserMapper.getById", fetchType = FetchType.EAGER)),
            @Result(column = "subject", property = "subject"),
            @Result(column = "priority", property = "priority", typeHandler = EnumOrdinalTypeHandler.class),
            @Result(column = "id", property = "tags", javaType = List.class,
                    many = @Many(select = "net.thumbtack.forums.database.mappers.MessageInfoMapper.getTags", fetchType = FetchType.EAGER)),
            @Result(column = "created", property = "created"),
            @Result(column = "rating", property = "rating"),
            @Result(column = "rated", property = "rated")
    })
    MessageInfo getById(long id);

    @Select("SELECT tag FROM message_tags WHERE treeId = (SELECT treeId FROM message WHERE id = #{id})")
    List<String> getTags(long id);

    @Select({"SELECT " +
            "message.id, creatorId, created, " +
            "AVG(rating) AS rating, " +
            "COUNT(rating) AS rated " +
            "FROM message " +
            "LEFT JOIN message_rating ON (message.id=message_rating.messageId) " +
            "WHERE parentId=#{id} GROUP BY message.id"
    })
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "creatorId", property = "creator", javaType = User.class,
                    one = @One(select = "net.thumbtack.forums.database.mappers.UserMapper.getById", fetchType = FetchType.EAGER)),
            @Result(column = "created", property = "created"),
            @Result(column = "rating", property = "rating"),
            @Result(column = "rated", property = "rated"),
            @Result(column = "id", property = "comments", javaType = List.class,
                    many = @Many(select = "net.thumbtack.forums.database.mappers.MessageInfoMapper.getCommentInfoAsc", fetchType = FetchType.EAGER))
    })
    List<CommentInfo> getCommentInfoAsc(long id);

    @Select({"SELECT " +
            "message.id, creatorId, created, " +
            "AVG(rating) AS rating, " +
            "COUNT(rating) AS rated " +
            "FROM message " +
            "LEFT JOIN message_rating ON (message.id=message_rating.messageId) " +
            "WHERE parentId=#{id} GROUP BY message.id ORDER BY created DESC"
    })
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "creatorId", property = "creator", javaType = User.class,
                    one = @One(select = "net.thumbtack.forums.database.mappers.UserMapper.getById", fetchType = FetchType.EAGER)),
            @Result(column = "created", property = "created"),
            @Result(column = "rating", property = "rating"),
            @Result(column = "rated", property = "rated"),
            @Result(column = "id", property = "comments", javaType = List.class,
                    many = @Many(select = "net.thumbtack.forums.database.mappers.MessageInfoMapper.getCommentInfoDesc", fetchType = FetchType.EAGER))
    })
    List<CommentInfo> getCommentInfoDesc(long id);

    @Select({"<script>" +
            "SELECT body, state FROM message_history" +
            "<where>" +
            "messageId = #{id} " +
            "<if test=\"allVersions == false\">" +
            "AND id = (SELECT max(id) FROM message_history WHERE messageId=#{id}) " +
            "</if>" +
            "<if test=\"unpublished == false\">" +
            "AND state = 'PUBLISHED' " +
            "</if>" +
            "</where>" +
            "ORDER BY id DESC" +
            "</script>"
    })
    @Results({
            @Result(column = "body", property = "body"),
            @Result(column = "state", property = "state")
    })
    List<MessageHistory> getHistory(@Param("id") long id, @Param("allVersions") boolean allVersions, @Param("unpublished") boolean unpublished);

    @Select("<script>" +
            "SELECT " +
            "message.id, creatorId, subject, priority, created, " +
            "AVG(message_rating.rating) AS rating, " +
            "COUNT(message_rating.rating) AS rated " +
            "FROM message " +
            "LEFT JOIN message_tree ON (message.treeId=message_tree.id) " +
            "LEFT JOIN message_rating ON (message.id=message_rating.messageId) " +
            "WHERE parentId IS NULL AND treeId IN (SELECT id FROM message_tree WHERE forumId=#{forumId}) " +
            "<if test=\"tags != null\">" +
            "AND treeId IN (SELECT treeId FROM message_tags WHERE tag IN " +
            "<foreach item=\"tag\" collection=\"tags\" open=\"(\" separator=\",\" close=\") \">" +
            "#{tag}" +
            "</foreach>" +
            ")" +
            "</if>" +
            "AND numberInForum > #{offset} " +
            "GROUP BY message.id " +
            "ORDER BY priority DESC, created <if test='!order.equals(\"DESC\")'>DESC</if><if test='order.equals(\"DESC\")'>ASC</if> " +
            "LIMIT #{limit}" +
            "</script>")
    @ResultMap("message")
    List<MessageInfo> getAllByForumId(@Param("forumId") int forumId, @Param("tags") List<String> tags,
                                      @Param("order") OrderValue order, @Param("offset") long offset, @Param("limit") int limit);
}
