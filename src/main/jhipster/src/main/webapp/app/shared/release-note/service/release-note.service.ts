import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';
import { ReleaseNote } from '../model/release-note.model';

@Injectable()
export class ReleaseNoteService {
    constructor(private http: HttpClient) {
    }

    getLatest(): Observable<HttpResponse<ReleaseNote>> {
        return this.http.get<ReleaseNote>(SERVER_API_URL + `release-notes/latest`, { observe: 'response' });
    }

}
