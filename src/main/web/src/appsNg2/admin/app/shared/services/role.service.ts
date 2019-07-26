import { Inject, Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable, Subject } from 'rxjs/Rx';
import { Role } from './../models/role.model';
import { RoleFilter } from './../models/role-filter.model';
import ServiceHelper from './service.helper';
import { SERVER_API_URL } from '../../app.constants';
import { Crop } from '../models/crop.model';
import { Program } from '../models/program.model';
import { RoleType } from '../models/role-type.model';
import { Permission } from '../models/permission.model';

@Injectable()
export class RoleService{

  public onRoleAdded = new Subject<Role>();

  private baseUrl: string = SERVER_API_URL;
  private http: Http;

  constructor(@Inject(Http) http:Http) {
      this.http = http;
  }

  createRole(role: Role): any {
    return this.http.post(`${this.baseUrl}/roles`, {
      name: role.name,
      description: role.description,
      roleType: role.type,
      permissions: role.permissions.map((permission: Permission) => {
        return permission.id;
      })
    }, { headers: this.getHeaders() });
  }

  getRoleTypes(): Observable<RoleType[]>{
    return this.http.get(`${this.baseUrl}/role-types`,{ headers: this.getHeaders() }).map(response => this.mapRoleType(response));
  }

  getCrops(): Observable<Crop[]> {
    return this.http.get(`${this.baseUrl}/crop/list`,{ headers: this.getHeaders() }).map(response => this.mapCrop(response));

  }

  getPrograms(cropName: string){
    return this.http.get(`${this.baseUrl}/program?cropName=${cropName}`,{ headers: this.getHeaders() }).map(response => this.mapProgram(response));

  }

  getFilteredRoles(roleFilter: RoleFilter): Observable<Role[]> {
    let headers = this.getHeaders();
    headers.append('Content-Type', 'application/json');

    let Roles$ = this.http
        .post(`${this.baseUrl}/roles/search`, JSON.stringify(roleFilter), { headers: headers })
      .map(mapRoles);
      return Roles$;
  }

  private getHeaders(){
      return ServiceHelper.getHeaders();
  }

  private mapRoleType(response: Response): RoleType[] {
    return response.json().map(toRoleType);
  }

  private mapCrop(response: Response): Crop[] {
    return response.json().map(toCrop);
  }

  private mapProgram(response: Response): Program[] {
    return response.json().map(toProgram);
  }

}



function mapRoles(response:Response): Role[]{
   return response.json().map(toRole)
}

function toRoleType(r:any): RoleType{
  let roleType = <RoleType>({
    id: r.id,
    name: r.name
  });
  return roleType;
}

function toRole(r:any): Role{
  let role = <Role>({
    id: r.id,
    name: r.name,
    description: r.description,
    type: r.roleType.name,
    active: r.active,
    editable: r.editable,
    assignable: r.assignable,
    permissions: r.permissions
  });
  return role;
}

function toCrop(r:any): Crop{
  let crop = <Crop>({
    cropName: r.cropName
  });
  return crop;
}

function toProgram(r: any): Program {
  let Program = <Program>({
    id: r.id,
    name: r.name,
    uuid: r.uniqueID,
    crop: new Crop(r.crop)
  });
  return Program;
}

function mapRole(response:Response): Role{
  return toRole(response.json());
}

/**
 * @permissions permissions iterate the list and set the parent based on the children
 */
export function setParent(permissions: Permission[], parent: Permission) {
    for (const permission of permissions) {
        permission.parent = parent;
        if (permission.children && permission.children.length) {
            setParent(permission.children, permission);
        }
    }
    return permissions;
}