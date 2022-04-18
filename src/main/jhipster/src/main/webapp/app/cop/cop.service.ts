import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SERVER_API_URL } from '../app.constants';
import { ParamContext } from '../shared/service/param.context';
import { BTypeEnum, CopResponse } from './cop.model';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class CopService {
    constructor(
        private http: HttpClient,
        private context: ParamContext
    ) {
    }

    calculateCop(gids: number[], btype: BTypeEnum, reset = false): Observable<CopResponse> {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        const params = '&btype=' + btype + '&reset=' + reset;
        return this.http.post<CopResponse>(baseUrl + '/cop/calculation?programUUID=' + this.context.programUUID + params, gids);
    }

    calculateCopForList(listId: number, btype: BTypeEnum): Observable<CopResponse> {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        const btypeParam = '&btype=' + btype;
        return this.http.post<CopResponse>(baseUrl + `/cop/calculation/list/${listId}?programUUID=` + this.context.programUUID + btypeParam, null);
    }

    cancelJobs(gids: number[], listId: number) {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        const params = {};
        if (gids) {
            params['gids'] = gids;
        } else if (listId) {
            params['listId'] = listId;
        }
        return this.http.delete(baseUrl + '/cop/calculation?programUUID=' + this.context.programUUID, { params });
    }

    getCop(gids: number[], listId: number): Observable<CopResponse> {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        const params = {};
        if (gids) {
            params['gids'] = gids;
        } else if (listId) {
            params['listId'] = listId;
        }
        return this.http.get<CopResponse>(baseUrl + '/cop?programUUID=' + this.context.programUUID, {
            params
        });
    }

    downloadForList(listId: number) {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        return this.http.get(baseUrl + `/cop/csv/list/${listId}?programUUID=` + this.context.programUUID, {
            observe: 'response',
            responseType: 'blob'
        });
    }

    downloadMatrix(gids: number[]) {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        const params = {};
        params['gids'] = gids;
        return this.http.get(baseUrl + `/cop/csv?programUUID=` + this.context.programUUID, {
            observe: 'response',
            responseType: 'blob',
            params
        });
    }
}
