import bookshelf from '../database';

const Message = bookshelf.model('Messages', {
  tableName: 'messages',
});

export default Message;
