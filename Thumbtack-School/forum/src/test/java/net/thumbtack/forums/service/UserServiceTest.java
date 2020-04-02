package net.thumbtack.forums.service;

import net.thumbtack.forums.database.dao.iface.ForumDao;
import net.thumbtack.forums.database.dao.iface.SessionDao;
import net.thumbtack.forums.database.dao.iface.UserDao;
import net.thumbtack.forums.dto.request.user.ChangePassDto;
import net.thumbtack.forums.dto.request.user.LoginDto;
import net.thumbtack.forums.dto.request.user.RegisterDto;
import net.thumbtack.forums.dto.response.user.UserActionResponseDto;
import net.thumbtack.forums.dto.response.user.UserInfoDto;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.Session;
import net.thumbtack.forums.model.User;
import net.thumbtack.forums.model.enums.BanStatus;
import net.thumbtack.forums.model.enums.UserType;
import net.thumbtack.forums.model.view.UserInfo;
import net.thumbtack.forums.util.Settings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @MockBean
    private UserDao userDao;
    @MockBean
    private SessionDao sessionDao;
    @MockBean
    private ForumDao forumDao;

    @Autowired
    private UserService userService;
    @Autowired
    private Settings settings;

    @Test
    void testRegisterUser() throws ServerException {
        User user = new User("dima", "pass");
        when(userDao.insert(any(User.class))).thenReturn(user);
        RegisterDto registerDto = new RegisterDto("dima", "em@em.com", "pass");
        UserActionResponseDto responseDto = userService.registerUser(registerDto, UUID.randomUUID().toString());
        assertEquals(user.getName(), registerDto.getName());
        verify(userDao, times(1)).insert(any(User.class));
        verify(sessionDao, times(1)).insertSession(any(Session.class));
    }

    @Test
    void testDeleteUser() throws ServerException {
        User user = new User("dima", "pass");
        when(sessionDao.getByCookie(anyString())).thenReturn(new Session(UUID.randomUUID().toString(), user));
        userService.deleteUser("sad");
        verify(userDao, times(1)).delete(user);
        verify(sessionDao, times(1)).deleteSession(anyString());
        verify(forumDao, times(1)).makeReadOnly(user);
    }

    @Test
    void restFailDeleteUser() throws ServerException {
        when(sessionDao.getByCookie(anyString())).thenReturn(null);
        assertThrows(ServerException.class, () -> userService.deleteUser("sad"));
    }

    @Test
    void testLogin() throws ServerException {
        User user = new User("dima", "pass");
        when(userDao.getByName(anyString())).thenReturn(user);
        LoginDto loginDto = new LoginDto("dima", "pass");
        UserActionResponseDto responseDto = userService.login(loginDto, "sad");
        assertEquals(responseDto.getName(), user.getName());
        verify(userDao, times(1)).getByName(anyString());
        verify(sessionDao, times(1)).insertSession(any(Session.class));
    }

    @Test
    void tesFailLoginWrongName() throws ServerException {
        User user = new User("dima", "pass");
        when(userDao.getByName(user.getName())).thenReturn(user);
        LoginDto loginDto = new LoginDto("wrong", "pass");
        assertThrows(ServerException.class, () -> userService.login(loginDto, "sad"));
    }

    @Test
    void tesFailLoginWrongPass() throws ServerException {
        User user = new User("dima", "pass");
        when(userDao.getByName(anyString())).thenReturn(user);
        LoginDto loginDto = new LoginDto("dima", "wrong");
        assertThrows(ServerException.class, () -> userService.login(loginDto, "sad"));
    }

    @Test
    void tesFailLoginDeleted() throws ServerException {
        User user = new User("dima", "pass");
        user.setDeleted(true);
        when(userDao.getByName(anyString())).thenReturn(user);
        LoginDto loginDto = new LoginDto("dima", "pass");
        assertThrows(ServerException.class, () -> userService.login(loginDto, "sad"));
    }

    @Test
    void testChangePass() throws ServerException {
        User user = new User("dima", "pass");
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        when(userDao.getByName(user.getName())).thenReturn(user);
        ChangePassDto passDto = new ChangePassDto("dima", "pass", "newPass");
        UserActionResponseDto responseDto = userService.changePassword(passDto, "sad");
        assertEquals(responseDto.getName(), user.getName());
        verify(userDao, times(1)).changePass(eq(user));
    }

    @Test
    void testFailChangePassWrongName() throws ServerException {
        User user = new User("dima", "pass");
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        when(userDao.getByName(user.getName())).thenReturn(user);
        ChangePassDto passDto = new ChangePassDto("wrong", "pass", "newPass");
        assertThrows(ServerException.class, () -> userService.changePassword(passDto, "sad"));
    }

    @Test
    void testFailChangePassWrongOldPass() throws ServerException {
        User user = new User("dima", "pass");
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        when(userDao.getByName(user.getName())).thenReturn(user);
        ChangePassDto passDto = new ChangePassDto("dima", "wrong", "newPass");
        assertThrows(ServerException.class, () -> userService.changePassword(passDto, "sad"));
    }

    @Test
    void testUpdateToSuper() throws ServerException {
        User user1 = new User("dima", "pass");
        user1.setUserType(UserType.SUPER);
        User user2 = new User("vova", "pass");
        Session session = new Session(UUID.randomUUID().toString(), user1);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        when(userDao.getById(anyLong())).thenReturn(user2);
        userService.updateToSuper(1, "sad");
        verify(userDao, times(1)).makeSuper(eq(user2));
    }

    @Test
    void testFailUpdateToSuper() throws ServerException {
        User user = new User("dima", "pass");
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        assertThrows(ServerException.class, () -> userService.updateToSuper(1, "sad"));
    }

    @Test
    void testBanUser() throws ServerException {
        User superUser = new User("admin", "pass");
        superUser.setUserType(UserType.SUPER);
        Session superSession = new Session(UUID.randomUUID().toString(), superUser);
        User user = new User("dima", "pass");
        user.setId(1);
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(superSession.getCookie())).thenReturn(superSession);
        when(sessionDao.getByCookie(session.getCookie())).thenReturn(session);
        when(userDao.getById(1)).thenReturn(user);
        userService.banUser(user.getId(), superSession.getCookie());
        verify(forumDao, never()).makeReadOnly(eq(user));
    }

    @Test
    void testBanUserForever() throws ServerException {
        User superUser = new User("admin", "pass");
        superUser.setUserType(UserType.SUPER);
        Session superSession = new Session(UUID.randomUUID().toString(), superUser);
        User user = new User("dima", "pass");
        user.setId(1);
        user.setBanCount(settings.getMaxBanCount() - 1);
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(superSession.getCookie())).thenReturn(superSession);
        when(sessionDao.getByCookie(session.getCookie())).thenReturn(session);
        when(userDao.getById(1)).thenReturn(user);
        userService.banUser(user.getId(), superSession.getCookie());
        user.setTimeBanExit(LocalDateTime.of(9999, Month.JANUARY, 1, 0, 0, 0));
        verify(userDao, times(1)).ban(user);
        verify(forumDao, times(1)).makeReadOnly(user);
    }

    @Test
    void testFailBanUser() throws ServerException {
        User user = new User("dima", "pass");
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(session.getCookie())).thenReturn(session);
        assertThrows(ServerException.class, () -> userService.banUser(user.getId(), session.getCookie()));
    }

    @Test
    void testGetUsersInfoNotSuper() throws ServerException {
        User user = new User("dima", "pass");
        Session session = new Session(UUID.randomUUID().toString(), user);
        UserInfo info1 = new UserInfo();
        info1.setName("dima");
        info1.setStatus(BanStatus.FULL);
        info1.setTimeBanExit(LocalDateTime.now());
        UserInfo info2 = new UserInfo();
        info2.setName("vova");
        info2.setStatus(BanStatus.LIMITED);
        info2.setTimeBanExit(LocalDateTime.now());
        List<UserInfo> userInfoList = Arrays.asList(info1, info2);
        when(sessionDao.getByCookie(session.getCookie())).thenReturn(session);
        when(userDao.getInfo(any(UserType.class))).thenReturn(userInfoList);
        List<UserInfoDto> infoDtoList = userService.getUsersInfo(session.getCookie());
        assertAll(
                () -> assertEquals(info1.getName(), infoDtoList.get(0).getName()),
                () -> assertEquals(info2.getName(), infoDtoList.get(1).getName()),
                () -> assertNull(infoDtoList.get(0).getTimeBanExit()),
                () -> assertNotNull(infoDtoList.get(1).getTimeBanExit())
        );
        verify(userDao, times(1)).getInfo(UserType.REGULAR);
    }

    @Test
    void testGetUsersInfoSuper() throws ServerException {
        User user = new User("dima", "pass");
        user.setUserType(UserType.SUPER);
        Session session = new Session(UUID.randomUUID().toString(), user);
        UserInfo info1 = new UserInfo();
        info1.setName("dima");
        info1.setStatus(BanStatus.FULL);
        info1.setTimeBanExit(LocalDateTime.now());
        UserInfo info2 = new UserInfo();
        info2.setName("vova");
        info2.setStatus(BanStatus.LIMITED);
        info2.setTimeBanExit(LocalDateTime.now());
        List<UserInfo> userInfoList = Arrays.asList(info1, info2);
        when(sessionDao.getByCookie(session.getCookie())).thenReturn(session);
        when(userDao.getInfo(any(UserType.class))).thenReturn(userInfoList);
        List<UserInfoDto> infoDtoList = userService.getUsersInfo(session.getCookie());
        assertAll(
                () -> assertEquals(info1.getName(), infoDtoList.get(0).getName()),
                () -> assertEquals(info2.getName(), infoDtoList.get(1).getName()),
                () -> assertNull(infoDtoList.get(0).getTimeBanExit()),
                () -> assertNotNull(infoDtoList.get(1).getTimeBanExit())
        );
        verify(userDao, times(1)).getInfo(UserType.SUPER);
    }

    @Test
    void testFailGetUsers() throws ServerException {
        when(sessionDao.getByCookie(anyString())).thenReturn(null);
        assertThrows(ServerException.class, () -> userService.getUsersInfo("sad"));
    }
}
