import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { ParamContext } from '../../service/param.context';
import { SERVER_API_URL } from '../../../app.constants';
import { Observable } from 'rxjs';
import { createRequestOption } from '../..';
import { StudySearchRequest } from '../model/study-search-request.model';
import { StudySearchResponse } from '../model/study-search-response.model';
import { StudyDetails } from '../model/study-details.model';

@Injectable()
export class StudyService {

    constructor(
        private http: HttpClient,
        private paramContext: ParamContext
    ) {
    }

    downloadImportTemplate(fileNamePrefix: string, isUpdateFormat= true) {
        let url = SERVER_API_URL + `crops/${this.paramContext.cropName}/germplasm-lists/templates/xls/${isUpdateFormat}?programUUID=${this.paramContext.programUUID}`;
        if (fileNamePrefix) {
            url += `&fileNamePrefix=${fileNamePrefix}`;
        }

        return this.http.get(url, { observe: 'response', responseType: 'blob' });
    }

    importStudyEntries(studyId: number, data: any[], newVariables: any[]): Observable<any> {
        const params: any = {data, newVariables};
        const url = SERVER_API_URL
            + `crops/${this.paramContext.cropName}/programs/${this.paramContext.programUUID}/studies/${studyId}/entries/import`;
        return this.http.post(url, params);
    }

    searchStudies(req: StudySearchRequest, pagination: any): Observable<HttpResponse<StudySearchResponse[]>> {
        const params = createRequestOption(pagination);
        const url = SERVER_API_URL + `crops/${this.paramContext.cropName}/programs/${this.paramContext.programUUID}/studies/search`;
        return this.http.post<StudySearchResponse[]>(url, req, { params, observe: 'response' });
    }

    getStudyDetails(studyId: number): Observable<HttpResponse<StudyDetails>> {
        const url = SERVER_API_URL + `crops/${this.paramContext.cropName}/programs/${this.paramContext.programUUID}/studies/${studyId}/details`;
        return this.http.get<StudyDetails>(url, { observe: 'response' });
    }

}
