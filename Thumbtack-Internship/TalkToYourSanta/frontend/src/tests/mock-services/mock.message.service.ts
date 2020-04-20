import {Injectable} from '@angular/core';
import {Observer} from 'rxjs';
import {Md5} from 'ts-md5';
import {MESSAGE_BULK_GET, MESSAGE_CONFIRM, MESSAGE_SHOW} from '../../app/constants/message-event-types';
import {MESSAGES} from '../../app/models/mock-messages';

@Injectable({
  providedIn: 'root'
})
export class MockMessageService {

  private callbacks: Observer<MessageEvent>[] = [];

  constructor() {
  }

  public sendMessage(messageEvent: MessageEvent) {
    const message = messageEvent.data;
    message.hashString = Md5.hashStr(message.text + new Date().getTime()) as string;
    const showEvent = new MessageEvent(MESSAGE_SHOW, {data: message});
    this.callbacks.forEach(callback => {
      callback.next(showEvent);
    });
    setTimeout(() => this.callbacks.forEach(callback => {
      message.id = 23;
      message.delivered = true;
      const confirmationEvent = new MessageEvent(MESSAGE_CONFIRM, {data: message});
      callback.next(confirmationEvent);
    }), 2000);
  }

  public connect(callback: Observer<MessageEvent>): void {
    this.callbacks.push(callback);
    callback.next(new MessageEvent(MESSAGE_BULK_GET, {data: [...MESSAGES]}));
  }
}
