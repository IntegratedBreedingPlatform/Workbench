import { Observable } from 'rxjs/Observable';
// import { LocalStorageService, SessionStorageService } from 'ngx-webstorage';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { SERVER_API_URL } from '../../app.constants';

export class AuthInterceptor implements HttpInterceptor {

    constructor(
        // private localStorage: LocalStorageService,
        // private sessionStorage: SessionStorageService
    ) {
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        if (!request || !request.url || (/^http/.test(request.url) && !(SERVER_API_URL && request.url.startsWith(SERVER_API_URL)))) {
            return next.handle(request);
        }

        // FIXME localStorage is null
        // const xAuthToken = this.localStorage.retrieve('bms.xAuthToken') || this.sessionStorage.retrieve('bms.xAuthToken');
        const token = JSON.parse(localStorage['bms.xAuthToken']).token;
        if (!!token) {
            request = request.clone({
                setHeaders: {
                    'X-Auth-Token': token
                }
            });
        }
        return next.handle(request);
    }

}
