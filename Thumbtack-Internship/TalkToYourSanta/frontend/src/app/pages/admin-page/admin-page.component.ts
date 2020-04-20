import {AfterViewInit, ChangeDetectorRef, Component, ViewChild} from '@angular/core';
import {AssignmentService} from '../../services/assignment/assignment.service';
import {NotificationComponent} from '../../components/notification/notification.component';
import {Notification} from '../../models/notification';
import {UserService} from '../../services/user/user.service';
import {CurrentUser} from '../../models/current-user';

@Component({
  selector: 'app-admin-page',
  templateUrl: './admin-page.component.html',
  styleUrls: ['./admin-page.component.sass']
})
export class AdminPageComponent implements AfterViewInit {

  @ViewChild('notification')
  private notificationComponent: NotificationComponent;

  public assignedPairs = new Map<string, string>();
  public freeSantas: string[] = [];
  public freeGiftees: string[] = [];

  public selectedSanta = '';
  public selectedGiftee = '';

  public unassignSanta = '';
  public unassignGiftee = '';

  public santaFilter = '';
  public gifteeFilter = '';

  constructor(
    private assignmentService: AssignmentService,
    private userService: UserService,
    private cd: ChangeDetectorRef
  ) {
    this.setNotification = this.setNotification.bind(this);
    this.showUserInfoErrorNotification = this.showUserInfoErrorNotification.bind(this);
    this.processCollections = this.processCollections.bind(this);
  }

  private setNotification = (notification: Notification): void => {
    this.notificationComponent.addNotification(notification);
  }

  private compareStringsLexicographically = (str1: string, str2: string): number => str1 < str2 ? -1 : str1 > str2 ? 1 : 0;
  private filterArrayByFirstLetters = (value: string, filter: string): boolean => value.startsWith(filter);

  public filteredFreeSantas(): string[] {
    return this.freeSantas
      .filter(santaName => this.filterArrayByFirstLetters(santaName, this.santaFilter))
      .sort(this.compareStringsLexicographically);
  }

  public filteredFreeGiftees(): string[] {
    return this.freeGiftees
      .filter(gifteeName => this.filterArrayByFirstLetters(gifteeName, this.gifteeFilter))
      .sort(this.compareStringsLexicographically);
  }

  public filteredAssignedPairs(): Map<string, string> {
    const filteredPairs = new Map<string, string>();
    this.assignedPairs.forEach((gifteeName, santaName) => {
      if (santaName.startsWith(this.santaFilter) && gifteeName.startsWith(this.gifteeFilter)) {
        filteredPairs.set(santaName, gifteeName);
      }
    });
    return filteredPairs;
  }

  public sortPairsAsIs(a, b): number {
    return this.compareStringsLexicographically(a.key, b.key);
  }

  public santaId(santa: string): string {
    return `${santa}_s`;
  }

  public gifteeId(giftee: string): string {
    return `${giftee}_g`;
  }

  public ngAfterViewInit(): void {
    this.setNotification({text: 'Retrieving data...', expirationTime: 3000});
    this.getCollections();
    if (!CurrentUser.firstName) {
      this.userService.getUserData(this.showUserInfoErrorNotification);
    }
  }

  public getCollections(): void {
    this.assignmentService.getPairs(
      this.processCollections,
      this.setNotification
    );
  }

  private processCollections(pairs: Map<string, string>): void {
    this.assignedPairs.clear();
    this.freeSantas = [];
    this.freeGiftees = [];
    pairs.forEach((value, key) => {
      value ? this.assignedPairs.set(key, value) : this.freeSantas.push(key);
    });
    this.freeGiftees = this.makeFreeGiftees();
    this.setNotification({text: 'Data received!', expirationTime: 3000});
  }

  private makeFreeGiftees(): string[] {
    const allSantas = [...this.freeSantas, ...this.assignedPairs.keys()];
    const assignedGiftees = [...this.assignedPairs.values()];
    return allSantas.filter(santa => assignedGiftees.indexOf(santa) < 0);
  }

  public selectSanta(santa: string): void {
    this.selectedSanta = santa === this.selectedSanta ? '' : santa;
    this.deselectPairToUnassign();
  }

  public selectGiftee(giftee: string): void {
    this.selectedGiftee = giftee === this.selectedGiftee ? '' : giftee;
    this.deselectPairToUnassign();
  }

  public assignPair(): void {
    const callbackForAssignment = (): void => {
      this.freeSantas.splice(this.freeSantas.indexOf(this.selectedSanta), 1);
      this.freeGiftees.splice(this.freeGiftees.indexOf(this.selectedGiftee), 1);
      this.assignedPairs.set(this.selectedSanta, this.selectedGiftee);
      this.clearFilters();
      this.deselectNewPair();
    };
    this.assignmentService.assign(
      {[this.selectedSanta]: this.selectedGiftee},
      callbackForAssignment,
      this.setNotification
    );
  }

  public selectPairToUnassign(santa: string): void {
    this.unassignSanta = santa;
    this.unassignGiftee = this.assignedPairs.get(santa);
    this.scrollTableToElement(this.santaId(santa));
    this.scrollTableToElement(this.gifteeId(this.unassignGiftee));
    this.deselectNewPair();
  }

  public unassignPair(): void {
    const callbackForUnassignment = (): void => {
      this.freeSantas.push(this.unassignSanta);
      this.freeGiftees.push(this.unassignGiftee);
      this.assignedPairs.delete(this.unassignSanta);
      this.selectedSanta = this.unassignSanta;
      this.selectedGiftee = this.unassignGiftee;
      this.deselectPairToUnassign();
      this.clearFilters();
      this.cd.detectChanges();
      this.scrollTableToElement(this.santaId(this.selectedSanta));
      this.scrollTableToElement(this.gifteeId(this.selectedGiftee));
    };
    this.assignmentService.unassign(
      this.unassignSanta,
      callbackForUnassignment,
      this.setNotification
    );
  }

  public assignRandomPairs(): void {
    this.assignmentService.assignRandom(
      this.processCollections,
      this.setNotification
    );
    this.deselectNewPair();
    this.deselectPairToUnassign();
    this.clearFilters();
  }

  public unassignAllPairs(): void {
    this.assignmentService.unassignAll(
      this.processCollections,
      this.setNotification
    );
    this.deselectNewPair();
    this.deselectPairToUnassign();
    this.clearFilters();
  }

  public clearSantaSearch() {
    this.santaFilter = '';
  }

  public clearGifteeSearch() {
    this.gifteeFilter = '';
  }

  private deselectPairToUnassign(): void {
    this.unassignSanta = '';
    this.unassignGiftee = '';
  }

  private deselectNewPair(): void {
    this.selectedSanta = '';
    this.selectedGiftee = '';
  }

  private clearFilters(): void {
    this.gifteeFilter = '';
    this.santaFilter = '';
  }

  private scrollTableToElement(elemId: string) {
    document.getElementById(elemId).scrollIntoView({behavior: 'smooth'});
  }

  private showUserInfoErrorNotification(): void {
    this.notificationComponent.addNotification({
      text: 'Can\'t Get Response From Server!',
      expirationTime: null
    });
  }
}
