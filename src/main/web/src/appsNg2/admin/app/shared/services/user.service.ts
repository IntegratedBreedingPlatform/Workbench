import { Injectable, Inject } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { User } from './../models/user.model';
import ServiceHelper from "./service.helper";

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
    let headers = this.getHeaders()
    headers.append('Content-Type', 'application/json');
    return this.http
      .post(`${this.baseUrl}/users`, JSON.stringify(user), {headers: headers});
    ;
  }

  update(user: User) : Observable < Response > {
      let headers = this.getHeaders()
      headers.append('Content-Type', 'application/json');
      return this.http
          .put(`${this.baseUrl}/users/${user.id}`, JSON.stringify(user), { headers: headers });
  }

  private getHeaders() {
      return ServiceHelper.getHeaders();
  }

  private mapUsers(response:Response): User[]{
     return response.json().map(this.toUser)
  }

  private toUser(r:any): User{
    let User = <User>({
      id: r.id,
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
