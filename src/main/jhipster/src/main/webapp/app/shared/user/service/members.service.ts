import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserDetail } from '../model/user-detail.model';
import { ParamContext } from '../../service/param.context';
import { SERVER_API_URL } from '../../../app.constants';
import { Pageable } from '../../model/pageable';
import { createRequestOption } from '../../model/request-util';
import { UserSearchRequest } from '../model/user-search-request.model';
import { getAllRecords } from '../../util/get-all-records';
import { ProgramMember } from '../model/program-member.model';

@Injectable()
export class MembersService {
    constructor(
        private http: HttpClient,
        private context: ParamContext
    ) {
    }

    getEligibleUsers(searchRequest: UserSearchRequest, pageable: Pageable): Observable<HttpResponse<UserDetail[]>> {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName + '/programs/' + this.context.programUUID + '/members/eligible-users/search';
        const params = createRequestOption(pageable);
        return this.http.post<UserDetail[]>(baseUrl, searchRequest, { observe: 'response', params });
    }

    getAllEligibleUsers(searchRequest: UserSearchRequest): Observable<UserDetail[]> {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName + '/programs/' + this.context.programUUID + '/members/eligible-users/search';
        return getAllRecords<UserDetail>((page, pageSize) => {
            const params = createRequestOption({ page, size: pageSize });
            return this.http.post<UserDetail[]>(baseUrl, searchRequest, { params, observe: 'response'});
        });
    }

    getMembers(searchRequest: UserSearchRequest, pageable: Pageable): Observable<HttpResponse<ProgramMember[]>> {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName + '/programs/' + this.context.programUUID + '/members/search';
        const params = createRequestOption(pageable);
        return this.http.post<ProgramMember[]>(baseUrl, searchRequest, { observe: 'response', params });
    }

    getAllMembers(searchRequest: UserSearchRequest): Observable<ProgramMember[]> {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName + '/programs/' + this.context.programUUID + '/members/search';
        return getAllRecords<ProgramMember>((page, pageSize) => {
            const params = createRequestOption({ page, size: pageSize });
            return this.http.post<ProgramMember[]>(baseUrl, searchRequest, { params, observe: 'response' });
        });
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
