package net.thumbtack.forums.database.mappers;

import net.thumbtack.forums.model.Session;
import net.thumbtack.forums.model.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

@Mapper
public interface SessionMapper {

    @Insert("INSERT INTO session(cookie, userId) VALUES (#{cookie}, #{user.id})" +
            " ON DUPLICATE KEY UPDATE cookie=#{cookie}, userId=#{user.id}")
    void insert(Session session);

    @Delete("DELETE FROM session WHERE cookie=#{cookie}")
    void delete(String cookie);

    @Select("SELECT cookie, userId FROM session WHERE cookie=#{cookie}")
    @Results({
            @Result(property = "cookie", column = "cookie"),
            @Result(property = "user", column = "userId", javaType = User.class,
                    one = @One(select = "net.thumbtack.forums.database.mappers.UserMapper.getById", fetchType = FetchType.EAGER))
    })
    Session getByCookie(String cookie);

    @Delete("DELETE FROM session")
    void deleteAll();
}
