import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {UserService} from '../services/user/user.service';

@Injectable({
  providedIn: 'root'
})
export class AuthorizationGuard implements CanActivate {
  constructor(
    private userService: UserService,
    private router: Router
  ) { }
  canActivate(): boolean {
    if (!this.userService.getToken()) {
      this.router.navigate(['login']);
      return false;
    }
    return true;
  }
}
