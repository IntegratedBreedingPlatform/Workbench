import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MyStudy } from './my-study';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../app.constants';
import { createRequestOption } from '../../shared';
import { ParamContext } from '../../shared/service/param.context';
import { MyList } from './my-list';

@Injectable()
export class MyListsService {
    constructor(
        private http: HttpClient,
        private context: ParamContext
    ) {
    }

    getMyLists(page, pageSize, cropName, programUUID): Observable<HttpResponse<MyList[]>> {
        const params = createRequestOption({
            page,
            size: pageSize,
            programUUID
        });
        return this.http.get<MyStudy[]>(SERVER_API_URL + `crops/${cropName}/germplasm-lists/my-lists`,
            { params, observe: 'response' });
    }
}
