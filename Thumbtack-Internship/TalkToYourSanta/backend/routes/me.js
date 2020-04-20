import * as express from 'express';
import User from '../models/user';
import jwtAuth from '../passport/middleware/jwt-authentication';

const meRouter = express.Router();

const getSantaForUser = userId => User.query()
  .count('id as isPresent')
  .where({ gifteeId: userId })
  .first();

const getGifteeNameForUser = gifteeId => User.query()
  .select('firstName', 'lastName')
  .where({ id: gifteeId })
  .first();

const makeResponseBodyFromModelAttributes = model => ({
  id: model.id,
  firstName: model.firstName,
});

meRouter.get('/', jwtAuth, (req, res) => {
  let responseBody;
  let userModel;
  User.forge({ id: req.user.id }).fetch({ require: false })
    .then(modelFromDB => {
      userModel = modelFromDB.attributes;
      responseBody = makeResponseBodyFromModelAttributes(userModel);
      return getSantaForUser(userModel.id);
    })
    .then(santaData => {
      responseBody.santaPresent = santaData.isPresent;
      return getGifteeNameForUser(userModel.gifteeId);
    })
    .then(giftee => {
      if (giftee) {
        responseBody.gifteeName = `${giftee.firstName} ${giftee.lastName}`;
      } else {
        responseBody.gifteeName = null;
      }
      res.send(responseBody);
    });
});

export default meRouter;
