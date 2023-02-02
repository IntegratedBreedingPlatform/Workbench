import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { ParamContext } from '../../service/param.context';
import { VariableDetails } from '../model/variable-details';
import { SERVER_API_URL } from '../../../app.constants';
import { Observable } from 'rxjs';
import { VariableFilterRequest } from '../model/variable-filter-request';
import { VariableSearchRequest } from '../model/variable-search-request.model';
import { Variable } from '../model/variable';

@Injectable()
export class VariableService {

    constructor(
        private http: HttpClient,
        private paramContext: ParamContext
    ) {
    }

    getVariables() {
        const params = { programUUID: this.paramContext.programUUID };
        return this.http.get<VariableDetails[]>(SERVER_API_URL + `crops/${this.paramContext.cropName}/variables`, { params });
    }

    /**
     * @deprecated Please, instead use {@link searchVariables}
     */
    filterVariables(request: VariableFilterRequest): Observable<VariableDetails[]> {
        const params = Object.assign({
            programUUID: this.paramContext.programUUID,
        }, request);
        return this.http.get<VariableDetails[]>(SERVER_API_URL + `crops/${this.paramContext.cropName}/variables/filter`, { params });
    }

    getVariableById(variableId: number): Observable<VariableDetails> {
        const params = { programUUID: this.paramContext.programUUID };
        return this.http.get<VariableDetails>(SERVER_API_URL + `crops/${this.paramContext.cropName}/variables/${variableId}`, { params });
    }

    getStudyEntryVariables(studyId: number, variableTypeId: number): Observable<HttpResponse<VariableDetails[]>> {
        const url = SERVER_API_URL
            + `crops/${this.paramContext.cropName}/programs/${this.paramContext.programUUID}/studies/${studyId}/entries/variables?variableTypeId=${variableTypeId}`;
        return this.http.get<VariableDetails[]>(url, { observe: 'response' });
    }

    searchVariables(req: VariableSearchRequest): Observable<HttpResponse<Variable[]>> {
        const url = SERVER_API_URL + `crops/${this.paramContext.cropName}/programs/${this.paramContext.programUUID}/variables/search`;
        return this.http.post<Variable[]>(url, req, { observe: 'response' });
    }

}
