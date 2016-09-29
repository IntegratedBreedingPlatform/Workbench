import { Injectable , Inject} from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import { User } from './../models/user.model';
import { Observable } from 'rxjs/Rx';

@Injectable()
export class MailService{
  private baseUrl: string = '/ibpworkbench/auth'; //sendResetEmail/{username}

  private http: Http;

  constructor(@Inject(Http) http:Http) {
      this.http = http;
  }

  send(user: User): Observable<Response>{
    return this.http
      .get(`${this.baseUrl}/sendResetEmail/${user.username}`, {headers: this.getHeaders()});
  }

  private getHeaders(){
    let headers = new Headers();
    headers.append('Accept', 'application/json');
    headers.append('X-Auth-Token', JSON.parse(localStorage["bms.xAuthToken"]).token);
    return headers;
  }
}
