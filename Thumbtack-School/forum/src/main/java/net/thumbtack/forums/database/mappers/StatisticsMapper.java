package net.thumbtack.forums.database.mappers;

import net.thumbtack.forums.model.view.MessageRating;
import net.thumbtack.forums.model.view.UserRating;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StatisticsMapper {

    @Select("<script>" +
            "SELECT COUNT(id) FROM message WHERE parentId IS NULL " +
            "<if test=\"forumId > 0\">" +
            "AND treeId IN (SELECT id FROM message_tree WHERE forumId=#{forumId})" +
            "</if>" +
            "</script>")
    Long getMessageCount(@Param("forumId") int forumId);

    @Select("<script>" +
            "SELECT COUNT(id) FROM message WHERE parentId IS NOT NULL " +
            "<if test=\"forumId > 0\">" +
            "AND treeId IN (SELECT id FROM message_tree WHERE forumId=#{forumId})" +
            "</if>" +
            "</script>")
    Long getCommentCount(@Param("forumId") int forumId);

    @Select("<script>" +
            "SELECT message.id, CASE WHEN(AVG(rating) IS NULL) THEN 0 ELSE AVG(rating) END AS rating " +
            "FROM message " +
            "LEFT JOIN message_rating ON(message.id=message_rating.messageId) " +
            "WHERE <if test=\"forumId == 0 or !forMessages\">message.id</if><if test=\"forumId > 0 and forMessages\">message.numberInForum</if> > #{offset} " +
            "AND parentId IS <if test=\"!forMessages\">NOT</if> NULL " +
            "<if test=\"forumId > 0\">" +
            "AND treeId IN (SELECT id FROM message_tree WHERE forumId=#{forumId}) " +
            "</if>" +
            "GROUP BY message.id " +
            "ORDER BY rating DESC " +
            "<if test=\"limit > 0\"> LIMIT #{limit} </if>" +
            "</script>")
    List<MessageRating> getRating(@Param("forumId") int forumId, @Param("forMessages") boolean forMessages,
                                  @Param("offset") long offset, @Param("limit") int limit);

    @Select("<script>" +
            "SELECT user.id, user.name, CASE WHEN(AVG(rating) IS NULL) THEN 0 ELSE AVG(rating) END AS rating " +
            "FROM user " +
            "LEFT JOIN message ON(user.id=message.creatorId) " +
            "LEFT JOIN message_rating ON(message.id=message_rating.messageId)" +
            "WHERE user.id > #{offset} " +
            "<if test=\"forumId > 0\">" +
            "AND treeId IN (SELECT id FROM message_tree WHERE forumId=#{forumId}) " +
            "</if>" +
            "GROUP BY user.id " +
            "ORDER BY rating DESC " +
            "<if test=\"limit > 0\"> LIMIT #{limit} </if>" +
            "</script>")
    List<UserRating> getUsersRating(@Param("forumId") int forumId, @Param("offset") long offset, @Param("limit") int limit);
}
