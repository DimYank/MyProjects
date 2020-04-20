import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {Notification} from '../../models/notification';

@Injectable({
  providedIn: 'root'
})
export class AssignmentService {

  constructor(
    private http: HttpClient
  ) { }

  public getPairs(responseCallback: (pairs: Map<string, string>) => void, errorCallback: (notification: Notification) => void): void {
    this.http.get<Map<string, string>>(environment.API_ASSIGNMENT_URL).subscribe(
      response => responseCallback(new Map(Object.entries(response))),
      () => errorCallback({ text: 'Can\'t Receive Data From Server!', expirationTime: null })
    );
  }

  public assign(pair: {}, successCallback: () => void, errorCallback: (notification: Notification) => void): void {
    this.http.put(environment.API_ASSIGNMENT_URL, pair, {observe: 'response'}).subscribe(
      response => successCallback(),
      () => errorCallback({ text: 'Can\'t Assign Pair! Something went wrong...', expirationTime: null })
    );
  }

  public unassign(santaName, successCallback: () => void, errorCallback: (notification: Notification) => void): void {
    this.http.put(environment.API_ASSIGNMENT_URL, {[santaName]: null}).subscribe(
      () => successCallback(),
      () => errorCallback({ text: 'Can\'t Unassign Pair! Something went wrong...', expirationTime: null })
    );
  }

  public assignRandom(responseCallback: (pairs: Map<string, string>) => void, errorCallback: (notification: Notification) => void): void {
    this.http.post<Map<string, string>>(environment.API_ASSIGNMENT_URL, null).subscribe(
      response => responseCallback(new Map(Object.entries(response))),
      () => errorCallback({ text: 'Can\'t Randomly Assign Pairs! Something went wrong...', expirationTime: null })
    );
  }

  public unassignAll(responseCallback: (pairs: Map<string, string>) => void, errorCallback: (notification: Notification) => void): void {
    this.http.delete<Map<string, string>>(environment.API_ASSIGNMENT_URL).subscribe(
      response => responseCallback(new Map(Object.entries(response))),
      () => errorCallback({ text: 'Can\'t Unassign All Pairs! Something went wrong...', expirationTime: null })
    );
  }
}
