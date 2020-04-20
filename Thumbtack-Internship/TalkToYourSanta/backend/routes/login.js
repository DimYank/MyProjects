import * as jwt from 'jsonwebtoken';
import * as express from 'express';
import ldapAuth from '../passport/middleware/ldap-authentication';
import User from '../models/user';

const loginRouter = express.Router();

const generateToken = subject => jwt.sign({}, process.env.JWT_SECRET, { subject });

const makeLoginResponse = userAttributes => ({
  token: generateToken(userAttributes.id.toString()),
  status: userAttributes.status,
});

const userStatus = userName => {
  const admins = process.env.ADMINISTRATORS.split(',');
  return admins.indexOf(userName) > -1 ? 'admin' : 'regular';
};

loginRouter.post('/', ldapAuth, (req, res) => {
  User.forge({ username: req.user.uid }).fetch({ require: false }).then(
    modelFromDB => modelFromDB
      || User.forge({
        username: req.user.uid,
        firstName: req.user.givenName,
        lastName: req.user.sn,
        status: userStatus(req.user.uid),
      })
        .save(null, { method: 'insert' }),
  )
    .then(newOrRetrievedModel => {
      res.send(makeLoginResponse(newOrRetrievedModel.attributes));
    });
});

export default loginRouter;
