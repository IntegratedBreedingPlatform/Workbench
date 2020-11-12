import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';
import { ParamContext } from '../../service/param.context';
import { createRequestOption } from '../..';
import { Germplasm } from '../../../entities/germplasm/germplasm.model';

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

    getAttributes(codes: string[]) {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/attributes` +
            '?programUUID=' + this.context.programUUID + '&codes=' + codes;
        return this.http.get(url, { observe: 'response' });
    }

    getGermplasmNameTypes(codes: string[]) {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/name-types` +
            '?programUUID=' + this.context.programUUID + '&codes=' + codes;
        return this.http.get(url, { observe: 'response' });
    }

}
