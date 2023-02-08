import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs/Rx';
import { Role } from './../models/role.model';
import { RoleFilter } from './../models/role-filter.model';
import { SERVER_API_URL } from '../../app.constants';
import { RoleType } from '../models/role-type.model';
import { Permission } from '../models/permission.model';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { createRequestOption } from '../../shared';
import { Program } from '../../shared/user/model/program.model';
import { Crop } from '../../shared/model/crop.model';

export type OnPermissionSelectedType = { id: string, selected: boolean };

@Injectable()
export class RoleService {

    private baseUrl: string = SERVER_API_URL;

    public onPermissionSelected = new Subject<OnPermissionSelectedType>();

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
        return this.http.get(`${this.baseUrl}/crops/${cropName}/programs`, { observe: 'response' }).pipe(map((response: HttpResponse<Program[]>) => response.body.map((p: any) => {
            return <Program>{
                id: p.id,
                name: p.name,
                uuid: p.uniqueID,
                crop: new Crop(p.crop)
            }
        })));
    }

    searchRoles(roleFilter: RoleFilter, pagination: any): Observable<HttpResponse<Role[]>> {
        const params = createRequestOption(pagination);
        return this.http.post<Role[]>(`${this.baseUrl}roles/search`, roleFilter, { params, observe: 'response' });
    }

    getRoles(): Observable<Role[]> {
        const roleFilter = new RoleFilter();
        return this.http.post<Role[]>(`${this.baseUrl}roles/search`, roleFilter, { observe: 'response' })
            .pipe(map((res: HttpResponse<Role[]>) => res.body));
    }
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
