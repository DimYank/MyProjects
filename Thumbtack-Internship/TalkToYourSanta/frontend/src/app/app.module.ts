import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './components/header/header.component';
import {MenuComponent} from './components/menu/menu.component';
import {MessagesComponent} from './components/messages/messages.component';
import {FooterComponent} from './components/footer/footer.component';
import {AuthPageComponent} from './pages/auth-page/auth-page.component';
import {ChatPageComponent} from './pages/chat-page/chat-page.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {AdminPageComponent} from './pages/admin-page/admin-page.component';
import {PairsMockInterceptor} from './services/assignment/mock-pairs-interceptor';
import {AuthInterceptor} from './services/user/auth-interceptor';
import {environment} from '../environments/environment';
import {LoginMockInterceptor} from './services/user/login-mock-interceptor.service';
import { NotificationComponent } from './components/notification/notification.component';

const providers = [
  {
    provide: HTTP_INTERCEPTORS,
    useClass: AuthInterceptor,
    multi: true
  }
];
if (!environment.production) {
  providers.push({
      provide: HTTP_INTERCEPTORS,
      useClass: LoginMockInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: PairsMockInterceptor,
      multi: true
    });
}

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    MenuComponent,
    MessagesComponent,
    FooterComponent,
    AuthPageComponent,
    ChatPageComponent,
    AdminPageComponent,
    NotificationComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    BrowserAnimationsModule
  ],
  providers,
  bootstrap: [AppComponent]
})
export class AppModule { }
