import { Injectable } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { Role } from './../models/role.model';

@Injectable()
export class RoleService{
  private baseUrl: string = '/bmsapi/brapi/v1';

  constructor(private http : Http){
  }

  getAll(): Observable<Role[]>{
    let Roles$ = this.http
      .get(`${this.baseUrl}/roles`, {headers: this.getHeaders()})
      .map(mapRoles);
      return Roles$;
  }

  private getHeaders(){
    let headers = new Headers();
    headers.append('Accept', 'application/json');
    headers.append('X-Auth-Token', JSON.parse(localStorage["bms.xAuthToken"]).token);
    return headers;
  }
}


function mapRoles(response:Response): Role[]{
   return response.json().map(toRole)
}

function toRole(r:any): Role{
  let Role = <Role>({
    id: r.id,
   description: r.description,
  });
  return Role;
}

function mapRole(response:Response): Role{
  return toRole(response.json());
}
