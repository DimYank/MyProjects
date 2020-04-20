import {TestBed} from '@angular/core/testing';

import {UserService} from '../../../app/services/user/login.service';
import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {AuthInterceptor} from '../../../app/services/user/auth-interceptor';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {environment} from '../../../environments/environment';

describe('LoginService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        {
          provide: HTTP_INTERCEPTORS,
          useClass: AuthInterceptor,
          multi: true
        }
      ]
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should add token', () => {
    service.logIn({username: 'admin', password: '12345'}, () => {});
    const httpReq = httpMock.expectOne(environment.API_LOGIN_URL);
    httpReq.flush({token: '12345'}, {status: 200, statusText: 'OK'});
    expect(localStorage.getItem('token')).toBe('12345');
  });

  it('should delete token', () => {
    localStorage.setItem('token', '12345');
    service.logOut();
    expect(localStorage.getItem('token')).toBeFalsy();
  });
});
