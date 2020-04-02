package net.thumbtack.forums.service;


import net.thumbtack.forums.database.dao.iface.SessionDao;
import net.thumbtack.forums.error.ServerError;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.Session;
import net.thumbtack.forums.model.User;
import net.thumbtack.forums.model.enums.BanStatus;

public abstract class ServiceBase {
    protected SessionDao sessionDao;

    public ServiceBase(SessionDao sessionDao) {
        this.sessionDao = sessionDao;
    }

    protected Session checkSession(String cookie) throws ServerException {
        Session session = sessionDao.getByCookie(cookie);
        if (session == null) {
            throw new ServerException(ServerError.NO_VALID_SESSION);
        }
        return session;
    }

    protected User getUserByCookieNotBanned(String cookie) throws ServerException {
        Session session = checkSession(cookie);
        if (session.getUser().getStatus() == BanStatus.LIMITED) {
            throw new ServerException(ServerError.USER_BANNED);
        }
        return session.getUser();
    }
}
