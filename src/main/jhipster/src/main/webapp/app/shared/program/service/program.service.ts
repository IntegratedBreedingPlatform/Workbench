import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { SERVER_API_URL } from '../../../app.constants';
import { Program } from '../model/program';
import { Observable } from 'rxjs';
import { createRequestOption } from '../..';
import { Pageable } from '../../model/pageable';

@Injectable()
export class ProgramService {

    constructor(private http: HttpClient) {
    }

    getPrograms(cropName, query: string, pageable: Pageable): Observable<HttpResponse<Program[]>> {
        const params = Object.assign({}, pageable);
        if (cropName) {
            params['cropName'] = cropName;
        }
        params['programNameContainsString'] = query;
        return this.http.get<Program[]>(SERVER_API_URL + 'programs', {
            params: createRequestOption(params),
            observe: 'response'
        });
    }

    addProgram(programBasicDetails: any, crop: string) {
        const url = SERVER_API_URL + `crops/${crop}/programs`;
        return this.http.post(url, programBasicDetails);
    }

    updateProgram(programBasicDetails: any, crop: string, programUUID: any) {
        const url = SERVER_API_URL + `crops/${crop}/programs/${programUUID}`;
        return this.http.patch(url, programBasicDetails);
    }

    deleteProgram(crop: any, programUUID: any) {
        const url = SERVER_API_URL + `crops/${crop}/programs/${programUUID}`;
        return this.http.delete(url);
    }
}
