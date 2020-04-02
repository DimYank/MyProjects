package net.thumbtack.forums.service;

import net.thumbtack.forums.database.dao.iface.ForumDao;
import net.thumbtack.forums.database.dao.iface.SessionDao;
import net.thumbtack.forums.dto.request.forum.CreateForumDto;
import net.thumbtack.forums.dto.response.forum.CreateForumResponseDto;
import net.thumbtack.forums.dto.response.forum.ForumInfoDto;
import net.thumbtack.forums.error.ServerError;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.Forum;
import net.thumbtack.forums.model.Message;
import net.thumbtack.forums.model.MessageTree;
import net.thumbtack.forums.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = DataAccessException.class)
public class ForumService extends ServiceBase {
    private ForumDao forumDao;

    @Autowired
    public ForumService(ForumDao forumDao, SessionDao sessionDao) {
        super(sessionDao);
        this.forumDao = forumDao;
    }

    public CreateForumResponseDto createForum(CreateForumDto dto, String cookie) throws ServerException {
        User user = getUserByCookieNotBanned(cookie);
        Forum forum = new Forum(dto.getName(), user, dto.getType());
        forum = forumDao.insert(forum);
        return new CreateForumResponseDto(forum.getId(), forum.getName(), forum.getType());
    }

    public List<ForumInfoDto> getAllForums(String cookie) throws ServerException {
        checkSession(cookie);
        List<ForumInfoDto> forumsInfo = new ArrayList<>();
        for (Forum forum : forumDao.getAll()) {
            forumsInfo.add(makeInfo(forum));
        }
        return forumsInfo;
    }

    public ForumInfoDto getForumById(int id, String cookie) throws ServerException {
        checkSession(cookie);
        return makeInfo(forumDao.getById(id));
    }

    public void deleteForum(int id, String cookie) throws ServerException {
        checkSession(cookie);
        checkIfForumNotReadOnly(id);
        forumDao.delete(id);
    }

    private ForumInfoDto makeInfo(Forum forum) {
        int messageCount = forum.getMessages() == null ? 0 : forum.getMessages().size();
        int commentCount = forum.getMessages() == null ? 0 : countForumComments(forum.getMessages());
        return new ForumInfoDto(forum.getId(), forum.getName(), forum.getType(), forum.getCreator().getName(),
                forum.isReadOnly(), messageCount, commentCount);
    }

    private void checkIfForumNotReadOnly(int id) throws ServerException {
        Forum forum = forumDao.getById(id);
        if(forum == null){
            return;
        }
        if(forum.isReadOnly()){
            throw new ServerException(ServerError.READ_ONLY_FORUM);
        }
    }

    private int countForumComments(List<MessageTree> messageTrees){
        int count = 0;
        for(MessageTree tree : messageTrees){
            if(tree != null){
                Message rootMessage = tree.getRootMessage();
                if(rootMessage != null){
                    count += rootMessage.getComments().size();
                    count += countMessagesComments(rootMessage.getComments());
                }
            }
        }
        return count;
    }

    private int countMessagesComments(List<Message> messages) {
        int count = 0;
        for (Message message : messages) {
            if (message.getComments() != null) {
                count += countMessagesComments(message.getComments());
            }
        }
        return count;
    }
}
