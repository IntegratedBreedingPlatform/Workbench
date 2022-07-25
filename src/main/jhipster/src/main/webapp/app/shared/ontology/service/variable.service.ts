import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { ParamContext } from '../../service/param.context';
import { VariableDetails } from '../model/variable-details';
import { SERVER_API_URL } from '../../../app.constants';
import { Observable } from 'rxjs';
import { VariableFilterRequest } from '../model/variable-filter-request';

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

    filterVariables(request: VariableFilterRequest) {
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
        const url = SERVER_API_URL + `crops/${this.paramContext.cropName}/studies/${studyId}/entries/variables?variableTypeId=${variableTypeId}&programUUID=`
            + this.paramContext.programUUID;
        return this.http.get<VariableDetails[]>(url, { observe: 'response' });
    }
}
