import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ParamContext } from '../../service/param.context';
import { SERVER_API_URL } from '../../../app.constants';
import { CropParameter } from '../model/crop-parameter';

@Injectable()
export class CropParameterService {
    constructor(
        private http: HttpClient,
        private context: ParamContext
    ) {
    }

    getCropParameters() {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        return this.http.get<CropParameter[]>(baseUrl + '/crop-parameters?programUUID=' + this.context.programUUID);
    }

    modifyCropParameters(key, value) {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        return this.http.patch<CropParameter[]>(baseUrl + `/crop-parameters/${key}?programUUID=` + this.context.programUUID, {value});
    }
}
