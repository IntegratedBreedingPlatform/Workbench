import { Injectable } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { User } from './inMemory.model';

@Injectable()
export class UserService{
  private baseUrl: string = '/bmsapi';

  constructor(private http : Http){
  }

  getAll(): Observable<User[]>{
    let users$ = this.http
      .get(`${this.baseUrl}/users/listUsers`, {headers: this.getHeaders()})
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
    // this won't actually work because the StarWars API doesn't
    // is read-only. But it would look like this:
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
   // The response of the API has a results
   // property with the actual results
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
  console.log('Parsed User:', User);
  return User;
}

function mapUser(response:Response): User{
  // toUser looks just like in the previous example
  return toUser(response.json());
}
