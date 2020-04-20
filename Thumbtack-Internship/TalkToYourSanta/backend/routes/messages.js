import * as jwt from 'jsonwebtoken';
import * as express from 'express';
import WsClients from '../websocket/wsClients';
import Message from '../models/message';
import {
  MESSAGE_INIT,
  MESSAGE_BULK_GET,
  MESSAGE_CONFIRM,
  MESSAGE_ERROR,
  MESSAGE_SEND,
  MESSAGE_GET,
} from '../constants/messagesEventTypes';
import User from '../models/user';

const messagesRouter = express.Router();

const wsClients = new WsClients();

const makeWsResponse = (responseType, data) => JSON.stringify(
  { type: responseType, data },
);

const getAllMessagesRelatedToUser = userId => Message.query()
  .select('authorId', 'userRole', 'text', 'hashString', 'timestamp')
  .leftJoin('users', 'messages.authorId', 'users.id')
  .where('authorId', '=', userId)
  .orWhere({
    gifteeId: userId,
    userRole: 'santa',
  })
  .orWhere({
    'users.id': User.query().select('gifteeId').where('id', '=', userId),
    userRole: 'giftee',
  });

const initializeWsConnection = (client, token) => {
  let payload;
  try {
    payload = jwt.verify(token, process.env.JWT_SECRET);
  } catch (err) {
    client.close();
    return;
  }
  wsClients.saveClient(payload.sub, client);
  client.id = payload.sub;
  getAllMessagesRelatedToUser(client.id).then(
    retrievedMessages => client.send(makeWsResponse(MESSAGE_BULK_GET, retrievedMessages)),
  );
};

const saveNewMessage = newMessage => Message.forge(newMessage)
  .save(null, { method: 'insert' });

const getMessageReceiverData = savedMessage => {
  if (savedMessage.userRole === 'santa') {
    return User.query()
      .select('gifteeId as id')
      .where('id', '=', savedMessage.authorId)
      .first();
  }
  return User.query()
    .select('id')
    .where('gifteeId', '=', savedMessage.authorId)
    .first();
};

const handleMessage = (client, wsData) => {
  let wsMessage;
  try {
    wsMessage = JSON.parse(wsData);
  } catch (err) {
    client.send(makeWsResponse(MESSAGE_ERROR, err.toString()));
    return;
  }
  switch (wsMessage.type) {
    case MESSAGE_INIT:
      initializeWsConnection(client, wsMessage.token);
      break;
    case MESSAGE_SEND:
      if (!client.id) {
        client.close();
        break;
      }
      saveNewMessage(wsMessage.message)
        .then(savedMessage => {
          client.send(makeWsResponse(
            MESSAGE_CONFIRM,
            { hashString: savedMessage.attributes.hashString, delivered: true },
          ));
          savedMessage.attributes.delivered = true;
          getMessageReceiverData(savedMessage.attributes)
            .then(messageReceiverData => {
              if (messageReceiverData && messageReceiverData.id) {
                wsClients.get(messageReceiverData.id).send(
                  makeWsResponse(MESSAGE_GET, { message: savedMessage.attributes }),
                );
              }
            });
        });
      break;
    default:
      client.close();
  }
};

messagesRouter.ws('/', client => {
  client.on('message', wsData => handleMessage(client, wsData));
  client.on('close', () => wsClients.deleteClient(client.id));
});

export default messagesRouter;
