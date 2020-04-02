package net.thumbtack.forums.service;

import net.thumbtack.forums.database.dao.iface.SessionDao;
import net.thumbtack.forums.database.dao.iface.StatisticsDao;
import net.thumbtack.forums.dto.response.information.MessageCountDto;
import net.thumbtack.forums.dto.response.information.MessagesRatingDto;
import net.thumbtack.forums.dto.response.information.ServerSettingsDto;
import net.thumbtack.forums.dto.response.information.UserRatingDto;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.User;
import net.thumbtack.forums.model.enums.UserType;
import net.thumbtack.forums.model.view.MessageRating;
import net.thumbtack.forums.model.view.UserRating;
import net.thumbtack.forums.util.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsService extends ServiceBase {

    private Settings settings;
    private StatisticsDao statisticsDao;

    @Autowired
    public StatisticsService(Settings settings, StatisticsDao statisticsDao, SessionDao sessionDao) {
        super(sessionDao);
        this.settings = settings;
        this.statisticsDao = statisticsDao;
    }

    public ServerSettingsDto getServerSettings(String cookie) throws ServerException {
        if (cookie != null) {
            User user = checkSession(cookie).getUser();

            if (user.getUserType() == UserType.SUPER) {
                return new ServerSettingsDto(settings.getMaxBanCount(), settings.getBanTime(),
                        settings.getMaxNameLength(), settings.getMinPasswordLength());
            }
        }
        return new ServerSettingsDto(null, null, settings.getMaxNameLength(), settings.getMinPasswordLength());
    }

    public MessageCountDto getMessageAndCommentCount(int forumId, String cookie) throws ServerException {
        checkSession(cookie);
        long messageCount = statisticsDao.getMessagesCount(forumId);
        long commentCount = statisticsDao.getCommentsCount(forumId);
        return new MessageCountDto(messageCount, commentCount);
    }

    public List<MessagesRatingDto> getRating(int forumId, boolean forMessages, long offSet, int limit, String cookie) throws ServerException {
        checkSession(cookie);
        List<MessageRating> messageRating = statisticsDao.getRating(forumId, forMessages, offSet, limit);
        List<MessagesRatingDto> dtoList = new ArrayList<>();
        for (MessageRating rating : messageRating) {
            dtoList.add(new MessagesRatingDto(rating.getId(), rating.getRating()));
        }
        return dtoList;
    }

    public List<UserRatingDto> getUsersRating(int forumId, long offSet, int limit, String cookie) throws ServerException {
        checkSession(cookie);
        List<UserRating> userRating = statisticsDao.getUsersRating(forumId, offSet, limit);
        List<UserRatingDto> dtoList = new ArrayList<>();
        for (UserRating rating : userRating) {
            dtoList.add(new UserRatingDto(rating.getId(), rating.getName(), rating.getRating()));
        }
        return dtoList;
    }
}
