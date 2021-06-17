import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../app.constants';
import { GermplasmNameChange } from './germplasm-name-changes.model';
import { ParamContext } from '../../shared/service/param.context';
import { createRequestOption } from '../../shared';

@Injectable()
export class GermplasmChangesService {

    constructor(private http: HttpClient,
                private context: ParamContext) {
    }

    getNamesChanges(gid: number, nameId: number, request: any): Observable<HttpResponse<GermplasmNameChange[]>> {
        const params = createRequestOption(request);
        return this.http.get<GermplasmNameChange[]>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/name/${nameId}/changes`,
            { params, observe: 'response' });
    }

}