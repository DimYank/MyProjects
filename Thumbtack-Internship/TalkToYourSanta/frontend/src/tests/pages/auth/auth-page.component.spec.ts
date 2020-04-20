import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';

import {AuthPageComponent} from '../../../app/pages/auth-page/auth-page.component';
import {AppRoutingModule} from '../../../app/app-routing.module';
import {UserService} from '../../../app/services/user/user.service';
import {MockUserService} from '../../mock-services/mock-user.service';
import {Routes} from '@angular/router';
import {ChatPageComponent} from '../../../app/pages/chat-page/chat-page.component';
import {RouterTestingModule} from '@angular/router/testing';
import {Location} from '@angular/common';
import {DebugElement} from '@angular/core';
import {By} from '@angular/platform-browser';
import {FormsModule} from '@angular/forms';

describe('AuthPageComponent', () => {
  let component: AuthPageComponent;
  let fixture: ComponentFixture<AuthPageComponent>;
  let userService;
  let location: Location;
  let debugElem: DebugElement;
  let loginInput: DebugElement;
  let passwordInput: DebugElement;
  let logInButton: DebugElement;

  const routes: Routes = [
    {path: 'chat', component: ChatPageComponent},
    {path: 'login', component: AuthPageComponent}
  ];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        AppRoutingModule,
        FormsModule,
        RouterTestingModule.withRoutes(routes)
      ],
      declarations: [
        AuthPageComponent
      ],
      providers: [
        {provide: UserService, useClass: MockUserService}
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    userService = TestBed.inject(UserService);
    location = TestBed.inject(Location);
    fixture = TestBed.createComponent(AuthPageComponent);
    debugElem = fixture.debugElement;
    component = fixture.componentInstance;
    loginInput = debugElem.query(By.css('.login-username'));
    passwordInput = debugElem.query(By.css('.login-password'));
    logInButton = debugElem.query(By.css('button'));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to chat page', fakeAsync(() => {
    loginInput.nativeElement.value = userService.MOCK_USER.name;
    passwordInput.nativeElement.value = userService.MOCK_USER.password;
    loginInput.nativeElement.dispatchEvent(new Event('input'));
    passwordInput.nativeElement.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    logInButton.nativeElement.click();
    tick();
    expect(location.path()).toBe('/chat');
  }));

  it('should show warning', fakeAsync(() => {
    loginInput.nativeElement.value = 'a';
    passwordInput.nativeElement.value = 'b';
    loginInput.nativeElement.dispatchEvent(new Event('input'));
    passwordInput.nativeElement.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    logInButton.nativeElement.click();
    fixture.detectChanges();
    const warning = debugElem.query(By.css('.login-warning'));
    expect(warning.nativeElement.textContent).toBe('Wrong Username or Password!');
  }));
});
