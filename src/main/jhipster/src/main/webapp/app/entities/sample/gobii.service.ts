import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { SERVER_API_URL } from '../../app.constants';
import { GobiiContact } from './gobii-contact.model';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable()
export class GobiiService {

    private contactsResourceUrl = SERVER_API_URL + `/gobii-contacts`;

    private gobiiSubmissionStatusResourceUrl = SERVER_API_URL + `/gobii-submission-status`;

    constructor(
        private http: HttpClient
    ) {
    }

    getAllContacts(): Observable<GobiiContact[]> {
        return this.http.get<GobiiContact[]>(`${this.contactsResourceUrl}`, { observe: 'response' })
            .pipe(map((res: HttpResponse<GobiiContact[]>) => res.body));
    }

    getGobiiSubmissionStatus(): Observable<boolean> {
        return this.http.get<boolean>(`${this.gobiiSubmissionStatusResourceUrl}`, { observe: 'response' })
            .pipe(map((res: HttpResponse<boolean>) => res.body));
    }

}
