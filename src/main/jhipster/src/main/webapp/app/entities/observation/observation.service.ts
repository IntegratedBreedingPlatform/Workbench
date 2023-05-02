import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../app.constants';
import { createRequestOption } from '../../shared';
import { ParamContext } from '../../shared/service/param.context';
import { ObservationAudit } from '../../shared/model/observation-audit.model';

@Injectable()
export class ObservationService {
    constructor(
        private http: HttpClient,
        private context: ParamContext
    ) {
    }

    getPhenotypeAuditRecords(studyId: number,
                             datasetId: number,
                             observationUnitId: string, variableId: number,
                             pagination: any): Observable<HttpResponse<ObservationAudit[]>> {
        const params = createRequestOption(pagination);
        const url: string = SERVER_API_URL
            + `crops/${this.context.cropName}/programs/${this.context.programUUID}/observationUnits/${observationUnitId}/variable/${variableId}/changes`;
        return this.http.get<ObservationAudit[]>(url, { params, observe: 'response' });
    }
}
