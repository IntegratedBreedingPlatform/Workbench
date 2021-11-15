import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SERVER_API_URL } from '../app.constants';
import { ParamContext } from '../shared/service/param.context';

@Injectable()
export class CopService {
    constructor(
        private http: HttpClient,
        private context: ParamContext
    ) {
    }

    getCopMatrixAs2dArray(gids: number[]) {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        const params = {};
        params['gids'] = gids;
        return this.http.get(baseUrl + '/cop/array?programUUID=' + this.context.programUUID, {params});
    }

}
