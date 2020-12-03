import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';
import { ParamContext } from '../../service/param.context';
import { NameType } from '../../germplasm/model/name-type.model';

@Injectable()
export class NameTypeService {
    constructor(private http: HttpClient,
                private context: ParamContext) {

    }

    searchNameTypes(query): Observable<HttpResponse<NameType[]>> {
        return this.http.get<NameType[]>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm/name-types/search?query=` + query, { observe: 'response' });
    }
}
