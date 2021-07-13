import { Injectable } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';
import { Principal } from '../auth/principal.service';
import { SERVER_API_URL } from '../../app.constants';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class LoginService {

    constructor(
        private languageService: JhiLanguageService,
        private principal: Principal,
        private http: HttpClient
    ) {
    }

    forceLogout() {
        alert('Site Admin needs  to authenticate you again. Redirecting to login page.');
        this.logout();
    }

    logout() {
        window.top.location.href = '/ibpworkbench/logout';
    }

    validateToken() {
        const url = SERVER_API_URL + `validateToken`;
        return this.http.get<void>(url);
    }

}
