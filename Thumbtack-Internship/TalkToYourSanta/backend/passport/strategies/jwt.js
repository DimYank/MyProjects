import JwtStrategy from 'passport-jwt/lib/strategy';
import ExtractJwt from 'passport-jwt/lib/extract_jwt';

const opts = {};
opts.jwtFromRequest = ExtractJwt.fromAuthHeaderAsBearerToken();
opts.secretOrKey = process.env.JWT_SECRET;
opts.ignoreExpiration = true;

const jwtStrategy = new JwtStrategy(opts, (jwtPayload, done) => {
  done(null, { id: jwtPayload.sub });
});

export default jwtStrategy;
