import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ParamContext } from '../../service/param.context';
import { SERVER_API_URL } from '../../../app.constants';
import { CropParameter } from '../model/crop-parameter';
import { CropParameterTypeEnum } from '../model/crop-parameter-type-enum';
import { Observable } from 'rxjs';

@Injectable()
export class CropParameterService {
    constructor(
        private http: HttpClient,
        private context: ParamContext
    ) {
    }

    getCropParameter(cropName: string, programUUID: string, key: CropParameterTypeEnum): Observable<CropParameter> {
        const baseUrl = SERVER_API_URL + 'crops/' + cropName;
        return this.http.get<CropParameter>(baseUrl + `/crop-parameters/${key}?programUUID=${programUUID}`);
    }

    getCropParameters() {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        return this.http.get<CropParameter[]>(baseUrl + `/crop-parameters?programUUID=${this.context.programUUID}`);
    }

    modifyCropParameters(key, value) {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        return this.http.patch<CropParameter[]>(baseUrl + `/crop-parameters/${key}?programUUID=${this.context.programUUID}`, value);
    }

    getByGroupName(groupName: string): Observable<CropParameter[]> {
        return this.http.get<CropParameter[]>(SERVER_API_URL + `crops/${this.context.cropName}/crop-parameters/${groupName}/group`);
    }

    getGenotypingToken(groupName: string): Observable<string> {
        const headers = new HttpHeaders().set('Content-Type', 'text/plain; charset=utf-8');
        return this.http.get<string>(SERVER_API_URL + `crops/${this.context.cropName}/crop-parameters/genotyping/${groupName}/token`, { headers, responseType: 'text' as 'json' });
    }
}
