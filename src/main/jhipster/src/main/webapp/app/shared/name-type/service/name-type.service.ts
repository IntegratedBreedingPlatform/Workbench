import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';
import { ParamContext } from '../../service/param.context';
import { NameType, NameTypeDetails, NameTypeMetaData } from '../../germplasm/model/name-type.model';
import { createRequestOption } from '../../model/request-util';

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

    searchNameTypes(nameTypeMetadataFilterRequest?: any, req?: any): Observable<HttpResponse<NameTypeDetails[]>> {
        const options = createRequestOption(req);
        const url = SERVER_API_URL + `crops/${this.context.cropName}/name-types/search?programUUID=` + this.context.programUUID;
        return this.http.post<NameTypeDetails[]>(url, nameTypeMetadataFilterRequest, { params: options, observe: 'response' });
    }

    getMetadata(nameTypeId: number): Observable<NameTypeMetaData> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/name-types/${nameTypeId}/metadata?programUUID=` + this.context.programUUID;
        return this.http.get(url);
    }

}
