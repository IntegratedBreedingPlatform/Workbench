import { Inject, Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable, Subject } from 'rxjs/Rx';
import { User } from './../models/user.model';
import ServiceHelper from './service.helper';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class UserService {
    private baseUrl: string = SERVER_API_URL;

    public onUserAdded = new Subject<User>();
    public onUserUpdated = new Subject<User>();

    /** User been edited or created */
    public user: User;

    private http: Http;

    constructor(@Inject(Http) http: Http) {
        this.http = http;
    }

    getAll(): Observable<User[]> {
        let users$ = this.http
            .get(`${this.baseUrl}/users`, { headers: this.getHeaders() })
            .map(response => this.mapUsers(response));
        return users$;
    }

    get(id: number): Observable<User> {
        let User$ = this.http
            .get(`${this.baseUrl}/users/${id}`, { headers: this.getHeaders() })
            .map(response => this.mapUser(response));
        return User$;
    }

    save(user: User): Observable<Response> {
        let headers = this.getHeaders()
        headers.append('Content-Type', 'application/json');
        return this.http
            .post(`${this.baseUrl}/users`, JSON.stringify(user), { headers: headers });
        ;
    }

    update(user: User): Observable<Response> {
        let headers = this.getHeaders()
        headers.append('Content-Type', 'application/json');
        return this.http
            .put(`${this.baseUrl}/users/${user.id}`, JSON.stringify(user), { headers: headers });
    }

    private getHeaders() {
        return ServiceHelper.getHeaders();
    }

    private mapUsers(response: Response): User[] {
        return response.json().map(this.toUser)
    }

    private toUser(r: any): User {
        let user = <User>({
            id: r.id,
            firstName: r.firstName,
            lastName: r.lastName,
            username: r.username,
            crops: r.crops,
            userRoles: (r.userRoles == null) ? [] : r.userRoles,
            email: r.email,
            active: r.active,
            multiFactorAuthenticationEnabled: r.multiFactorAuthenticationEnabled
        });
        return user;
    }

    private mapUser(response: Response): User {
        return this.toUser(response.json());
    }

}
