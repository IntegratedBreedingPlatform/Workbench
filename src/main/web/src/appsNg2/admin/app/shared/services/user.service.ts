import { Injectable, Inject } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { User } from './../models/user.model';

@Injectable()
export class UserService{
  private baseUrl: string = '/bmsapi/brapi/v1';

  private http: Http;

  constructor(@Inject(Http) http:Http) {
      this.http = http;
  }

  getAll(): Observable<User[]>{
    let users$ = this.http
      .get(`${this.baseUrl}/users`, {headers: this.getHeaders()})
      .map(response => this.mapUsers(response));
      return users$;
  }

  get(id: number): Observable<User> {
    let User$ = this.http
      .get(`${this.baseUrl}/users/${id}`, {headers: this.getHeaders()})
      .map(response => this.mapUser(response));
      return User$;
  }

  save(user: User) : Observable<Response>{
    return this.http
      .post(`${this.baseUrl}/users`, JSON.stringify(User), {headers: this.getHeaders()});
  }

  update(user: User) : Observable<Response>{
    return this.http
      .put(`${this.baseUrl}/users`, JSON.stringify(User), {headers: this.getHeaders()});
  }

  private getHeaders(){
    let headers = new Headers();
    headers.append('Accept', 'application/json');
    headers.append('X-Auth-Token', JSON.parse(localStorage["bms.xAuthToken"]).token);
    return headers;
  }

  private mapUsers(response:Response): User[]{
     return response.json().map(this.toUser)
  }

  private toUser(r:any): User{
    let User = <User>({
      id: r.userId,
      firstName: r.firstName,
      lastName: r.lastName,
      username: r.username,
      role: r.role,
      email: r.email,
      status: r.status,
    });
    return User;
  }

  private mapUser(response:Response): User{
    return this.toUser(response.json());
  }

}
