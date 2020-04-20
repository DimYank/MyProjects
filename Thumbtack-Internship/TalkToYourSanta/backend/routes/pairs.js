import * as express from 'express';
import jwtAuth from '../passport/middleware/jwt-authentication';
import User from '../models/user';
import Message from '../models/message';

const pairsRouter = express.Router();

const checkIfUserAdmin = (req, res, done) => {
  User.query()
    .select('status')
    .where('id', '=', req.user.id)
    .first()
    .then(retrievedUser => {
      if (retrievedUser.status !== 'admin') {
        res.status(403).send();
        return;
      }
      done();
    });
};

const getPairs = () => User.query()
  .select('a.username as santaName', ' b.username as gifteeName')
  .from('users as a')
  .leftJoin('users as b', 'a.gifteeId', 'b.id');

const getGifteeByName = gifteeName => User.forge({ userName: gifteeName })
  .fetch({ require: false });

const updatePair = (santaName, gifteeId) => {
  const santaModel = User.forge().where('userName', '=', santaName);
  if (gifteeId) {
    return santaModel.save({ gifteeId }, { method: 'update' });
  }
  return santaModel.save({ gifteeId: null }, { method: 'update' });
};

const deleteMessagesForOldPair = userName => Message.query()
  .where({
    authorId: User.query().select('id').where({ userName }),
    userRole: 'santa',
  })
  .orWhere({
    authorId: User.query().select('gifteeId').where({ userName }),
    userRole: 'giftee',
  })
  .delete();

const getUnassignedSantas = () => User.query()
  .select('userName')
  .where({ gifteeId: null });

const getUnassignedGiftees = () => User.query()
  .select('users.userName')
  .leftJoin('users as extra', 'users.id', 'extra.gifteeId')
  .whereNull('extra.id');

const shuffleArray = arrayToShuffle => {
  const array = [...arrayToShuffle];
  let currentIndex = array.length;
  let temporaryValue;
  let randomIndex;

  while (currentIndex !== 0) {
    randomIndex = Math.floor(Math.random() * currentIndex);
    currentIndex -= 1;

    temporaryValue = array[currentIndex];
    array[currentIndex] = array[randomIndex];
    array[randomIndex] = temporaryValue;
  }

  return array;
};

const makeRandomPairs = (santas, giftees) => {
  const randomPairs = [];
  giftees = shuffleArray(giftees);

  while (santas.length) {
    const santa = santas.pop().userName;
    const giftee = giftees[0].userName === santa
      ? giftees.pop().userName
      : giftees.shift().userName;
    if (santa === giftee) {
      if (randomPairs.length) {
        const previousPair = randomPairs.pop();
        const newPair = { santa: previousPair.santa, giftee };
        previousPair.santa = santa;
        randomPairs.push(previousPair);
        randomPairs.push(newPair);
      } else {
        randomPairs.push({ santa, giftee: null });
      }
      break;
    }
    randomPairs.push({ santa, giftee });
  }
  return randomPairs;
};

const makePairsMapFromArray = pairsArray => {
  const pairsMap = {};
  pairsArray.forEach(pair => {
    pairsMap[pair.santaName] = pair.gifteeName;
  });
  return pairsMap;
};

pairsRouter.get('/', jwtAuth, checkIfUserAdmin, (req, res) => {
  getPairs().then(retrievedPairs => {
    res.send(makePairsMapFromArray(retrievedPairs));
  });
});

pairsRouter.put('/', jwtAuth, checkIfUserAdmin, (req, res) => {
  const keys = Object.keys(req.body);
  if (!keys.length || keys.length > 1 || keys[0] === req.body[keys[0]]) {
    res.status(422).send();
    return;
  }
  if (!req.body[keys[0]]) {
    deleteMessagesForOldPair(keys[0])
      .then(() => {
        updatePair(keys[0], null)
          .then(() => res.send())
          .catch(() => res.status(422).send());
      })
      .catch(() => res.status(422).send());
    return;
  }
  getGifteeByName(req.body[keys[0]])
    .then(retrievedGiftee => {
      if (!retrievedGiftee) {
        res.status(422).send();
        return;
      }
      updatePair(keys[0], retrievedGiftee.id)
        .then(() => res.send())
        .catch(() => res.status(422).send());
    });
});

pairsRouter.post('/', jwtAuth, checkIfUserAdmin, ((req, res) => {
  let freeSantas;
  getUnassignedSantas()
    .then(unassignedSantas => {
      freeSantas = unassignedSantas;
      return getUnassignedGiftees();
    })
    .then(freeGiftees => {
      const updateQueries = [];
      makeRandomPairs(freeSantas, freeGiftees).forEach(pair => {
        const query = User.query()
          .update({ gifteeId: User.query().select('id').where({ userName: pair.giftee }) })
          .where({ userName: pair.santa });
        updateQueries.push(query);
      });
      return Promise.all(updateQueries);
    })
    .then(() => getPairs())
    .then(retrievedPairs => {
      res.send(makePairsMapFromArray(retrievedPairs));
    });
}));

pairsRouter.delete('/', jwtAuth, checkIfUserAdmin, ((req, res) => {
  Message.query().delete()
    .then(() => User.query().update('gifteeId', null))
    .then(() => getPairs())
    .then(retrievedPairs => {
      res.send(makePairsMapFromArray(retrievedPairs));
    });
}));
export default pairsRouter;
