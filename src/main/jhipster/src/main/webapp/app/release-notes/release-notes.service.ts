import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../app.constants';
import { ReleaseNotes } from './release-notes.model';

@Injectable()
export class ReleaseNotesService {

    private readonly resourceUrl: string;

    constructor(private http: HttpClient) {
        this.resourceUrl = SERVER_API_URL + 'release-notes';
    }

    getLatest(): Observable<HttpResponse<ReleaseNotes>> {
        return this.http.get<ReleaseNotes>(this.resourceUrl + `/latest`, { observe: 'response' });
    }

    dontShowAgain(): Observable<void> {
       return this.http.put<void>(this.resourceUrl + '/dont-show-again', { observe: 'response' });
    }

}
