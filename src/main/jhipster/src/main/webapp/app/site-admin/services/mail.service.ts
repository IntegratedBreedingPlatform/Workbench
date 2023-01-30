import { Injectable, Inject } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import { User } from '../../shared/user/model/user.model';
import { Observable } from 'rxjs/Rx';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class MailService {
    private baseUrl = '/ibpworkbench/controller/auth'; //sendResetEmail/{username}

    constructor(private http: HttpClient) {
    }

    send(user: User): Observable<Response> {
        return this.http
            .post<Response>(`${this.baseUrl}/sendResetEmail/${user.id}`, { observe: 'response'  });
    }
}
