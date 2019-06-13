import { Injectable , Inject} from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { Role } from './../models/role.model';
import ServiceHelper from "./service.helper";
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class RoleService{
  private baseUrl: string = SERVER_API_URL;

  private http: Http;

  constructor(@Inject(Http) http:Http) {
      this.http = http;
  }

  getAll(): Observable<Role[]>{
    let Roles$ = this.http
      .get(`${this.baseUrl}/roles/assignable`, {headers: this.getHeaders()})
      .map(mapRoles);
      return Roles$;
  }

  private getHeaders(){
      return ServiceHelper.getHeaders();
  }
}


function mapRoles(response:Response): Role[]{
   return response.json().map(toRole)
}

function toRole(r:any): Role{
  let Role = <Role>({
    id: r.id,
    name: r.name,
    type: r.type
  });
  return Role;
}

function mapRole(response:Response): Role{
  return toRole(response.json());
}
