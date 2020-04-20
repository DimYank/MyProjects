import logger from 'morgan';
import express from 'express';
import passport from 'passport';
import jwtStrategy from './passport/strategies/jwt';
import ldapStrategy from './passport/strategies/ldap';
import expressWs from "express-ws";

const app = express();

passport.use(jwtStrategy);
passport.use(ldapStrategy);

app.use(logger('dev'));
app.use(express.json());

app.initializeWebSocket = server => {
  expressWs(app, server);
   import('./routes/api')
     .then(apiRouter => app.use('/', apiRouter.default));
};

export default app;
