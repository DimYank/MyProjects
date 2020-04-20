import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {MenuComponent} from '../../../app/components/menu/menu.component';
import {ChatPageComponent} from '../../../app/pages/chat-page/chat-page.component';
import {AuthPageComponent} from '../../../app/pages/auth-page/auth-page.component';
import {Routes} from '@angular/router';
import {AppRoutingModule} from '../../../app/app-routing.module';
import {FormsModule} from '@angular/forms';
import {RouterTestingModule} from '@angular/router/testing';
import {HttpClient} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('MenuComponent', () => {
  let component: MenuComponent;
  let fixture: ComponentFixture<MenuComponent>;

  const routes: Routes = [
    {path: 'chat', component: ChatPageComponent},
    {path: 'login', component: AuthPageComponent}
  ];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        AppRoutingModule,
        FormsModule,
        RouterTestingModule.withRoutes(routes),
        HttpClientTestingModule
      ],
      declarations: [MenuComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
