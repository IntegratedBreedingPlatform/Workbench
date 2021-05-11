import { Inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { SERVER_API_URL } from '../../app.constants';
import { Observable } from 'rxjs';
import { Program } from '../program/model/program';

@Injectable()
export class ProgramUsageService {

    constructor(@Inject(HttpClient) private http: HttpClient) {
    }

    getLastestSelectedProgram(userId?: number): Observable<HttpResponse<Program>> {
        return this.http.get<Program>(SERVER_API_URL + `program-usage/last?userId=${userId}`, {
            observe: 'response'
        });
    }

    save(programUsage: any): Observable<any> {
        return this.http.post<any>(SERVER_API_URL + `program-usage`, programUsage, {
            observe: 'response'
        });
    }
}
