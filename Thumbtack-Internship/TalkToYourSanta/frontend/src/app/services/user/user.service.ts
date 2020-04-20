import {Injectable} from '@angular/core';

import {User} from 'src/app/models/user';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {LoginResponse} from '../../models/login-response';
import {CurrentUser} from '../../models/current-user';
import {UserDataResponse} from '../../models/user-data-response';


@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(
    private http: HttpClient
  ) { }

  public logIn(user: User, callback: (message: string | void) => void): void {
    this.http.post<LoginResponse>(environment.API_LOGIN_URL, JSON.stringify(user), {headers: {'Content-Type': 'application/json'}})
    .subscribe(
      response => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('status', response.status);
        callback();
      },
      (error: HttpErrorResponse) => {
        switch (error.status) {
          case 500:
            callback('Server Error!');
            break;
          case 401:
            callback('Wrong Username or Password!');
            break;
          default:
            callback('Can\'t connect to server!');
        }
      }
    );
  }

  public getToken(): string {
    return localStorage.getItem('token');
  }

  public getUserStatus(): string {
    return localStorage.getItem('status');
  }

  public logOut(): void {
    localStorage.clear();
  }

  public getUserData(errorCallback: () => void): void {
    this.http.get<UserDataResponse>(environment.API_USER_DATA_URL)
       .subscribe(
         response => {
           Object.assign(CurrentUser, response);
        },
       () => errorCallback()
      );
  }
}
