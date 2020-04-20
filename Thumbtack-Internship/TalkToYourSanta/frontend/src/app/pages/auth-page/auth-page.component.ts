import {Component} from '@angular/core';
import {UserService} from 'src/app/services/user/user.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-auth-page',
  templateUrl: './auth-page.component.html',
  styleUrls: ['./auth-page.component.sass']
})
export class AuthPageComponent {

  username = '';
  password = '';
  warningMessage: string;
  disableLogInButton = false;

  constructor(
    private userService: UserService,
    private router: Router
  ) { }

  public logInUser() {
    if (!this.username.length || !this.password.length) {
      return;
    }
    this.disableLogInButton = true;
    this.warningMessage = null;
    this.userService.logIn(
      {username: this.username.trim(), password: this.password},
      (message: string | void) => this.handleResponse(message)
    );
  }

  private handleResponse(message: string | void): void {
    if (!message) {
      this.goToChatPage();
      return;
    }
    this.handleLoginError(message);
  }

  private goToChatPage() {
    this.router.navigate(['chat']);
  }

  private handleLoginError(errorMessage: string) {
    this.warningMessage = errorMessage;
    this.disableLogInButton = false;
  }
}
