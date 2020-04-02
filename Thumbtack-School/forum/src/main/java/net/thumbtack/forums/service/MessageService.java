package net.thumbtack.forums.service;

import net.thumbtack.forums.database.dao.iface.ForumDao;
import net.thumbtack.forums.database.dao.iface.MessageDao;
import net.thumbtack.forums.database.dao.iface.MessageInfoDao;
import net.thumbtack.forums.database.dao.iface.SessionDao;
import net.thumbtack.forums.dto.request.message.*;
import net.thumbtack.forums.dto.response.message.*;
import net.thumbtack.forums.error.ServerError;
import net.thumbtack.forums.error.ServerException;
import net.thumbtack.forums.model.*;
import net.thumbtack.forums.model.enums.ForumType;
import net.thumbtack.forums.model.enums.MessageState;
import net.thumbtack.forums.model.enums.OrderValue;
import net.thumbtack.forums.model.view.CommentInfo;
import net.thumbtack.forums.model.view.MessageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional(rollbackFor = DataAccessException.class)
public class MessageService extends ServiceBase {
    private final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);
    private final String UNPUBLISHED_PREFIX = "[UNPUBLISHED] ";

    private MessageDao messageDao;
    private ForumDao forumDao;
    private MessageInfoDao messageInfoDao;

    @Autowired
    public MessageService(MessageDao messageDao, ForumDao forumDao, MessageInfoDao messageInfoDao, SessionDao sessionDao) {
        super(sessionDao);
        this.messageDao = messageDao;
        this.forumDao = forumDao;
        this.messageInfoDao = messageInfoDao;
    }

    public PostMessageResponseDto postMessage(PostMessageDto dto, int forumId, String cookie) throws ServerException {
        User user = getUserByCookieNotBanned(cookie);
        checkIfForumNotReadOnly(forumId);
        Message message = new Message(user, Collections.singletonList(new MessageHistory(dto.getBody())));
        MessageTree messageTree = new MessageTree(forumDao.getById(forumId), dto.getSubject(), dto.getPriority(),
                message, dto.getTags());
        MessageState state = setMessageState(message);

        MessageTree postedTree = messageDao.insert(messageTree);
        Message postedMessage = postedTree.getRootMessage();
        return new PostMessageResponseDto(postedMessage.getId(), state);
    }

    public PostMessageResponseDto postComment(MessageBodyDto dto, long parentId, String cookie) throws ServerException {
        User user = getUserByCookieNotBanned(cookie);
        Message messageFromDB = getMessageFromDBNotInReadonly(parentId);
        MessageTree tree = messageFromDB.getTree();
        checkIfMessageIsPublished(messageFromDB);

        Message message = new Message(user, Collections.singletonList(new MessageHistory(dto.getBody())));
        message.setTree(tree);
        message.setParent(messageFromDB);
        MessageState state = setMessageState(message);
        Message posted = messageDao.insertComment(message);
        return new PostMessageResponseDto(posted.getId(), state);
    }

    public void deleteMessage(long messageId, String cookie) throws ServerException {
        User userFromDb = getUserNotBannedForever(cookie);
        Message messageFromDB = getMessageFromDBNotInReadonly(messageId);
        checkIfUserIsMessageAuthor(messageFromDB, userFromDb);
        checkIfMessageHasComments(messageFromDB);

        if (messageFromDB.getParent() != null) {
            messageDao.deleteMessage(messageFromDB);
        } else {
            messageDao.deleteComment(messageFromDB);
        }
    }

    public EditMessageResponseDto editMessage(long messageId, MessageBodyDto dto, String cookie) throws ServerException {
        User userFromDb = getUserByCookieNotBanned(cookie);
        Message messageFromDB = getMessageFromDBNotInReadonly(messageId);
        checkIfUserIsMessageAuthor(messageFromDB, userFromDb);

        MessageHistory newHistory = new MessageHistory(dto.getBody());
        MessageHistory oldHistory = messageFromDB.getHistory().get(messageFromDB.getHistory().size() - 1);
        if (oldHistory.getState() == MessageState.UNPUBLISHED) {
            newHistory.setId(oldHistory.getId());
            messageFromDB.getHistory().add(newHistory);
            messageDao.editUnpublished(messageFromDB);
            return new EditMessageResponseDto(MessageState.UNPUBLISHED);
        } else {
            messageFromDB.addHistory(newHistory);
            MessageState setState = setMessageState(messageFromDB);
            messageDao.editPublished(messageFromDB);
            return new EditMessageResponseDto(setState);
        }
    }

    public void rateMessage(long messageId, MessageRatingDto dto, String cookie) throws ServerException {
        User user = getUserNotBannedForever(cookie);
        Message messageFromDB = getMessageFromDB(messageId);
        checkIfRatingOwnMessage(messageFromDB, user);
        checkIfMessageIsPublished(messageFromDB);
        if (dto.getRating() == 0) {
            messageDao.deleteRating(user, messageFromDB);
            return;
        }
        Rating newRating = new Rating(user, dto.getRating());
        messageDao.putRating(newRating, messageFromDB);
    }

    public void changePriority(long messageId, ChangePriorityDto dto, String cookie) throws ServerException {
        User userFromDB = getUserByCookieNotBanned(cookie);
        Message messageFromDB = getMessageFromDBNotInReadonly(messageId);
        checkIfUserIsMessageAuthor(messageFromDB, userFromDB);
        messageDao.updatePriority(dto.getPriority(), messageFromDB);
    }

    public CreateTreeResponseDto createTree(long messageId, CreateTreeDto dto, String cookie) throws ServerException {
        User userFromDB = getUserNotBannedForever(cookie);
        Message messageFromDB = getMessageFromDB(messageId);
        MessageTree tree = messageFromDB.getTree();
        checkTreeCreation(messageFromDB, tree.getForum(), userFromDB);

        MessageTree newTree = new MessageTree(tree.getForum(), dto.getSubject(), dto.getPriority(), messageFromDB, dto.getTags());
        return new CreateTreeResponseDto(messageDao.createTree(newTree).getRootMessage().getId());
    }

    public void publishMessage(long messageId, PublishMessageDto dto, String cookie) throws ServerException {
        Message messageFromDB = getMessageFromDB(messageId);
        checkIfMessageAlreadyPublished(messageFromDB);
        User userFromDB = getUserNotBannedForever(cookie);
        MessageTree tree = messageFromDB.getTree();
        checkIfUserIsModerator(tree, userFromDB);

        if(dto.getDecision() == PublishDecision.YES){
            messageDao.makePublic(messageFromDB);
            return;
        }
        sendEmail(messageFromDB.getCreator());
        if (messageFromDB.getHistory().size() > 1) {
            messageDao.deleteUnpublished(messageFromDB);
            return;
        }
        if (messageFromDB.getParent() == null) {
            //Deleting only root message because of DB FK setting "ON DELETE CASCADE"
            messageDao.deleteMessage(messageFromDB);
        } else {
            messageDao.deleteComment(messageFromDB);
        }
    }

    public MessageInfoDto getMessageInfo(long messageId, boolean allVersions, boolean noComments, boolean unpublished, OrderValue order, String cookie) throws ServerException {
        User user = checkSession(cookie).getUser();
        Message message = getMessageFromDB(messageId);
        if (user.getId() != message.getTree().getForum().getCreator().getId() || message.getTree().getForum().getType() == ForumType.UNMODERATED) {
            unpublished = false;
        }
        MessageInfo messageInfo = messageInfoDao.getById(messageId);
        setMessageInfoData(messageInfo, allVersions, unpublished, noComments, order);
        return constructInfoDto(messageInfo);
    }

    public List<MessageInfoDto> getAllMessagesInfo(int forumId, boolean allVersions, boolean noComments, boolean unpublished,
                                                   List<String> tags, OrderValue order, long offset, int limit, String cookie) throws ServerException {
        checkSession(cookie);
        Forum forum = forumDao.getById(forumId);
        if (forum.getType() == ForumType.UNMODERATED) {
            unpublished = false;
        }

        List<MessageInfo> infoList = messageInfoDao.getAllByForumId(forumId, tags, order, offset, limit);
        for (MessageInfo info : infoList) {
            setMessageInfoData(info, allVersions, unpublished, noComments, order);
        }
        List<MessageInfoDto> dtoList = new ArrayList<>();
        for (MessageInfo info : infoList) {
            dtoList.add(constructInfoDto(info));
        }
        return dtoList;
    }

    private void setMessageInfoData(MessageInfo messageInfo, boolean allVersions, boolean unpublished,
                                    boolean noComments, OrderValue order) throws ServerException {
        messageInfo.setHistory(messageInfoDao.getHistory(messageInfo.getId(), allVersions, unpublished));
        if (allVersions && unpublished) {
            if (messageInfo.getHistory().get(0).getState() == MessageState.UNPUBLISHED) {
                MessageHistory unpublishedHistory = messageInfo.getHistory().get(0);
                unpublishedHistory.setBody(UNPUBLISHED_PREFIX + unpublishedHistory.getBody());
            }
        }
        if (!noComments) {
            List<CommentInfo> commentInfoList = messageInfoDao.getCommentInfo(messageInfo.getId(), order);
            setCommentsHistory(commentInfoList, allVersions, unpublished);
            messageInfo.setComments(commentInfoList);
        }
    }

    private List<CommentInfo> setCommentsHistory(List<CommentInfo> comments, boolean allVersions, boolean unpublished) throws ServerException {
        if(comments == null){
            return null;
        }
        for (CommentInfo commentInfo : comments) {
            commentInfo.setHistory(messageInfoDao.getHistory(commentInfo.getId(), allVersions, unpublished));
            if (allVersions && unpublished) {
                if (commentInfo.getHistory().get(0).getState() == MessageState.UNPUBLISHED) {
                    MessageHistory unpublishedHistory = commentInfo.getHistory().get(0);
                    unpublishedHistory.setBody(UNPUBLISHED_PREFIX + unpublishedHistory.getBody());
                }
            }
            commentInfo.setComments(setCommentsHistory(commentInfo.getComments(), allVersions, unpublished));
        }
        return comments;
    }

    private List<String> getBodyFromHistory(List<MessageHistory> historyList) {
        List<String> body = new ArrayList<>();
        for (MessageHistory history : historyList) {
            body.add(history.getBody());
        }
        return body;
    }

    private MessageInfoDto constructInfoDto(MessageInfo messageInfo) {
        List<String> body = getBodyFromHistory(messageInfo.getHistory());
        List<CommentInfoDto> commentDtoList = null;
        if (messageInfo.getComments() != null) {
            commentDtoList = getCommentInfoDtoList(messageInfo.getComments());
        }
        return new MessageInfoDto(messageInfo.getId(), messageInfo.getCreator(), messageInfo.getSubject(), body,
                messageInfo.getPriority(), messageInfo.getTags(), messageInfo.getCreated(), messageInfo.getRating(),
                messageInfo.getRated(), commentDtoList);
    }

    private List<CommentInfoDto> getCommentInfoDtoList(List<CommentInfo> comments) {
        if(comments == null){
            return null;
        }
        List<CommentInfoDto> dtoList = new ArrayList<>();
        for (CommentInfo commentInfo : comments) {
            List<String> commentBody = getBodyFromHistory(commentInfo.getHistory());
            dtoList.add(new CommentInfoDto(commentInfo.getId(), commentInfo.getCreator(), commentBody,
                    commentInfo.getCreated(), commentInfo.getRating(), commentInfo.getRated(), getCommentInfoDtoList(commentInfo.getComments())));
        }
        return dtoList;
    }

    private MessageState setMessageState(Message message) {
        Forum forum = message.getTree().getForum();
        if (forum.getCreator().getId() == message.getCreator().getId() || forum.getType() == ForumType.UNMODERATED) {
            message.getHistory().get(message.getHistory().size() - 1).setState(MessageState.PUBLISHED);
            return MessageState.PUBLISHED;
        } else {
            message.getHistory().get(message.getHistory().size() - 1).setState(MessageState.UNPUBLISHED);
            return MessageState.UNPUBLISHED;
        }
    }

    private Message getMessageFromDB(long id) throws ServerException {
        Message messageFromDB = messageDao.getMessage(id);
        if (messageFromDB == null) {
            ServerError er = ServerError.MESSAGE_WRONG_ID;
            er.setMessage(String.format(er.getMessage(), id));
            throw new ServerException(er);
        }
        return messageFromDB;
    }

    private Message getMessageFromDBNotInReadonly(long id) throws ServerException {
        Message messageFromDB = getMessageFromDB(id);
        if (messageFromDB.getTree().getForum().isReadOnly()){
            throw new ServerException(ServerError.READ_ONLY_FORUM);
        }
        return messageFromDB;
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

    private User getUserNotBannedForever(String cookie) throws ServerException {
        User user = checkSession(cookie).getUser();
        if(user.getTimeBanExit() == null){
            return user;
        }
        LocalDateTime foreverBanTime = LocalDateTime.of(9999, Month.JANUARY, 1, 0, 0, 0);
        if (user.getTimeBanExit().equals(foreverBanTime)) {
            throw new ServerException(ServerError.USER_BANNED_FOREVER);
        }
        return user;
    }

    private void checkIfUserIsMessageAuthor(Message messageFromDB, User userFromDb) throws ServerException {
        if (messageFromDB.getCreator().getId() != userFromDb.getId()) {
            throw new ServerException(ServerError.ANOTHER_USER_MESSAGE);
        }
    }

    private void checkIfRatingOwnMessage(Message message, User user) throws ServerException {
        if (user.getId() == message.getCreator().getId()) {
            throw new ServerException(ServerError.RATE_OWN_MESSAGE);
        }
    }

    private void checkTreeCreation(Message message,Forum forum, User userFromDB) throws ServerException {
        if (message.getParent() == null || forum.getCreator().getId() != userFromDB.getId()) {
            throw new ServerException(ServerError.CANT_CREATE_TREE);
        }
    }

    private void checkIfMessageAlreadyPublished(Message messageFromDB) throws ServerException {
        if (messageFromDB.getHistory().get(messageFromDB.getHistory().size() - 1).getState() == MessageState.PUBLISHED) {
            throw new ServerException(ServerError.MESSAGE_PUBLISHED);
        }
    }

    private void checkIfUserIsModerator(MessageTree tree, User userFromDB) throws ServerException {
        if (userFromDB.getId() != tree.getForum().getCreator().getId()) {
            throw new ServerException(ServerError.USER_NOT_MODERATOR);
        }
    }

    private void checkIfMessageHasComments(Message message) throws ServerException {
        if (message.getComments() == null){
            return;
        }
        if (message.getComments().size() > 0) {
            throw new ServerException(ServerError.MESSAGE_HAS_COMMENTS);
        }
    }

    private void checkIfMessageIsPublished(Message message) throws ServerException {
        if (message == null) {
            throw new ServerException(ServerError.MESSAGE_WRONG_ID);
        }
        if (message.getHistory().get(message.getHistory().size() - 1).getState() == MessageState.UNPUBLISHED) {
            throw new ServerException(ServerError.MESSAGE_UNPUBLISHED);
        }
    }

    private void sendEmail(User user) {
        LOGGER.info("SENDING EMAIL TO " + user.getEmail());
    }

    //For tests
    public Message getMessage(long id) throws ServerException {
        return messageDao.getMessage(id);
    }
    ////////////////////////
}
