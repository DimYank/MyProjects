package net.thumbtack.forums.service;

import net.thumbtack.forums.database.dao.iface.ForumDao;
import net.thumbtack.forums.database.dao.iface.SessionDao;
import net.thumbtack.forums.database.dao.iface.UserDao;
import net.thumbtack.forums.dto.request.user.ChangePassDto;
import net.thumbtack.forums.dto.request.user.LoginDto;
import net.thumbtack.forums.dto.request.user.RegisterDto;
import net.thumbtack.forums.dto.response.user.UserActionResponseDto;
import net.thumbtack.forums.dto.response.user.UserInfoDto;
import net.thumbtack.forums.error.ServerError;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.Session;
import net.thumbtack.forums.model.User;
import net.thumbtack.forums.model.enums.BanStatus;
import net.thumbtack.forums.model.enums.UserType;
import net.thumbtack.forums.model.view.UserInfo;
import net.thumbtack.forums.util.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = DataAccessException.class)
public class UserService extends ServiceBase {
    private UserDao userDao;
    private ForumDao forumDao;
    private Settings settings;

    @Autowired
    public UserService(UserDao userDao, ForumDao forumDao, SessionDao sessionDao, Settings settings) {
        super(sessionDao);
        this.userDao = userDao;
        this.forumDao = forumDao;
        this.settings = settings;
    }

    public UserActionResponseDto registerUser(RegisterDto dto, String newCookie) throws ServerException {
        User user = new User(dto.getName(), dto.getEmail(), dto.getPassword());
        return addSession(userDao.insert(user), newCookie);
    }

    public void deleteUser(String cookie) throws ServerException {
        Session session = checkSession(cookie);
        userDao.delete(session.getUser());
        sessionDao.deleteSession(cookie);
        forumDao.makeReadOnly(session.getUser());
    }

    public UserActionResponseDto login(LoginDto dto, String newCookie) throws ServerException {
        User user = checkUsernameAndPass(dto.getName(), dto.getPassword());
        return addSession(user, newCookie);
    }

    public void logout(String cookie) throws ServerException {
        sessionDao.deleteSession(cookie);
    }

    public UserActionResponseDto changePassword(ChangePassDto dto, String cookie) throws ServerException {
        Session session = checkSession(cookie);
        checkDtoUsername(session, dto.getName());
        checkUsernameAndPass(dto.getName(), dto.getOldPassword());
        User user = session.getUser();
        user.setPassword(dto.getPassword());
        userDao.changePass(user);
        return new UserActionResponseDto(session.getUser().getId(),
                session.getUser().getName(), session.getUser().getEmail());
    }

    public void updateToSuper(long id, String cookie) throws ServerException {
        checkIfSuperUser(cookie);
        userDao.makeSuper(userDao.getById(id));
    }

    public void banUser(long id, String cookie) throws ServerException {
        checkIfSuperUser(cookie);
        User userToBan = getUserByIdNotSuper(id);
        if (userToBan.getBanCount() == settings.getMaxBanCount() - 1) {
            LocalDateTime time = LocalDateTime.of(9999, Month.JANUARY, 1, 0, 0, 0);
            userToBan.setTimeBanExit(time);
            userDao.ban(userToBan);
            forumDao.makeReadOnly(userToBan);
        } else {
            LocalDateTime time = LocalDateTime.now().plusDays(settings.getBanTime()).withHour(0).withMinute(0).withSecond(0).withNano(0);
            userToBan.setTimeBanExit(time);
            userDao.ban(userToBan);
        }
    }

    public List<UserInfoDto> getUsersInfo(String cookie) throws ServerException {
        User requestUser = checkSession(cookie).getUser();
        List<UserInfoDto> listOfData;
        listOfData = makeInfoDto(userDao.getInfo(requestUser.getUserType()));
        for (UserInfoDto dto : listOfData) {
            if (dto.getStatus() == BanStatus.FULL) {
                dto.setTimeBanExit(null);
            }
        }
        return listOfData;
    }

    private UserActionResponseDto addSession(User user, String newCookie) throws ServerException {
        Session session = new Session(newCookie, user);
        sessionDao.insertSession(session);
        return new UserActionResponseDto(session.getUser().getId(),
                session.getUser().getName(), session.getUser().getEmail());
    }

    private User checkUsernameAndPass(String username, String password) throws ServerException {
        User userFromDB = userDao.getByName(username);
        if (userFromDB == null) {
            ServerError er = ServerError.USER_WRONG_NAME;
            er.setMessage(String.format(er.getMessage(), username));
            throw new ServerException(er);
        }
        if (userFromDB.isDeleted()) {
            throw new ServerException(ServerError.USER_DELETED);
        }
        if (!password.equals(userFromDB.getPassword())) {
            throw new ServerException(ServerError.USER_WRONG_PASSWORD);
        }
        return userFromDB;
    }

    private void checkDtoUsername(Session session, String username) throws ServerException {
        if (!session.getUser().getName().equals(username)) {
            ServerError er = ServerError.USER_WRONG_NAME;
            er.setMessage(String.format(er.getMessage(), username));
            throw new ServerException(ServerError.USER_WRONG_NAME);
        }
    }

    private User checkIfSuperUser(String cookie) throws ServerException {
        User user = checkSession(cookie).getUser();
        if (user.getUserType() != UserType.SUPER) {
            throw new ServerException(ServerError.USER_NOT_SUPER);
        }
        return user;
    }

    private User getUserByIdNotSuper(long id) throws ServerException {
        User user = userDao.getById(id);
        if (user == null) {
            throw new ServerException(ServerError.USER_NOT_FOUND);
        }
        if (user.getUserType() == UserType.SUPER) {
            throw new ServerException(ServerError.CANT_BAN_SUPER);
        }
        return user;
    }

    private List<UserInfoDto> makeInfoDto(List<UserInfo> infoList) {
        List<UserInfoDto> dtoList = new ArrayList<>();
        for (UserInfo info : infoList) {
            UserType thisUserType = info.getUserType();
            Boolean superr = thisUserType == null ? null : thisUserType == UserType.SUPER;
            dtoList.add(new UserInfoDto(info.getId(), info.getName(), info.getTimeRegistered(), info.isOnline(), info.isDeleted(),
                    info.getStatus(), info.getTimeBanExit(), info.getBanCount(), info.getEmail(), superr));
        }
        return dtoList;
    }

    @Scheduled(cron = "0 0 0 * * *")
    void unbanUsersScheduled() throws ServerException {
        LocalDateTime dateTime = LocalDateTime.now().withMinute(1);
        userDao.unbanUsers(dateTime);
    }
}
