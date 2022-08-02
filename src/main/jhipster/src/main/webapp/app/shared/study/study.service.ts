import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ParamContext } from '../service/param.context';
import { SERVER_API_URL } from '../../app.constants';
import { Observable } from 'rxjs';
import { StudyEntryDetailsImportRequest } from './study-entry-details-import-request';

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
        const params = Object.assign({
            data,
            newVariables
        });
        const url = SERVER_API_URL
            + `crops/${this.paramContext.cropName}/programs/${this.paramContext.programUUID}/studies/${studyId}/entries/import`;
        return this.http.post<StudyEntryDetailsImportRequest>(url, params);
    }
}
