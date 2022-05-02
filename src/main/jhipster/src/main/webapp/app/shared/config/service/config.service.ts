import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ParamContext } from '../../service/param.context';
import { SERVER_API_URL } from '../../../app.constants';
import { Config } from '../model/config';

@Injectable()
export class ConfigService {
    constructor(
        private http: HttpClient,
        private context: ParamContext
    ) {
    }

    getConfig() {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        return this.http.get<Config[]>(baseUrl + '/config?programUUID=' + this.context.programUUID);
    }

    modifyConfig(key, value) {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        return this.http.patch<Config[]>(baseUrl + `/config/${key}?programUUID=` + this.context.programUUID, {value});
    }
}
