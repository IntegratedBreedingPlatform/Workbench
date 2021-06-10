import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ParamContext } from '../../service/param.context';
import { VariableDetails } from '../model/variable-details';
import { SERVER_API_URL } from '../../../app.constants';
import { Observable } from 'rxjs';

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

    filterVariables(variableIds) {
        const params = {
            programUUID: this.paramContext.programUUID,
            variableIds
        };
        return this.http.get<VariableDetails[]>(SERVER_API_URL + `crops/${this.paramContext.cropName}/variables/filter`, { params });
    }

    getVariableById(variableId: number): Observable<VariableDetails> {
        const params = { programUUID: this.paramContext.programUUID };
        return this.http.get<VariableDetails>(SERVER_API_URL + `crops/${this.paramContext.cropName}/variables/${variableId}`, { params });
    }

}
