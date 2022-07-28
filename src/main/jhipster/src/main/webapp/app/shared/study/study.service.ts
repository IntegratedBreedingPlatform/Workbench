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

    downloadImportTemplate(fileNamePrefix: string, isUpdateFormat: boolean = true) {
        let url = SERVER_API_URL + `crops/${this.paramContext.cropName}/germplasm-lists/templates/xls/${isUpdateFormat}?programUUID=${this.paramContext.programUUID}`;
        if (fileNamePrefix) {
            url +=`&fileNamePrefix=${fileNamePrefix}`;
        }

        return this.http.get(url, { observe: 'response', responseType: 'blob' });
    }

    importStudyEntries(studyId: number, data: any[], newVariables: any[]): Observable<any> {
        const params = Object.assign({
            programUUID: this.paramContext.programUUID,
            data: data,
            newVariables: newVariables
        });
        const url = SERVER_API_URL + `crops/${this.paramContext.cropName}/studies/${studyId}/entries/import?programUUID=`
            + this.paramContext.programUUID;
        return this.http.post<StudyEntryDetailsImportRequest>(url, params);
    }
}
