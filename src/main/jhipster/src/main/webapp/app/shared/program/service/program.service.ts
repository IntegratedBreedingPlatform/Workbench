import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { SERVER_API_URL } from '../../../app.constants';
import { Program } from '../model/program';
import { Observable } from 'rxjs';
import { createRequestOption } from '../..';

@Injectable()
export class ProgramService {

    constructor(private http: HttpClient) {
    }

    getPrograms(pageable): Observable<HttpResponse<Program[]>> {
        return this.http.get<Program[]>(SERVER_API_URL + 'programs', {
            params: createRequestOption(pageable),
            observe: 'response'
        });
    }
}
