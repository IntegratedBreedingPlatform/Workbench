import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ParamContext } from '../../service/param.context';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { SERVER_API_URL } from '../../../app.constants';
import { Lot } from '../model/lot.model';
import { createRequestOption } from '../..';

@Injectable()
export class LotService {

    constructor(private context: ParamContext,
                private http: HttpClient) {
    }

    createLots(lotGeneratorBatchRequest): Observable<string[]> {
        return this.http.post<any>(SERVER_API_URL + `crops/${this.context.cropName}/lots/generation?programUUID=` + this.context.programUUID,
            lotGeneratorBatchRequest);
    }

    getLotsByGId(gid: number, request: any): Observable<HttpResponse<Lot[]>> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/lots/germplasm/${gid}`;
        const options = createRequestOption(request);
        return this.http.get<Lot[]>(url, { params: options, observe: 'response' });

    }
}
