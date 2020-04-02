package net.thumbtack.forums.database.dao.iface;

import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.Session;

public interface SessionDao {
    void insertSession(Session session) throws ServerException;

    void deleteSession(String cookie) throws ServerException;

    Session getByCookie(String cookie) throws ServerException;
}
