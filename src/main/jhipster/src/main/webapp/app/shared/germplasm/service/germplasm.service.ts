import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';
import { ParamContext } from '../../service/param.context';
import { createRequestOption } from '../..';
import { Germplasm } from '../../../entities/germplasm/germplasm.model';
import { Attribute } from '../../attributes/model/attribute.model';
import { NameType } from '../model/name-type.model';
import { ExtendedGermplasmImportRequest } from '../model/germplasm-import-request.model';

@Injectable()
export class GermplasmService {
    constructor(private http: HttpClient,
                private context: ParamContext) {

    }

    searchGermplasm(germplasmSearchRequest, pageable): Observable<HttpResponse<Germplasm[]>> {
        const options = createRequestOption(pageable);
        return this.http.post<Germplasm[]>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm/search?programUUID=` + this.context.programUUID,
            germplasmSearchRequest, { params: options, observe: 'response' });
    }

    downloadGermplasmTemplate(): Observable<HttpResponse<Blob>> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/templates/xls?programUUID=` + this.context.programUUID;
        return this.http.get(url, { observe: 'response', responseType: 'blob' });
    }

    getGermplasmAttributes(codes: string[]): Observable<Attribute[]> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/attributes` +
            '?programUUID=' + this.context.programUUID + '&codes=' + codes;
        return this.http.get<Attribute[]>(url);
    }

    getGermplasmNameTypes(codes: string[]): Observable<NameType[]> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/name-types` +
            '?programUUID=' + this.context.programUUID + '&codes=' + codes;
        return this.http.get<NameType[]>(url);
    }

    validateImportGermplasmData(data: ExtendedGermplasmImportRequest[]) {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/validation` +
            '?programUUID=' + this.context.programUUID;
        return this.http.post(url, data);
    }

}
