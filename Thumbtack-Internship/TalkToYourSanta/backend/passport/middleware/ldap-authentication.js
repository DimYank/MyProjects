import passport from 'passport';

const ldapAuth = passport.authenticate('ldapauth', { session: false });

export default ldapAuth;
