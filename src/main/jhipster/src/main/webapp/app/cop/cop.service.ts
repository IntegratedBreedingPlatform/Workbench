import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SERVER_API_URL } from '../app.constants';
import { ParamContext } from '../shared/service/param.context';
import { CopResponse } from './cop.model';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class CopService {
    constructor(
        private http: HttpClient,
        private context: ParamContext
    ) {
    }

    calculateCop(gids: number[]): Observable<CopResponse> {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        return this.http.post<CopResponse>(baseUrl + '/cop/calculation?programUUID=' + this.context.programUUID, gids);
    }

    cancelJobs(gids: number[]) {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        const params = {};
        params['gids'] = gids;
        return this.http.delete(baseUrl + '/cop/calculation?programUUID=' + this.context.programUUID, { params });
    }

    getCop(gids: number[]): Observable<CopResponse> {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        const params = {};
        params['gids'] = gids;
        return this.http.get<CopResponse>(baseUrl + '/cop?programUUID=' + this.context.programUUID, { params });
    }

}
