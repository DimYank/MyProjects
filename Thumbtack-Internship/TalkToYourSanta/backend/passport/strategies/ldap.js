import LdapStrategy from 'passport-ldapauth';
import fs from 'fs';

const opts = {
  server: {
    url: process.env.LDAP_URL,
    searchBase: process.env.LDAP_SEARCH_BASE,
    searchFilter: process.env.LDAP_SEARCH_FILTER,
  },
};
if (process.env.LDAP_CA_PATH) {
  opts.server.tlsOptions = {
    ca: [fs.readFileSync(process.env.LDAP_CA_PATH)],
  };
}

const ldapStrategy = new LdapStrategy(opts);

export default ldapStrategy;
