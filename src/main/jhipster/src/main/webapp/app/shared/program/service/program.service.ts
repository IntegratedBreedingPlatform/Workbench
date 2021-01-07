import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SERVER_API_URL } from '../../../app.constants';
import { Program } from '../model/program';
import { Observable } from 'rxjs';

@Injectable()
export class ProgramService {

    constructor(private http: HttpClient) {
    }

    getPrograms(): Observable<Program[]> {
        return this.http.get<Program[]>(SERVER_API_URL + 'programs');
    }
}
