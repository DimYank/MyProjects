import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {MessagesComponent} from '../../../app/components/messages/messages.component';
import {UserService} from '../../../app/services/user/login.service';
import {MockUserService} from '../../mock-services/mock-user.service';
import {MessageService} from '../../../app/services/message/message.service';
import {MockMessageService} from '../../mock-services/mock.message.service';

describe('MessagesComponent', () => {
  let component: MessagesComponent;
  let fixture: ComponentFixture<MessagesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        MessagesComponent
      ],
      providers: [
        {provide: UserService, useClass: MockUserService},
        {provide: MessageService, useClass: MockMessageService}
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MessagesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
