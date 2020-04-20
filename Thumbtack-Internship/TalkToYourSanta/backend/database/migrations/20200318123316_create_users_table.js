const up = knex => knex.schema.createTable('users', table => {
  table.increments('id').unsigned().primary();
  table.string('firstName').notNullable();
  table.string('lastName').notNullable();
  table.string('username').notNullable().unique();
  table.string('status').notNullable();
  table.integer('gifteeId', 10).unsigned().nullable();
  table.timestamps(true, true);
  table.foreign('gifteeId')
    .onDelete('SET NULL')
    .onUpdate('CASCADE')
    .references('users.id');
});

const down = knex => knex.schema.dropTable('users');

export { up, down };
