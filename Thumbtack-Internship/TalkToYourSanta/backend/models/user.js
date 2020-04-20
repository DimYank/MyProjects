import bookshelf from '../database';

const User = bookshelf.model('User', {
  tableName: 'users',
});

export default User;
