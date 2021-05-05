import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../app.constants';
import { HttpClient } from '@angular/common/http';

declare const cropName: string;
declare const currentProgramId: string;

@Injectable()
export class AccountService  {
    constructor(private http: HttpClient) { }

    get(cropNameParam?, programUUIDParam?): Observable<Account> {
        return this.http.get<Account>(SERVER_API_URL + '/account', {
            params: {
                cropName: cropNameParam || cropName,
                programUUID: programUUIDParam || currentProgramId
            }
        });
    }
}
