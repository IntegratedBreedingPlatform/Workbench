import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable, Subject } from 'rxjs/Rx';
import { User } from '../../shared/user/model/user.model';
import { SERVER_API_URL } from '../../app.constants';
import { createRequestOption } from '../../shared';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { Location } from '../../shared/location/model/location';

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
            // .map((response) => this.mapUsers(response));
        // return users$;
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

    save(user: User): Observable<Response> {
        return null;
/*        const headers = this.getHeaders()
        headers.append('Content-Type', 'application/json');
        return this.http
            .post(`${this.baseUrl}/users`, JSON.stringify(user), { headers: headers });
        ;*/
    }

    update(user: User): Observable<HttpResponse<number>> {
        return this.http.put<number>(`${this.baseUrl}/users/${user.id}`, user, { observe: 'response' });
    }

/*    private getHeaders() {
        return ServiceHelper.getHeaders();
    }*/

   /* private mapUsers(response: Response): User[] {
        return response.json().map(this.toUser)
    }
*/
/*    private toUser(r: any): User {
        const user = <User>({
            id: r.id,
            firstName: r.firstName,
            lastName: r.lastName,
            username: r.username,
            crops: r.crops,
            userRoles: (r.userRoles == null) ? [] : r.userRoles,
            email: r.email,
            status: r.status,
            multiFactorAuthenticationEnabled: r.multiFactorAuthenticationEnabled
        });
        return user;
    }*/

/*    private mapUser(response: Response): User {
        return this.toUser(response.json());
    }*/

}
