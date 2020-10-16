import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';
import { ParamContext } from '../../service/param.context';
import { Attribute } from '../model/attribute.model';

@Injectable()
export class AttributesService {
    constructor(private http: HttpClient,
                private context: ParamContext) {

    }

    searchAttributes(query): Observable<HttpResponse<Attribute[]>> {
        return this.http.get<Attribute[]>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm/attributes/search?query=` + query, { observe: 'response' });
    }
}
