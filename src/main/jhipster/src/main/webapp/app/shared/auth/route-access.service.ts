import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { Principal } from './principal.service';
import { AccountService } from './account.service';
import { ParamContext } from '../service/param.context';

@Injectable()
export class RouteAccessService implements CanActivate {

    constructor(private router: Router,
                private principal: Principal,
                private paramContext: ParamContext) {
    }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | Promise<boolean> {
        const authorities = route.data['authorities'];
        if (!this.paramContext.cropName) {
            this.paramContext.cropName = route.queryParams.cropName;
        }

        if (!this.paramContext.programUUID) {
            this.paramContext.programUUID = route.queryParams.programUUID;
        }

        // We need to call the checkLogin / and so the principal.identity() function, to ensure,
        // that the client has a principal too, if they already logged in by the server.
        // This could happen on a page refresh.
        return this.checkLogin(authorities, state.url);
    }

    checkLogin(authorities: string[], url: string): Promise<boolean> {
        const principal = this.principal;
        return Promise.resolve(principal.identity(true).then((account) => {

            if (!authorities || authorities.length === 0) {
                return true;
            }

            if (account) {
                return principal.hasAnyAuthority(authorities).then((response) => {
                    if (response) {
                        return true;
                    }
                    this.router.navigate(['accessdenied']);
                    return false;
                });
            }

            this.router.navigate(['accessdenied']);
            return false;
        }));
    }
}
