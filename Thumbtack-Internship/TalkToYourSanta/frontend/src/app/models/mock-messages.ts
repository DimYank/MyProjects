import {Message} from './message';
import {Md5} from 'ts-md5';
import {ROLE_GIFTEE, ROLE_SANTA} from '../constants/user-roles';

const date: Date = new Date(2020, 0O1, 0O1, 1, 1, 1);

export const MESSAGES: Message[] = [
  {
    id: 1,
    authorId: '12345',
    text: 'user as giftee. writes a looong message. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod' +
      'tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris\n' +
      ' nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore\n' +
      ' eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit\n' +
      ' anim id est laborum.',
    userRole: ROLE_GIFTEE,
    timestamp: date,
    hashString: Md5.hashStr('user as giftee') as string,
    delivered: true
  },
  {
    id: 1,
    authorId: '54321',
    text: 'stranger as santa', userRole: ROLE_SANTA,
    timestamp: date,
    hashString: Md5.hashStr('stranger as santa') as string,
    delivered: true
  },
  {
    id: 1,
    authorId: '54321',
    text: 'stranger as giftee',
    userRole: ROLE_GIFTEE,
    timestamp: date,
    hashString: Md5.hashStr('stranger as giftee') as string,
    delivered: true
  },
  {
    id: 1,
    authorId: '12345',
    text: 'user as santa',
    userRole: ROLE_SANTA,
    timestamp: date,
    hashString: Md5.hashStr('user as santa') as string,
    delivered: true
  },
];
