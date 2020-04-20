import {
  describe, it, before, after, beforeEach, afterEach,
} from 'mocha';
import * as jwt from 'jsonwebtoken';
import { expect } from 'chai';
import * as http from 'http';
import WebSocket from 'ws';
import app from '../app';
import {
  MESSAGE_BULK_GET, MESSAGE_CONFIRM, MESSAGE_GET, MESSAGE_INIT, MESSAGE_INIT_CONFIRM, MESSAGE_SEND,
} from '../constants/messagesEventTypes';
import User from '../models/user';
import bookshelf from '../database';
import Message from '../models/message';


const getNewWsConnection = () => new WebSocket(
  'ws://localhost:3002/api/messages',
  null,
  { perMessageDeflate: false },
);

const sendInitMessage = wsClient => {
  wsClient.send(JSON.stringify({
    type: MESSAGE_INIT,
    token: jwt.sign({}, process.env.JWT_SECRET, { subject: '12' }),
  }));
};

const sendMessage = (wsClient, message) => {
  wsClient.send(JSON.stringify({
    type: MESSAGE_SEND,
    message,
  }));
};

describe('Messages Endpoint', () => {
  let server;
  let wsClient;

  before(() => {
    app.set('port', process.env.APP_PORT);
    server = http.createServer(app);
    server.listen(process.env.APP_PORT);
    app.initializeWebSocket(server);
    return bookshelf.knex.raw('DELETE FROM users')
      .then(
        () => User.forge({
          id: '12', firstName: 'test', lastName: 'test', username: 'test', status: 'regular',
        })
          .save(null, { method: 'insert' }),
      );
  });

  beforeEach(() => bookshelf.knex.raw('DELETE FROM messages'));

  afterEach(() => {
    if (wsClient) {
      wsClient.close();
    }
  });

  after(() => {
    if (server) {
      server.close();
    }
  });

  it('should authorize connection', done => {
    wsClient = getNewWsConnection();
    wsClient.on('open', () => {
      sendInitMessage(wsClient);
    });
    wsClient.on('message', message => {
      const wsMessageBody = JSON.parse(message);
      expect(wsMessageBody.type).to.equal(MESSAGE_BULK_GET);
      done();
    });
  });

  describe('retrieving messages', () => {
    const amountOfMessages = 4;
    const newMessage = {
      authorId: 12,
      userRole: 'santa',
      timestamp: '24123',
    };

    beforeEach(done => {
      const queries = [];
      for (let i = 0; i < amountOfMessages; i++) {
        newMessage.text = i;
        newMessage.hashString = i;
        queries.push(Message.forge({ ...newMessage }).save());
      }
      Promise.all(queries)
        .then(() => done());
    });

    it('should retrieve message bulk', done => {
      wsClient = getNewWsConnection();
      wsClient.on('open', () => {
        sendInitMessage(wsClient);
      });
      wsClient.on('message', message => {
        const wsMessageBody = JSON.parse(message);
        expect(wsMessageBody.data).to.be.an('array').that.have.lengthOf(amountOfMessages);
        expect(wsMessageBody.data[0].userRole).to.equal(newMessage.userRole);
        done();
      });
    });
  });

  describe('receiving message', () => {
    let wsMessageBody;

    beforeEach(done => {
      wsClient = getNewWsConnection();
      wsClient.on('open', () => {
        sendInitMessage(wsClient);
      });
      wsClient.on('message', message => {
        wsMessageBody = JSON.parse(message);
        if (wsMessageBody.type === MESSAGE_BULK_GET) {
          done();
        }
      });
    });

    it('should send response', done => {
      const newMessage = {
        authorId: 12,
        userRole: 'santa',
        text: 'test',
        hashString: 'sadq241fa',
        timestamp: '24123',
      };
      sendMessage(wsClient, newMessage);

      wsClient.on('message', message => {
        wsMessageBody = JSON.parse(message);
        if (wsMessageBody.type === MESSAGE_CONFIRM) {
          expect(wsMessageBody.data.delivered).to.be.true;
          expect(wsMessageBody.data.hashString).to.equal(newMessage.hashString);
          done();
        }
      });
    });

    describe('sending message to receiver', () => {
      let anotherClient;

      beforeEach(done => {
        User.forge({
          id: '13', firstName: 'test', lastName: 'test', username: 'anotherUser', status: 'regular', gifteeId: '12',
        })
          .save(null, { method: 'insert' })
          .then(() => done());
      });

      beforeEach(done => {
        anotherClient = getNewWsConnection();
        anotherClient.on('open', () => {
          anotherClient.send(JSON.stringify({
            type: MESSAGE_INIT,
            token: jwt.sign({}, process.env.JWT_SECRET, { subject: '13' }),
          }));
        });
        anotherClient.on('message', message => {
          wsMessageBody = JSON.parse(message);
          if (wsMessageBody.type === MESSAGE_BULK_GET) {
            done();
          }
        });
      });

      it('should send message to another user', done => {
        const newMessage = {
          authorId: 13,
          userRole: 'santa',
          text: 'test',
          hashString: '13',
          timestamp: '24123',
        };
        wsClient.on('message', message => {
          wsMessageBody = JSON.parse(message);
          if (wsMessageBody.type === MESSAGE_GET) {
            expect(wsMessageBody.data.message.authorId).to.equal(newMessage.authorId);
            done();
          }
        });
        sendMessage(anotherClient, newMessage);
      });
    });
  });
});
