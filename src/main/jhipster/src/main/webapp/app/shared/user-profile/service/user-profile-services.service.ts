import { Injectable } from '@angular/core';
import { SERVER_API_URL } from '../../../../../../../../web/src/appsNg2/admin/app/app.constants';
import ServiceHelper from '../../../../../../../../web/src/appsNg2/admin/app/shared/services/service.helper';
import { Observable } from 'rxjs/Observable';
import { UserProfileModel } from '../model/user-profile.model';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class UserProfileServices {
    private baseUrl: string = SERVER_API_URL;

    private resourceUrl;

    constructor(private http: HttpClient) {
    }

    update(userProfile: UserProfileModel, userId: number): Observable<void> {
        const resourceUrl = `users/${userId}/profile`;
        return this.http.put<void>(this.baseUrl + resourceUrl, userProfile);
    }

    private getHeaders() {
        return ServiceHelper.getHeaders();
    }
}
