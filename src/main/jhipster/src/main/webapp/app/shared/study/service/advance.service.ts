import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ParamContext } from '../../service/param.context';
import { SERVER_API_URL } from '../../../app.constants';
import { Observable } from 'rxjs';
import { AdvanceStudyRequest } from '../model/advance-study-request.model';
import { AdvanceSamplesRequest } from '../model/advance-sample-request.model';
import { AdvancedGermplasmPreview } from '../model/advanced-germplasm-preview';

@Injectable()
export class AdvanceService {

    constructor(
        private http: HttpClient,
        private context: ParamContext
    ) {
    }

    advanceStudy(studyId: number, request: AdvanceStudyRequest): Observable<number[]> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/programs/${this.context.programUUID}/studies/${studyId}/advance`;
        return this.http.post<number[]>(url, request);
    }

    advanceStudyPreview(studyId: number, request: AdvanceStudyRequest): Observable<AdvancedGermplasmPreview[]> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/programs/${this.context.programUUID}/studies/${studyId}/advance-preview`;
        return this.http.post<AdvancedGermplasmPreview[]>(url, request);
    }

    advanceSamples(studyId: number, request: AdvanceSamplesRequest): Observable<number[]> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/programs/${this.context.programUUID}/studies/${studyId}/advance/samples`;
        return this.http.post<number[]>(url, request);
    }

}
