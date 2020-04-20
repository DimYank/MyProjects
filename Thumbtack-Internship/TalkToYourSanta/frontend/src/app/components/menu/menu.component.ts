import {Component, OnInit} from '@angular/core';
import {STATUS_ADMIN} from '../../constants/user-status';
import {Router} from '@angular/router';
import {UserService} from '../../services/user/user.service';
import {CurrentUser} from '../../models/current-user';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.sass']
})
export class MenuComponent implements OnInit {

  public showAdminButton = false;

  constructor(
    private router: Router,
    private userService: UserService
  ) { }

  ngOnInit(): void {
    if (this.userService.getUserStatus() === STATUS_ADMIN) {
      this.showAdminButton = true;
    }
  }

  public logOut(): void {
    this.userService.logOut();
  }

  public getUserName(): string {
    return  CurrentUser.firstName || '...';
  }
}
