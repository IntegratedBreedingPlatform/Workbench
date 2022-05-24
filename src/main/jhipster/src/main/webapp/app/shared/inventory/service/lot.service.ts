import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ParamContext } from '../../service/param.context';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { SERVER_API_URL } from '../../../app.constants';
import { Lot } from '../model/lot.model';
import { createRequestOption } from '../..';
import { LotImportRequest } from '../model/lot-import-request';
import { map } from 'rxjs/operators';
import { LotSearch } from '../model/lot-search.model';

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
        if (this.context.programUUID) {
            request['programUUID'] = this.context.programUUID;
        }

        const url = SERVER_API_URL + `crops/${this.context.cropName}/lots/germplasm/${gid}`;
        const params = createRequestOption(request);
        return this.http.get<Lot[]>(url, { params, observe: 'response' });

    }

    importLotsWithInitialBalance(lotImportRequest: LotImportRequest): Observable<any> {
        return this.http.post<Lot>(SERVER_API_URL + `crops/${this.context.cropName}/lot-lists?programUUID` + this.context.programUUID, lotImportRequest);
    }

    search(req?: LotSearch): Observable<string> {
        let url = SERVER_API_URL + `crops/${this.context.cropName}/lots/search`;
        if (this.context.programUUID) {
            url += `?programUUID=` + this.context.programUUID;
        }
        return this.http.post<any>(url, req, { observe: 'response' })
            .pipe(map((res: any) => res.body.searchResultDbId));
    }

    getSearchResults(req?: any): Observable<HttpResponse<Lot[]>> {
        const options = createRequestOption(req);
        let url = SERVER_API_URL + `crops/${this.context.cropName}/lots/search`;
        if (this.context.programUUID) {
            url += `?programUUID=` + this.context.programUUID;
        }
        return this.http.get<Lot[]>(url, { params: options, observe: 'response' });
    }
}
