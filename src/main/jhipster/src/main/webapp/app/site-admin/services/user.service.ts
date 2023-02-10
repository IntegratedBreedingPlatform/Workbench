import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs/Rx';
import { User } from '../../shared/user/model/user.model';
import { SERVER_API_URL } from '../../app.constants';
import { createRequestOption } from '../../shared';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map } from 'rxjs/operators';

@Injectable()
export class UserService {
    private baseUrl: string = SERVER_API_URL;

    public onUserAdded = new Subject<User>();
    public onUserUpdated = new Subject<User>();

    /** User been edited or created */
    public user: User;

    constructor(private http: HttpClient) {
    }

    getAll(): Observable<User[]> {
        return this.http
            .get(`${this.baseUrl}/users`, { observe: 'response' }).pipe(map((res: HttpResponse<User[]>) => res.body));
    }

    searchUsers(request: any, pagination: any): Observable<HttpResponse<User[]>> {
        const params = createRequestOption(pagination);
        const url = `${this.baseUrl}/users/search`;
        return this.http.post<User[]>(url, request, { params, observe: 'response' });
    }

    get(id: number): Observable<User> {
        return this.http
            .get(`${this.baseUrl}/users/${id}`, { observe: 'response' }).pipe(map((res: HttpResponse<User>) => res.body));
    }

    save(user: User) {
        return this.http.post(`${this.baseUrl}/users`, user);
    }

    update(user: User): Observable<HttpResponse<number>> {
        return this.http.put<number>(`${this.baseUrl}/users/${user.id}`, user, { observe: 'response' });
    }

}
