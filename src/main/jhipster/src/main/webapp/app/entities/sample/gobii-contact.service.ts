import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { SERVER_API_URL } from '../../app.constants';
import { GobiiContact } from './gobii-contact.model';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable()
export class GobiiContactService {

    private resourceUrl = SERVER_API_URL + `/gobii-contacts`;

    constructor(
        private http: HttpClient
    ) {
    }

    getAll(): Observable<GobiiContact[]> {
        return this.http.get<GobiiContact[]>(`${this.resourceUrl}`, { observe: 'response' })
            .pipe(map((res: HttpResponse<GobiiContact[]>) => res.body));
    }

}
