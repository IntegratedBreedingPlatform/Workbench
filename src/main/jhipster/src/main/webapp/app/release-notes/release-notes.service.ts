import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../app.constants';
import { ReleaseNotes } from './release-notes.model';

@Injectable()
export class ReleaseNotesService {

    constructor(private http: HttpClient) {
    }

    getLatest(): Observable<HttpResponse<ReleaseNotes>> {
        return this.http.get<ReleaseNotes>(SERVER_API_URL + `release-notes/latest`, { observe: 'response' });
    }

}
