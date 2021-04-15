import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MyStudy } from './my-study';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../app.constants';
import { createRequestOption } from '../../shared';
import { ParamContext } from '../../shared/service/param.context';

@Injectable()
export class MyStudiesService {
    constructor(
        private http: HttpClient,
        private context: ParamContext
    ) {
    }

    getMyStudies(page, pageSize, cropName, programUUID): Observable<HttpResponse<MyStudy[]>> {
        const params = createRequestOption({
            page,
            size: pageSize,
            programUUID
        });
        return this.http.get<MyStudy[]>(SERVER_API_URL + `crops/${cropName}/my-studies`, { params, observe: 'response' });
    }
}
