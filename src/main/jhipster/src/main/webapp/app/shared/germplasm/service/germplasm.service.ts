import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';
import { ParamContext } from '../../service/param.context';
import { createRequestOption } from '../..';
import { Germplasm } from '../../../entities/germplasm/germplasm.model';
import { GermplasmNameTypeModel } from '../../../entities/germplasm/germplasm-name-type.model';
import { GermplasmAttributeModel } from '../../../entities/germplasm/germplasm-attribute.model';

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

    downloadGermplasmTemplate(updateFormat?: boolean): Observable<HttpResponse<Blob>> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/templates/xls?programUUID=` + this.context.programUUID + `&updateFormat=` + updateFormat;
        return this.http.get(url, { observe: 'response', responseType: 'blob' });
    }

    importGermplasmUpdates(germplasmUpdates: any): Observable<HttpResponse<Germplasm[]>> {
        return this.http.patch<any>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm?programUUID=` + this.context.programUUID,
            germplasmUpdates, { observe: 'response' });
    }

    getGermplasmNameTypes(codes: string[]): Observable<GermplasmNameTypeModel[]> {
        return this.http.get<GermplasmNameTypeModel[]>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm/name-types?codes=` + codes.join(','));
    }

    getGermplasmAttributes(codes: string[]): Observable<GermplasmAttributeModel[]> {
        return this.http.get<GermplasmAttributeModel[]>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm/attributes?codes=` + codes.join(','));
    }

    getGermplasmById(gid: number): Observable<HttpResponse<Germplasm>> {
        const params = {};
        if (this.context.programUUID) {
            params['programUUID'] = this.context.programUUID
        }
        return this.http.get<Germplasm>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}`,
            { params, observe: 'response' });
    }
}
