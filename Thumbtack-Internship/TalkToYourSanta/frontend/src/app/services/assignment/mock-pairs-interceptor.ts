import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {environment} from '../../../environments/environment';
import {PAIRS} from '../../../tests/pages/admin/mock-pairs';

@Injectable({
  providedIn: 'root'
})
export class PairsMockInterceptor implements HttpInterceptor {

  private assignmentDB: Map<string, string> = PAIRS;

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (req.url === environment.API_ASSIGNMENT_URL && req.method === 'GET') {
      return of(new HttpResponse({status: 200, body: this.assignmentDB}));
    }
    if (req.url === environment.API_ASSIGNMENT_URL && req.method === 'PUT') {
      console.log('Pair Sent To Server: ', JSON.parse(req.body));
      return of(new HttpResponse({status: 200, body: {}}));
    }
    if (req.method === 'DELETE') {
      console.log('Pair Deleted For Santa: ', req.url);
      return of(new HttpResponse({status: 200, body: {}}));
    }
    return next.handle(req);
  }
}
