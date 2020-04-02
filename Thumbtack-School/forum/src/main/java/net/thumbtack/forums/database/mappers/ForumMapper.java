package net.thumbtack.forums.database.mappers;

import net.thumbtack.forums.model.Forum;
import net.thumbtack.forums.model.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Mapper
public interface ForumMapper {

    @Insert("INSERT INTO forum(name, creator, type) VALUES (#{forum.name}, #{forum.creator.id}, #{forum.type})")
    @Options(useGeneratedKeys = true, keyProperty = "forum.id")
    void insert(@Param("forum") Forum forum);

    @Select("SELECT * FROM forum")
    @ResultMap("getById")
    List<Forum> getAll();

    @Select("SELECT * FROM forum WHERE id=#{id}")
    @Results(id = "getById", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "creator", column = "creator", javaType = User.class,
                    one = @One(select = "net.thumbtack.forums.database.mappers.UserMapper.getById", fetchType = FetchType.LAZY)),
            @Result(property = "type", column = "type"),
            @Result(property = "readOnly", column = "readOnly"),
            @Result(property = "messages", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.forums.database.mappers.MessageMapper.getForumMessages", fetchType = FetchType.LAZY))
    })
    Forum getById(int id);

    @Update("UPDATE forum SET readOnly=1 WHERE creator=#{id} AND type='MODERATED'")
    void updateToReadOnly(User user);

    @Delete("DELETE FROM forum WHERE id=#{id}")
    void delete(int id);

    @Delete("DELETE FROM forum")
    void deleteAll();
}
