import {
  describe, it, before, after, beforeEach,
} from 'mocha';
import chai, { expect } from 'chai';
import chaiHttp from 'chai-http';
import * as http from 'http';
import * as jwt from 'jsonwebtoken';

import User from '../models/user';
import app from '../app';

chai.use(chaiHttp);

const testUser = {
  firstName: 'test',
  lastName: 'test',
  userName: 'user1',
  status: 'regular',
};

describe('Pairs Endpoint', () => {
  let server;
  let token;
  before(() => {
    app.set('port', process.env.APP_PORT);
    server = http.createServer(app);
    server.listen(process.env.APP_PORT);
  });

  beforeEach(done => {
    User.query()
      .del()
      .then(() => User.forge(testUser).save(null, { method: 'insert' }))
      .then(savedUser => {
        token = jwt.sign(
          {},
          process.env.JWT_SECRET,
          { subject: savedUser.attributes.id.toString() },
        );
        done();
      });
  });

  after(() => {
    server.close();
  });

  it('should return no pairs for user', done => {
    chai.request(server)
      .get('/api/pairs')
      .set('Authorization', `Bearer ${token}`)
      .end((err, res) => {
        expect(res.status).to.equal(200);
        expect(Object.keys(res.body).length).to.equal(1);
        const bodyKey = Object.keys(res.body)[0];
        expect(bodyKey).to.equal(testUser.userName);
        expect(res.body[bodyKey]).to.be.null;
        done();
      });
  });

  describe('processing 2 users pairs', () => {
    const testUser2 = { ...testUser };
    beforeEach(done => {
      testUser2.userName = 'user2';
      User.forge(testUser2)
        .save(null, { method: 'insert' })
        .then(savedUser => {
          User.where({ userName: testUser.userName })
            .save({ gifteeId: savedUser.attributes.id }, { method: 'update' });
        })
        .then(() => done());
    });

    it('should return giftee name', done => {
      chai.request(server)
        .get('/api/pairs')
        .set('Authorization', `Bearer ${token}`)
        .end((err, res) => {
          expect(res.status).to.equal(200);
          expect(Object.keys(res.body).length).to.equal(2);
          const bodyKey = Object.keys(res.body)[0];
          expect(bodyKey).to.equal(testUser.userName);
          expect(res.body[bodyKey]).to.equal(testUser2.userName);
          done();
        });
    });

    it('should assign pair', done => {
      chai.request(server)
        .put('/api/pairs')
        .set('Authorization', `Bearer ${token}`)
        .send({ [testUser2.userName]: testUser.userName })
        .end((err, res) => {
          expect(res.status).to.equal(200);
          User.where({ userName: testUser2.userName })
            .fetch()
            .then(userFromDB => {
              expect(userFromDB.attributes.gifteeId).to.not.be.null;
              done();
            });
        });
    });

    it('should unassign pair', done => {
      chai.request(server)
        .put('/api/pairs')
        .set('Authorization', `Bearer ${token}`)
        .send({ [testUser2.userName]: null })
        .end((err, res) => {
          expect(res.status).to.equal(200);
          User.where({ userName: testUser2.userName })
            .fetch()
            .then(userFromDB => {
              expect(userFromDB.attributes.gifteeId).to.be.null;
              done();
            });
        });
    });

    it('should not assign pair', done => {
      chai.request(server)
        .put('/api/pairs')
        .set('Authorization', `Bearer ${token}`)
        .send({ test: null })
        .end((err, res) => {
          expect(res.status).to.equal(422);
          done();
        });
    });
  });
});
