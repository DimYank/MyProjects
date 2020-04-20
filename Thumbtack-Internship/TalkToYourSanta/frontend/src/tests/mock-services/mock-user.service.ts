import {User} from '../../app/models/user';
import {Injectable} from '@angular/core';


@Injectable(
  {providedIn: 'root'}
)
export class MockUserService {

  public MOCK_USER = {id: 29, name: 'admin', password: '12345'};

  public logIn(user: User, callback: (message: string | void) => void): void {
    if (user.username === this.MOCK_USER.name && user.password === this.MOCK_USER.password) {
      localStorage.setItem('token', '12345');
      callback();
    } else {
      callback('Wrong Username or Password!');
    }
  }

  public getToken(): string {
    return localStorage.getItem('token');
  }

  public getUid(): string {
    return localStorage.getItem('user_uid');
  }
}
