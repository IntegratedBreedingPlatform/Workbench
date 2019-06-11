import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../app.constants';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class AccountService  {
    constructor(private http: HttpClient) { }

    get(): Observable<Account> {
        return this.http.get<Account>(SERVER_API_URL + '/account');
    }
}
