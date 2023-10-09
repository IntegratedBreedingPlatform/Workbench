import {Observable} from 'rxjs/Observable';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {CrossPlanSearchResponse} from '../model/cross-plan-search-response.model';
import {ParamContext} from '../../service/param.context';
import {createRequestOption} from '../../model/request-util';
import {SERVER_API_URL} from '../../../app.constants';
import {CrossPlanSearchRequest} from '../model/cross-plan-search-request.model';
import {Injectable} from '@angular/core';

@Injectable()

export class CrossPlanService {

    constructor(private http: HttpClient,
                private paramContext: ParamContext) {
    }
    searchCrossPlan(req: CrossPlanSearchRequest, pagination:any): Observable<HttpResponse<CrossPlanSearchResponse[]>> {
        const params = createRequestOption(pagination);
        const url = SERVER_API_URL + `crops/${this.paramContext.cropName}/programs/${this.paramContext.programUUID}/cross-plan/search`;
        return this.http.post<CrossPlanSearchResponse[]>(url, req, { params, observe: 'response' });
    }
}