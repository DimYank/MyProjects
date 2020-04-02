package net.thumbtack.forums.service;

import net.thumbtack.forums.database.dao.iface.SessionDao;
import net.thumbtack.forums.database.dao.iface.StatisticsDao;
import net.thumbtack.forums.dto.response.information.MessageCountDto;
import net.thumbtack.forums.dto.response.information.MessagesRatingDto;
import net.thumbtack.forums.dto.response.information.ServerSettingsDto;
import net.thumbtack.forums.dto.response.information.UserRatingDto;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.Session;
import net.thumbtack.forums.model.User;
import net.thumbtack.forums.model.enums.UserType;
import net.thumbtack.forums.model.view.MessageRating;
import net.thumbtack.forums.model.view.UserRating;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class StatisticsServiceTest {

    @MockBean
    private StatisticsDao statisticsDao;
    @MockBean
    private SessionDao sessionDao;

    @Autowired
    private StatisticsService informationService;

    @Test
    void testGetServerSettingsWithCookie() throws ServerException {
        User user = new User("dima", "pass");
        user.setUserType(UserType.SUPER);
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        ServerSettingsDto settingsDto = informationService.getServerSettings(session.getCookie());
        assertAll(
                () -> assertNotEquals(0, settingsDto.getMaxNameLength()),
                () -> assertNotEquals(0, settingsDto.getMinPasswordLength()),
                () -> assertNotNull(settingsDto.getBanTime()),
                () -> assertNotNull(settingsDto.getMaxBanCount())
        );
    }

    @Test
    void testGetServerSettingsNoCookie() throws ServerException {
        ServerSettingsDto settingsDto = informationService.getServerSettings(null);
        assertAll(
                () -> assertNotEquals(0, settingsDto.getMaxNameLength()),
                () -> assertNotEquals(0, settingsDto.getMinPasswordLength()),
                () -> assertNull(settingsDto.getBanTime()),
                () -> assertNull(settingsDto.getMaxBanCount())
        );
    }

    @Test
    void testGetMessageAndCommentsCount() throws ServerException {
        User user = new User("dima", "pass");
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        when(statisticsDao.getCommentsCount(anyInt())).thenReturn(2L);
        when(statisticsDao.getMessagesCount(anyInt())).thenReturn(3L);
        MessageCountDto countDto = informationService.getMessageAndCommentCount(1, "das");
        assertAll(
                () -> assertEquals(2L, countDto.getCommentCount()),
                () -> assertEquals(3L, countDto.getMessageCount())
        );
    }

    @Test
    void testGetRating() throws ServerException {
        User user = new User("dima", "pass");
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        List<MessageRating> ratingList = Arrays.asList(new MessageRating(1, 3), new MessageRating(2, 4));
        when(statisticsDao.getRating(anyInt(), anyBoolean(), anyLong(), anyInt())).thenReturn(ratingList);
        List<MessagesRatingDto> ratingDtoList = informationService.getRating(1, true, 2, 3, "asd");
        assertAll(
                () -> assertEquals(1, ratingDtoList.get(0).getId()),
                () -> assertEquals(2, ratingDtoList.get(1).getId()),
                () -> assertEquals(3, ratingDtoList.get(0).getRating()),
                () -> assertEquals(4, ratingDtoList.get(1).getRating())
        );
    }

    @Test
    void testGetUsersRating() throws ServerException {
        User user = new User("dima", "pass");
        Session session = new Session(UUID.randomUUID().toString(), user);
        when(sessionDao.getByCookie(anyString())).thenReturn(session);
        List<UserRating> ratingList = Arrays.asList(new UserRating(1, "dima", 3), new UserRating(2, "vova", 4));
        when(statisticsDao.getUsersRating(anyInt(), anyLong(), anyInt())).thenReturn(ratingList);
        List<UserRatingDto> ratingDtoList = informationService.getUsersRating(1, 1, 1, "sad");
        assertAll(
                () -> assertEquals(1, ratingDtoList.get(0).getId()),
                () -> assertEquals(2, ratingDtoList.get(1).getId()),
                () -> assertEquals(3, ratingDtoList.get(0).getRating()),
                () -> assertEquals(4, ratingDtoList.get(1).getRating()),
                () -> assertEquals("dima", ratingDtoList.get(0).getName()),
                () -> assertEquals("vova", ratingDtoList.get(1).getName())
        );
    }
}
