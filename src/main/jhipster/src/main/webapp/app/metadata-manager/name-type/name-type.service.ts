import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../app.constants';
import { ParamContext } from '../../shared/service/param.context';
import { NameType, NameTypeDetails } from '../../shared/germplasm/model/name-type.model';
import { createRequestOption } from '../../shared/model/request-util';

@Injectable()
export class NameTypeService {
    constructor(private http: HttpClient,
                private context: ParamContext) {

    }

    createNameType(nameType: NameType) {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/name-types?programUUID=` + this.context.programUUID;
        return this.http.post(url, nameType);
    }

    updateNameType(nameType: NameType, nameTypeId: number) {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/name-types/${nameTypeId}?programUUID=` + this.context.programUUID;
        return this.http.patch(url, nameType);
    }

    deleteNameType(nameTypeId: number) {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/name-types/${nameTypeId}?programUUID=` + this.context.programUUID;
        return this.http.delete(url);
    }

    getNameTypes(req?: any): Observable<HttpResponse<NameTypeDetails[]>> {
        const options = createRequestOption(req);
        const url = SERVER_API_URL + `crops/${this.context.cropName}/name-types?programUUID=` + this.context.programUUID;
        return this.http.get<NameTypeDetails[]>(url, { params: options, observe: 'response' });
    }

}
