import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ChatPageComponent } from './pages/chat-page/chat-page.component';
import { AuthPageComponent } from './pages/auth-page/auth-page.component';
import {AuthorizationGuard} from './guards/authorization.guard';
import {AdminPageComponent} from './pages/admin-page/admin-page.component';
import {AdminGuard} from './guards/admin.guard';


const routes: Routes = [
  {path: 'chat', component: ChatPageComponent, canActivate: [AuthorizationGuard]},
  {path: 'admin', component: AdminPageComponent, canActivate: [AdminGuard]},
  {path: 'login', component: AuthPageComponent},
  {path: '', redirectTo: 'chat', pathMatch: 'full'},
  {path: '**', redirectTo: 'chat'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
