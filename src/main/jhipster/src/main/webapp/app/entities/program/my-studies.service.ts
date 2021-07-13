import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MyStudy } from './my-study';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../app.constants';
import { createRequestOption } from '../../shared';
import { ParamContext } from '../../shared/service/param.context';
import { Pageable } from '../../shared/model/pageable';

@Injectable()
export class MyStudiesService {
    constructor(
        private http: HttpClient,
        private context: ParamContext
    ) {
    }

    getMyStudies(pageable: Pageable, cropName, programUUID): Observable<HttpResponse<MyStudy[]>> {
        const params = createRequestOption(Object.assign({
            programUUID
        }, pageable));
        return this.http.get<MyStudy[]>(SERVER_API_URL + `crops/${cropName}/my-studies`, { params, observe: 'response' });
    }
}
