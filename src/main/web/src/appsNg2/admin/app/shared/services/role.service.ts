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

export type OnPermissionSelectedType = { id: string, selected: boolean };

@Injectable()
export class RoleService{

  public onRoleAdded = new Subject<Role>();
  public onPermissionSelected = new Subject<OnPermissionSelectedType>();

  private baseUrl: string = SERVER_API_URL;
  private http: Http;

  /** Role being edited or created */
  role: Role;

  constructor(@Inject(Http) http:Http) {
      this.http = http;
  }

  getPermissionsTree(roleTypeId: number): Observable<Permission> {
    return this.http.get(`${this.baseUrl}/permissions/tree?roleTypeId=${roleTypeId}`, {
      headers: this.getHeaders(),
    }).map((response) => response.json());
  }

  getRole(roleId: number) {
    return this.http.get(`${this.baseUrl}/roles/${roleId}`, {
      headers: this.getHeaders(),
    }).map((response) => response.json());
  }

  createRole(role: Role): any {
    return this.http.post(`${this.baseUrl}/roles`, {
      name: role.name,
      description: role.description || '',
      roleType: role.type,
      permissions: role.permissions.map((permission: Permission) => {
        return permission.id;
      }),
      editable: true,
      assignable: true
    }, { headers: this.getHeaders() });
  }

  updateRole(role: Role, showWarnings: boolean) {
    return this.http.put(`${this.baseUrl}/roles`, {
      id: role.id,
      name: role.name,
      description: role.description || '',
      roleType: role.type,
      permissions: role.permissions.map((permission: Permission) => {
        return permission.id;
      }),
      editable: true,
      assignable: true,
      showWarnings: showWarnings
    }, { headers: this.getHeaders() });
  }

  getRoleTypes(): Observable<RoleType[]>{
    return this.http.get(`${this.baseUrl}/role-types`,{ headers: this.getHeaders() }).map(response => this.mapRoleType(response));
  }

  getCrops(): Observable<Crop[]> {
    return this.http.get(`${this.baseUrl}/crop/list`,{ headers: this.getHeaders() }).map(response => this.mapCrop(response));

  }

  getPrograms(cropName: string){
    return this.http.get(`${this.baseUrl}/crops/${cropName}/program`,{ headers: this.getHeaders() }).map(response => this.mapProgram(response));

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
    type: r.roleType.name, // TODO Deprecate
    roleType: r.roleType,
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
 * Visit each permission in the tree
 */
export function visitPermissions(permissions: Permission[], visit: (permission: Permission) => any) {
  for (const permission of permissions) {
    visit(permission);
    if (permission.children && permission.children.length) {
      visitPermissions(permission.children, visit);
    }
  }
  return permissions;
}

/**
 * @permissions recurse the tree and set the parent
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
