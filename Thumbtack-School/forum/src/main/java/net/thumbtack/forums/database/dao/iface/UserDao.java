package net.thumbtack.forums.database.dao.iface;

import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.User;
import net.thumbtack.forums.model.enums.UserType;
import net.thumbtack.forums.model.view.UserInfo;

import java.time.LocalDateTime;
import java.util.List;

public interface UserDao {
    User insert(User user) throws ServerException;

    User getById(long id) throws ServerException;

    User getByName(String name) throws ServerException;

    void makeSuper(User user) throws ServerException;

    void changePass(User user) throws ServerException;

    void delete(User user) throws ServerException;

    void ban(User user) throws ServerException;

    void unbanUsers(LocalDateTime dateTime) throws ServerException;

    List<UserInfo> getInfo(UserType userType) throws ServerException;
}
