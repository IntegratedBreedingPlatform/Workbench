import { Injectable } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';
import { Principal } from '../auth/principal.service';

@Injectable()
export class LoginService {

    constructor(
        private languageService: JhiLanguageService,
        private principal: Principal,
    ) {
    }

    logout() {
        alert('Site Admin needs  to authenticate you again. Redirecting to login page.');
        window.top.location.href = '/ibpworkbench/logout';
    }
}
