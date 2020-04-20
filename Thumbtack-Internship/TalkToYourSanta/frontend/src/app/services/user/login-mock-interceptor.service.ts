import {Injectable} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {timeout} from 'rxjs/operators';
import {environment} from '../../../environments/environment';
import {STATUS_ADMIN} from '../../constants/user-status';

@Injectable({
  providedIn: 'root'
})
export class LoginMockInterceptor implements HttpInterceptor {

  private MOCK_USER = {username: 'admin', password: '12345'};
  private MOCK_TOKEN = '12345';

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (req.url === environment.API_LOGIN_URL && req.method === 'POST') {
      const user = JSON.parse(req.body);
      if (user.password === this.MOCK_USER.password && user.username === this.MOCK_USER.username) {
        return of(new HttpResponse({status: 200, body: {token: this.MOCK_TOKEN, userUid: '12345', userStatus: STATUS_ADMIN}}));
      } else {
        throw new HttpErrorResponse({status: 401, error: 'Wrong Username or Password!'});
      }
    }
  }
}
