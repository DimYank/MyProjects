import {Injectable} from '@angular/core';
import {PAIRS} from './mock-pairs';

@Injectable({
  providedIn: 'root'
})
export class MockAssignmentService {

  public getPairs(callback: (pairs: Map<string, string>) => void): void {
    callback(PAIRS);
  }

public assign(pair: {}, callback: (response: string | void) => void): void {
    callback();
  }

  public unassign(santaName, callback: (response: string | void) => void): void {
    callback();
  }
}
