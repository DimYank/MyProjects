import knex from 'knex';
import bs from 'bookshelf';

const knexInst = knex({
  client: process.env.DB_CLIENT,
  connection: {
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_DATABASE,
    charset: process.env.DB_CHARSET,
  },
  migrations: {
    tableName: 'knex_migrations',
  },
});

const bookshelf = bs(knexInst);

export default bookshelf;
