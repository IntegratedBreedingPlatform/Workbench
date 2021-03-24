import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SELECT_PROGRAM_URL } from '../../app.constants';
import { Observable } from 'rxjs';
@Injectable()
export class SelectProgramService {

    constructor(@Inject(HttpClient) private http: HttpClient) {
    }

    setSelectedProgram(programUUID: string): Observable<any> {
        return this.http.post(SELECT_PROGRAM_URL, programUUID, {responseType: 'text'});
    }
}
