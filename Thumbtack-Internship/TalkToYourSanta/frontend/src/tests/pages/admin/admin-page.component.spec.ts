import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';

import {AdminPageComponent} from '../../../app/pages/admin-page/admin-page.component';
import {AssignmentService} from '../../../app/services/assignment/assignment.service';
import {DebugElement} from '@angular/core';
import {By} from '@angular/platform-browser';
import {MockAssignmentService} from './mock.assignment.service';
import {HeaderComponent} from '../../../app/components/header/header.component';
import {FormsModule} from '@angular/forms';

describe('AdminPageComponent', () => {
  let component: AdminPageComponent;
  let fixture: ComponentFixture<AdminPageComponent>;
  let debugElem: DebugElement;
  let santasList: DebugElement;
  let gifteesList: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        FormsModule
      ],
      declarations: [
        HeaderComponent,
        AdminPageComponent
      ],
      providers: [
        {provide: AssignmentService, useClass: MockAssignmentService}
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    debugElem = fixture.debugElement;
    santasList = debugElem.query(By.css('#santas-list'));
    gifteesList = debugElem.query(By.css('#giftees-list'));
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should assign users', fakeAsync(() => {
    const assignButton = debugElem.query(By.css('.table-buttons button'));
    const santaElem = santasList.query(By.css('td'));
    const gifteeElem = gifteesList.queryAll(By.css('td'))[2];
    const santaElemId = santaElem.nativeElement.id;
    const gifteeElemId = gifteeElem.nativeElement.id;
    santaElem.nativeElement.click();
    gifteeElem.nativeElement.click();
    fixture.detectChanges();
    expect(assignButton.nativeElement.disabled).toBeFalsy();
    assignButton.nativeElement.click();
    tick();
    fixture.detectChanges();
    expect(santasList.query(By.css(`#${santaElemId}`)).nativeElement.classList).toContain('list-item-assigned');
    expect(gifteesList.query(By.css(`#${gifteeElemId}`)).nativeElement.classList).toContain('list-item-assigned');
  }));

  it('should disable selected santa in giftees list', () => {
    const santaElem = santasList.query(By.css('td'));
    const santaElemText = santaElem.nativeElement.innerText;
    santaElem.nativeElement.click();
    fixture.detectChanges();
    expect(gifteesList.query(By.css(`#${santaElemText}_g`)).nativeElement.classList).toContain('list-item-disabled');
  });

  it('should remove selected giftee from santas list', () => {
    const gifteeElem = gifteesList.query(By.css('td'));
    const gifteeElemText = gifteeElem.nativeElement.innerText;
    gifteeElem.nativeElement.click();
    fixture.detectChanges();
    expect(santasList.query(By.css(`#${gifteeElemText}_s`)).nativeElement.classList).toContain('list-item-disabled');
  });

  it('santa filter should work', () => {
    const nameToSearch = 'dima11';
    const santaFilter = debugElem.query(By.css('.column-search input'));
    santaFilter.nativeElement.value = nameToSearch;
    santaFilter.nativeElement.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    expect(santasList.queryAll(By.css('td')).length).toBe(1);
    expect(santasList.query(By.css('td')).nativeElement.innerText).toBe(nameToSearch);
  });

  it('giftee filter should work', () => {
    const nameToSearch = 'dima11';
    const gifteeFilter = debugElem.queryAll(By.css('.column-search input'))[1];
    gifteeFilter.nativeElement.value = nameToSearch;
    gifteeFilter.nativeElement.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    expect(gifteesList.queryAll(By.css('td')).length).toBe(1);
    expect(gifteesList.query(By.css('td')).nativeElement.innerText).toBe(nameToSearch);
  });

  it( 'should unassign pair', () => {
    const unassignButton = debugElem.queryAll(By.css('.table-buttons button'))[1];
    const santaElem = santasList.query(By.css('.list-item-assigned'));
    const gifteeElem = gifteesList.query(By.css('.list-item-assigned'));
    const santaElemId = santaElem.nativeElement.id;
    const gifteeElemId = gifteeElem.nativeElement.id;
    santaElem.nativeElement.click();
    fixture.detectChanges();
    expect(unassignButton.nativeElement.disabled).toBeFalsy();
    unassignButton.nativeElement.click();
    fixture.detectChanges();
    expect(santasList.query(By.css(`#${santaElemId}`)).nativeElement.classList).not.toContain('list-item-assigned');
    expect(gifteesList.query(By.css(`#${gifteeElemId}`)).nativeElement.classList).not.toContain('list-item-assigned');
  });
});
