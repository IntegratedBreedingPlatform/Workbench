import { Injectable , Inject} from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import { User } from './../models/user.model';
import { Observable } from 'rxjs/Rx';

@Injectable()
export class MailService{
  private baseUrl: string = '/ibpworkbench/controller/auth'; //sendResetEmail/{username}

  private http: Http;

  constructor(@Inject(Http) http:Http) {
      this.http = http;
  }

  send(user: User): Observable<Response>{
    return this.http
      .post(`${this.baseUrl}/sendResetEmail/${user.id}`, {headers: this.getHeaders()});
  }

  private getHeaders(){
    let headers = new Headers();
    headers.append('Accept', 'application/json');
    headers.append('Authorization', 'Bearer ' + JSON.parse(localStorage["bms.xAuthToken"]).token);
    return headers;
  }
}
