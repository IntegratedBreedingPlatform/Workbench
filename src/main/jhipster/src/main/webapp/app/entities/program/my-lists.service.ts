import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MyStudy } from './my-study';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../app.constants';
import { createRequestOption } from '../../shared';
import { ParamContext } from '../../shared/service/param.context';
import { MyList } from './my-list';
import { Pageable } from '../../shared/model/pageable';

@Injectable()
export class MyListsService {
    constructor(
        private http: HttpClient,
        private context: ParamContext
    ) {
    }

    getMyLists(pageable: Pageable, cropName, programUUID): Observable<HttpResponse<MyList[]>> {
        const params = createRequestOption(Object.assign({
            programUUID
        }, pageable));
        return this.http.get<MyStudy[]>(SERVER_API_URL + `crops/${cropName}/germplasm-lists/my-lists`,
            { params, observe: 'response' });
    }
}
