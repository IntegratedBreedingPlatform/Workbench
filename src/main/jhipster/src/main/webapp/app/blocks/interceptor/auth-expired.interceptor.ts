import {Injector} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {LoginService} from '../../shared/login/login.service';
import {tap} from 'rxjs/operators';

export class AuthExpiredInterceptor implements HttpInterceptor {

    constructor(
        private injector: Injector
    ) {
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).pipe(
            tap(
                (event: HttpEvent<any>) => {
                    // Handle successful response if needed
                },
                (err: any) => {
                    if (err instanceof HttpErrorResponse) {
                        if (err.status === 401) {
                            const loginService: LoginService = this.injector.get(LoginService);
                            loginService.forceLogout();
                        }
                    }
                }
            )
        );
    }
}
