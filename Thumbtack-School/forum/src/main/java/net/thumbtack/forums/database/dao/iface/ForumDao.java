package net.thumbtack.forums.database.dao.iface;

import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.Forum;
import net.thumbtack.forums.model.User;

import java.util.List;

public interface ForumDao {

    Forum insert(Forum forum) throws ServerException;

    List<Forum> getAll() throws ServerException;

    Forum getById(int id) throws ServerException;

    void makeReadOnly(User user) throws ServerException;

    void delete(int id) throws ServerException;

    void deleteAll() throws ServerException;
}
