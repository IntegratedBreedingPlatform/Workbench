import { Injectable } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { User } from './../models/user.model';

@Injectable()
export class UserService{
  private baseUrl: string = '/bmsapi/brapi/v1';

  constructor(private http : Http){
  }

  getAll(): Observable<User[]>{
    let users$ = this.http
      .get(`${this.baseUrl}/users`, {headers: this.getHeaders()})
      .map(mapUsers);
      return users$;
  }

  get(id: number): Observable<User> {
    let User$ = this.http
      .get(`${this.baseUrl}/users/${id}`, {headers: this.getHeaders()})
      .map(mapUser);
      return User$;
  }

  save(User: User) : Observable<Response>{
    return this.http
      .put(`${this.baseUrl}/users/${User.id}`, JSON.stringify(User), {headers: this.getHeaders()});
  }

  private getHeaders(){
    let headers = new Headers();
    headers.append('Accept', 'application/json');
    headers.append('X-Auth-Token', JSON.parse(localStorage["bms.xAuthToken"]).token);
    return headers;
  }
}


function mapUsers(response:Response): User[]{
   return response.json().map(toUser)
}

function toUser(r:any): User{
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

function mapUser(response:Response): User{
  return toUser(response.json());
}
