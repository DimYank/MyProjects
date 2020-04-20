import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderComponent } from '../../../app/components/header/header.component';

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HeaderComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('menu should be toggled', () => {
    const nativeElem: HTMLElement = fixture.nativeElement;
    const menuBefore = nativeElem.querySelector('app-menu');
    expect(menuBefore).toBeNull();
    const menuButton = nativeElem.querySelector('button');
    menuButton.click();
    fixture.detectChanges();
    const menuAfter = nativeElem.querySelector('app-menu');
    expect(menuAfter).not.toBeNull();
  });
});
