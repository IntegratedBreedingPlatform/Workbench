import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { UserDetail } from '../model/user-detail.model';
import { ParamContext } from '../../service/param.context';
import { SERVER_API_URL } from '../../../app.constants';
import { Pageable } from '../../model/pageable';
import { createRequestOption } from '../../model/request-util';

@Injectable()
export class MembersService {
    constructor(
        private http: HttpClient,
        private context: ParamContext
    ) {
    }

    getMembersEligibleUsers(pageable: Pageable): Observable<HttpResponse<UserDetail[]>> {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName + '/programs/' + this.context.programUUID + '/members/eligible-users';
        const params = createRequestOption(pageable);
        return this.http.get<UserDetail[]>(baseUrl, { observe: 'response', params });
    }

    getMembers(pageable: Pageable): Observable<HttpResponse<UserDetail[]>> {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName + '/programs/' + this.context.programUUID + '/members';
        const params = createRequestOption(pageable);
        return this.http.get<UserDetail[]>(baseUrl, { observe: 'response', params });
    }

    removeMembers(userIds: number[]) {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName + '/programs/' + this.context.programUUID + '/members';
        const params = {};
        params['userIds'] = userIds;
        return this.http.delete<UserDetail[]>(baseUrl, { observe: 'response', params });
    }

    addProgramRoleToUsers(roleId: number, userIds: number[]) {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName + '/programs/' + this.context.programUUID + '/members';
        return this.http.post<UserDetail[]>(baseUrl, { roleId, userIds }, { observe: 'response' });
    }
}
