import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { USER_PROGRAM_INFO } from '../../app.constants';
import { Observable } from 'rxjs';
@Injectable()
export class UserProgramInfoService {

    constructor(@Inject(HttpClient) private http: HttpClient) {
    }

    setSelectedProgram(programUUID: string): Observable<any> {
        return this.http.post(USER_PROGRAM_INFO, programUUID, {responseType: 'text'});
    }
}
