import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../app.constants';
import { ReleaseNote } from './release-notes.model';

@Injectable()
export class ReleaseNotesService {

    private readonly resourceUrl: string;

    constructor(private http: HttpClient) {
        this.resourceUrl = SERVER_API_URL + 'release-notes';
    }

    getLatest(): Observable<HttpResponse<ReleaseNote>> {
        return this.http.get<ReleaseNote>(this.resourceUrl + `/latest`, { observe: 'response' });
    }

    getContent(version: string): Observable<HttpEvent<string>> {
        const options: any = {responseType: 'text'};
        return this.http.get<string>(`/ibpworkbench/main/app/content/release-notes/${version}.html`, options);
    }

    dontShowAgain(): Observable<void> {
       return this.http.put<void>(this.resourceUrl + '/dont-show-again', {});
    }

}
