import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {STATUS_ADMIN} from '../constants/user-status';
import {AuthorizationGuard} from './authorization.guard';
import {UserService} from '../services/user/user.service';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {

  constructor(
    private router: Router,
    private authGuard: AuthorizationGuard,
    private userService: UserService
  ) { }

  canActivate(): boolean {
    if (!this.authGuard.canActivate()) {
      return false;
    }
    if (this.userService.getUserStatus() !== STATUS_ADMIN) {
      this.router.navigate(['chat']);
      return false;
    }
    return true;
  }

}
