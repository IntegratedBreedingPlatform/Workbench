import { Inject, Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable, Subject } from 'rxjs/Rx';
import { Role } from './../models/role.model';
import { RoleFilter } from './../models/role-filter.model';
import { SERVER_API_URL } from '../../app.constants';
import { RoleType } from '../models/role-type.model';
import { Permission } from '../models/permission.model';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { createRequestOption } from '../../shared';
import { Crop } from '../../shared/model/crop.model';
import { Program } from '../../shared/user/model/program.model';

export type OnPermissionSelectedType = { id: string, selected: boolean };

@Injectable()
export class RoleService {

    private baseUrl: string = SERVER_API_URL;

    public onRoleAdded = new Subject<Role>();
    public onPermissionSelected = new Subject<OnPermissionSelectedType>();

    public role: Role;

    constructor(private http: HttpClient) {
    }

    getPermissionsTree(roleTypeId: number): Observable<Permission> {
        return this.http.get(`${this.baseUrl}/permissions/tree?roleTypeId=${roleTypeId}`, { observe: 'response' }).pipe(map((response) => response.body));
    }

    getRole(roleId: number) {
        return this.http.get(`${this.baseUrl}/roles/${roleId}`, { observe: 'response' }).pipe(map((response) => response.body));
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
        }, { observe: 'response' });
    }

    updateRole(role: Role, warnings: boolean) {
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
            showWarnings: warnings
        }, { observe: 'response' });
    }

    getRoleTypes(): Observable<RoleType[]> {
        return this.http.get(`${this.baseUrl}/role-types`, { observe: 'response' }).pipe(map((response: HttpResponse<RoleType[]>) => response.body));
    }

    getCrops(): Observable<Crop[]> {
        return this.http.get(`${this.baseUrl}/crop/list`, { observe: 'response' }).pipe(map((response: HttpResponse<Crop[]>) => response.body));

    }

    getPrograms(cropName: string): Observable<Program[]> {
        return this.http.get(`${this.baseUrl}/crops/${cropName}/programs`, { observe: 'response' }).pipe(map((response: HttpResponse<Program[]>) => response.body));

    }

    searchRoles(roleFilter: RoleFilter,  pagination: any): Observable<HttpResponse<Role[]>> {
        const params = createRequestOption(pagination);
        return this.http.post<Role[]>(`${this.baseUrl}roles/search`, roleFilter, { params, observe: 'response' });
            //  .pipe(map((response: HttpResponse<Role[]>) => response.body));
    }

/*    private getHeaders() {
        return ServiceHelper.getHeaders();
    }*/

    private mapRoleType(response: Response): RoleType[] {
        return response.json().map(toRoleType);
    }

    private mapCrop(response: Response): Crop[] {
        return response.json().map(toCrop);
    }
/*
    private mapProgram(response: Response): Program[] {
        return response.json().map(toProgram);
    }*/

}

function mapRoles(response: Response): Role[] {
    return response.json().map(toRole)
}

function toRoleType(r: any): RoleType {
  const roleType = <RoleType>({
        id: r.id,
        name: r.name
    });
    return roleType;
}

function toRole(r: any): Role {
  const role = <Role>({
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

function toCrop(r: any): Crop {
  const crop = <Crop>({
        cropName: r.cropName
    });
    return crop;
}

/*function toProgram(r: any): Program {
  const program = <Program>({
        id: r.id,
        name: r.name,
        uuid: r.uniqueID,
        crop: new Crop(r.crop)
    });
    return program;
}*/

function mapRole(response: Response): Role {
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
