import {Component, Input} from '@angular/core';
import { MessageService } from 'src/app/services/message/message.service';
import {MESSAGE_CONFIRM, MESSAGE_SEND} from 'src/app/constants/message-event-types';
import {UserService} from '../../services/user/user.service';
import {Md5} from 'ts-md5';
import {ROLE_GIFTEE, ROLE_SANTA} from '../../constants/user-roles';
import {CurrentUser} from '../../models/current-user';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.sass']
})
export class FooterComponent {

  inputText = '';

  @Input() userRole: string;

  constructor(
    private userService: UserService,
    private messageService: MessageService
  ) { }

  public onEnterKeyDown(event) {
    event.preventDefault();
  }

  public sendMessage() {
    this.inputText = this.inputText.trim();
    if (!this.inputText.length || !this.isPairPresent()) {
      return;
    }
    this.messageService.sendMessage(
      new MessageEvent(
         MESSAGE_SEND,
        {
          data: {
            message:
              {
                authorId: CurrentUser.id.toString(),
                text: this.inputText,
                userRole: this.userRole,
                timestamp: new Date(),
                hashString: Md5.hashStr(this.inputText + new Date().getTime()) as string
              }
          }
        }
      )
    );
    this.inputText = '';
  }

  public isPairPresent(): boolean {
    return this.userRole === ROLE_SANTA ? typeof CurrentUser.gifteeName === 'string' : CurrentUser.santaPresent;
  }
}
