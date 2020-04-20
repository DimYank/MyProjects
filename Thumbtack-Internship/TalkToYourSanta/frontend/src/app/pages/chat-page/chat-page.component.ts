import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {ROLE_GIFTEE, ROLE_SANTA} from '../../constants/user-roles';
import {CurrentUser} from '../../models/current-user';
import {NotificationComponent} from '../../components/notification/notification.component';
import {CONNECTED, DISCONNECTED} from '../../constants/connection-status';
import {UserService} from '../../services/user/user.service';

@Component({
  selector: 'app-chat-page',
  templateUrl: './chat-page.component.html',
  styleUrls: ['./chat-page.component.sass']
})
export class ChatPageComponent implements AfterViewInit {

  @ViewChild('notification')
  private notificationsComponent: NotificationComponent;
  private connected = false;

  public currentUserRole = ROLE_GIFTEE;

  constructor(
    private userService: UserService
  ) {
    this.showUserInfoErrorNotification = this.showUserInfoErrorNotification.bind(this);
  }

  ngAfterViewInit(): void {
    this.userService.getUserData(this.showUserInfoErrorNotification);
  }

  public setRole(role: string) {
    this.currentUserRole = role;
  }

  public giftee(): string {
    return ROLE_GIFTEE;
  }

  public santa(): string {
    return ROLE_SANTA;
  }

  public getPairName(): string {
    return this.currentUserRole === ROLE_SANTA ? CurrentUser.gifteeName : 'Santa';
  }

  public isPairPresent(): boolean {
    return (this.currentUserRole === ROLE_SANTA && typeof CurrentUser.gifteeName === 'string') ||
      (this.currentUserRole === ROLE_GIFTEE && CurrentUser.santaPresent);
  }

  public updateConnectionStatus(newStatus: string): void {
    if (newStatus === CONNECTED) {
      if (!this.connected) {
        this.notificationsComponent.addNotification({
          text: 'Connected To Server',
          expirationTime: 2500
        });
      }
      this.connected = true;
    }
    if (newStatus === DISCONNECTED) {
      if (this.connected) {
        this.notificationsComponent.addNotification({
          text: 'Connection To Server Lost! Reconnecting...',
          expirationTime: null
        });
      }
      this.connected = false;
    }
  }

  public showNewMessageNotification(messageTabRole: string): void {
    const messageAuthor = messageTabRole === ROLE_GIFTEE ? ROLE_SANTA : ROLE_GIFTEE;
    if (messageTabRole !== this.currentUserRole) {
      this.notificationsComponent.addNotification({
        text: `New Message From ${messageAuthor}!`,
        expirationTime: 3000
      });
    }
  }

  private showUserInfoErrorNotification(): void {
    this.notificationsComponent.addNotification({
      text: 'Can\'t Get Response From Server!',
      expirationTime: null
    });
  }
}
