const up = knex => knex.schema.createTable('messages', table => {
  table.increments('id').unsigned().primary();
  table.integer('authorId', 10).unsigned().notNullable();
  table.string('userRole').notNullable();
  table.string('text').notNullable();
  table.string('hashString').notNullable().unique();
  table.string('timeStamp').notNullable();
  table.timestamps(true, true);
  table.foreign('authorId')
    .onDelete('CASCADE')
    .onUpdate('CASCADE')
    .references('users.id');
});

const down = knex => knex.schema.dropTable('messages');

export { up, down };
