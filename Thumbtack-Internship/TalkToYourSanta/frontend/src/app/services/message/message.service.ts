import {Injectable} from '@angular/core';
import {Observer, SubscriptionLike, timer} from 'rxjs';
import {webSocket} from 'rxjs/webSocket';

import {MESSAGE_BULK_GET, MESSAGE_INIT, MESSAGE_SHOW} from '../../constants/message-event-types';
import {UserService} from '../user/user.service';
import {NavigationEnd, Router} from '@angular/router';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  private callbacks: Observer<MessageEvent>[] = [];
  private reconnection: SubscriptionLike;
  private wsConnection: SubscriptionLike;
  private wsSubject = webSocket<MessageEvent>({
    url: environment.API_MESSAGES_URL,
    serializer: (messageEvent: MessageEvent) => JSON.stringify({
      type: messageEvent.type,
      message: messageEvent.data.message,
      token: messageEvent.data.token
    })
  });

  constructor(
    private userService: UserService,
    private router: Router
  ) {
    router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.disconnect();
      }
    });
  }

  public sendMessage(sendMessageEvent: MessageEvent) {
    const showEvent = new MessageEvent(MESSAGE_SHOW, {data: sendMessageEvent.data});
    this.callbacks.forEach(callback => {
      callback.next(showEvent);
    });
    this.wsSubject.next(sendMessageEvent);
  }

  public connect(callback: Observer<MessageEvent>): void {
    this.wsSubject.subscribe(callback);
    this.callbacks.push(callback);
    this.initializeConnection();
  }

  public disconnect(): void {
    if (this.reconnection) {
      this.reconnection.unsubscribe();
      this.reconnection = null;
    }
    if (this.wsConnection) {
      this.wsConnection.unsubscribe();
      this.wsConnection = null;
      this.wsSubject.complete();
      this.callbacks = [];
    }
  }

  private initializeConnection() {
    if (!this.wsConnection) {
      this.wsConnection = this.wsSubject.subscribe({
        next: (message: MessageEvent) => {
          if (message.type === MESSAGE_BULK_GET) {
            if (this.reconnection) {
              this.reconnection.unsubscribe();
              this.reconnection = null;
            }
          }
        },
        error: () => {
          this.reconnect();
        }
      });
      this.wsSubject.next(new MessageEvent(MESSAGE_INIT, {data: {token: this.userService.getToken()}}));
    }
  }

  private reconnect() {
    this.wsConnection = null;
    this.reconnection = timer(2000)
      .subscribe(() => {
        this.initializeConnection();
        this.callbacks.forEach(callback => {
          this.wsSubject.subscribe(callback);
        });
      });
  }
}
