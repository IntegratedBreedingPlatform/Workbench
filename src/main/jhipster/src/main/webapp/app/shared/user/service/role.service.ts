import { Observable } from 'rxjs/Rx';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { RoleFilter } from '../model/role-filter.model';
import { Role } from '../model/role.model';
import { SERVER_API_URL } from '../../../app.constants';

@Injectable()
export class RoleService {

    constructor(
        private http: HttpClient
    ) {
    }

    getFilteredRoles(roleFilter: RoleFilter): Observable<Role[]> {
        const baseUrl = SERVER_API_URL + '/roles/search';
        return this.http.post<Role[]>(baseUrl, roleFilter);
    }
}
