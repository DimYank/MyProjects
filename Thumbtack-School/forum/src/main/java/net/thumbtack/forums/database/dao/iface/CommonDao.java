package net.thumbtack.forums.database.dao.iface;

import net.thumbtack.forums.error.ServerException;

public interface CommonDao {

    void clearDB() throws ServerException;
}
