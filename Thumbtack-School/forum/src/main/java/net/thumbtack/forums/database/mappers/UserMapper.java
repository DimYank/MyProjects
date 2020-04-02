package net.thumbtack.forums.database.mappers;

import net.thumbtack.forums.model.User;
import net.thumbtack.forums.model.enums.UserType;
import net.thumbtack.forums.model.view.UserInfo;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {

    @Insert("INSERT INTO user(name, email, password, timeRegistered, userType) VALUES " +
            "(#{user.name}, #{user.email}, #{user.password}, #{user.timeRegistered}, #{user.userType})")
    @Options(useGeneratedKeys = true, keyProperty = "user.id")
    void insert(@Param("user") User user);

    @Select("SELECT * FROM user WHERE id=#{id}")
    User getById(long id);

    @Select("SELECT * FROM user WHERE name=#{user}")
    User getByName(String userName);

    @Select("<script>" +
            "SELECT <if test=\"userType.ordinal == 1\">user.email, user.userType,</if> user.id, user.name, user.timeRegistered, user.deleted, user.status, user.timeBanExit, user.banCount, " +
            "CASE WHEN (session.cookie IS NULL) THEN 0 ELSE 1 END AS online " +
            "FROM user left join session on (user.id = session.userId)" +
            "</script>")
    List<UserInfo> getInfo(@Param("userType") UserType userType);

    @Update("UPDATE user SET userType='SUPER',status='FULL',timeBanExit='2000-01-01' WHERE id=#{id}")
    void makeSuper(User user);

    @Update("UPDATE user SET password=#{user.password} WHERE id=#{user.id}")
    void changePass(@Param("user") User user);

    @Update("UPDATE user SET deleted=1 WHERE id=#{id}")
    void delete(User user);

    @Update("UPDATE user SET status='LIMITED', timeBanExit=#{user.timeBanExit}, banCount=banCount+1 WHERE id=#{user.id}")
    void ban(@Param("user") User user);

    @Update("UPDATE user SET status='FULL', timeBanExit='2000-01-01' WHERE status='LIMITED' AND timeBanExit < #{date}")
    void unbanAll(LocalDateTime date);

    @Delete("DELETE FROM user WHERE name <> 'Admin'")
    void deleteAll();
}
