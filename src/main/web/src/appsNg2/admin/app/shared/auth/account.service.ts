import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../app.constants';
import { Http } from '@angular/http';
import ServiceHelper from '../services/service.helper';

@Injectable()
export class AccountService  {
    constructor(private http: Http) { }

    get(): Observable<Account> {
        return this.http.get(SERVER_API_URL + '/account', { headers: ServiceHelper.getHeaders() })
            .map((response: any) => response.json());
    }
}
