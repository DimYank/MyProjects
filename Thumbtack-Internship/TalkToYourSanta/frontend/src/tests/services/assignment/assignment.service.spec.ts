import { TestBed } from '@angular/core/testing';

import { AssignmentService } from '../../../app/services/assignment/assignment.service';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';

describe('AssignmentService', () => {
  let service: AssignmentService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    http = TestBed.inject(HttpTestingController);
    service = TestBed.inject(AssignmentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
