import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserProfileModel } from '../model/user-profile.model';
import { HttpClient } from '@angular/common/http';
import { SERVER_API_URL } from '../../../app.constants';

@Injectable()
export class UserProfileServices {

    private readonly resourceUrl: string;

    constructor(private http: HttpClient) {
        this.resourceUrl = SERVER_API_URL
    }

    update(userProfile: UserProfileModel): Observable<void> {
        return this.http.patch<void>(this.resourceUrl + `my-profile`, userProfile);
    }

}
