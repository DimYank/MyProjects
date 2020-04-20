import {
  AfterViewChecked,
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';

import {Message} from '../../models/message';
import {MessageService} from 'src/app/services/message/message.service';
import {MESSAGE_BULK_GET, MESSAGE_CONFIRM, MESSAGE_GET, MESSAGE_SHOW} from '../../constants/message-event-types';
import {ROLE_GIFTEE, ROLE_SANTA} from '../../constants/user-roles';
import {CurrentUser} from '../../models/current-user';
import {CONNECTED, DISCONNECTED} from '../../constants/connection-status';

@Component({
  selector: 'app-messages',
  templateUrl: './messages.component.html',
  styleUrls: ['./messages.component.sass']
})
export class MessagesComponent implements OnInit, AfterViewInit, AfterViewChecked {

  @Output()
  connectionStatusUpdate: EventEmitter<string> = new EventEmitter();
  @Output()
  newMessage: EventEmitter<string> = new EventEmitter();

  @Input() public userRole: string;

  public messages = new Map<string, Message>();

  constructor(
    private component: ElementRef,
    private messageService: MessageService,
    private cd: ChangeDetectorRef
  ) {
  }

  ngOnInit(): void {
    this.messageService.connect(
      {
        next: messageEvent => this.handleMessageEvent(messageEvent),
        error: () => {
          this.connectionStatusUpdate.emit(DISCONNECTED);
        },
        complete: null
      }
    );
  }

  ngAfterViewInit(): void {
    this.scrollToBottom();
  }

  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }

  // For keyvalue pipe
  public asIsOrder(a, b) {
    return a.value.timestamp.getTime() - b.value.timestamp.getTime();
  }

  public isPairPresent(): boolean {
    return (this.userRole === ROLE_SANTA && typeof CurrentUser.gifteeName === 'string') ||
      (this.userRole === ROLE_GIFTEE && CurrentUser.santaPresent);
  }

  public getUserId(): string {
    return CurrentUser.id.toString();
  }

  private scrollToBottom(): void {
    this.cd.detectChanges();
    this.component.nativeElement.scrollTop = this.component.nativeElement.scrollHeight;
  }

  private handleMessageEvent(event: MessageEvent): void {
    switch (event.type) {
      case MESSAGE_BULK_GET:
        this.connectionStatusUpdate.emit(CONNECTED);
        this.handleBulkMessageReceiving(event.data);
        this.scrollToBottom();
        break;
      case MESSAGE_SHOW:
      case MESSAGE_GET:
        this.handleMessageReceiving(event.data.message);
        this.scrollToBottom();
        break;
      case MESSAGE_CONFIRM:
        this.handleConfirmation(event.data);
    }
  }

  private handleBulkMessageReceiving(messages: Message[]) {
    messages.forEach(message => {
      message.authorId = message.authorId.toString();
      message.delivered = true;
      if (this.isMessageFromThisChat(message)) {
        this.processMessage(message);
      }
    });
  }

  private handleMessageReceiving(message: Message): void {
    if (this.isMessageFromThisChat(message)) {
      this.processMessage(message);
      this.newMessage.emit(this.userRole);
    }
  }

  private processMessage(message: Message): void {
    message.timestamp = new Date(message.timestamp);
    this.messages.set(message.hashString, message);
    this.scrollToBottom();
  }

  private handleConfirmation(message: Message): void {
    if (!this.messages.has(message.hashString)) {
      return;
    }
    this.messages.set(message.hashString, {...message, ...this.messages.get(message.hashString)});
    this.cd.detectChanges();
  }

  private isMessageFromThisChat(message: Message): boolean {
    return (message.userRole === this.userRole && message.authorId === CurrentUser.id.toString()) ||
      (message.userRole !== this.userRole && message.authorId !== CurrentUser.id.toString());
  }
}
