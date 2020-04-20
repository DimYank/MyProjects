import {Component, Input, OnInit} from '@angular/core';
import {timer} from 'rxjs';
import {Notification} from '../../models/notification';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.sass']
})
export class NotificationComponent {

  public notifications: Notification[] = [];

  public addNotification(newNotification: Notification): void {
    this.notifications.push(newNotification);
    if (!newNotification.expirationTime) {
      return;
    }
    timer(newNotification.expirationTime).subscribe(() => {
      this.removeNotification(newNotification);
    });
  }

  public removeNotification(notification: Notification): void {
    this.notifications.splice(this.notifications.indexOf(notification), 1);
  }
}
