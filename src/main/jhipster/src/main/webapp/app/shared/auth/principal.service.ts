import { Injectable } from '@angular/core';
import { AccountService } from './account.service';
import { Subject, Observable } from 'rxjs';

@Injectable()
export class Principal {
    private userIdentity: any;
    private authenticationState = new Subject<any>();

    constructor(
        private account: AccountService
    ) {}

    hasAnyAuthority(authorities: string[]): Promise<boolean> {
        return Promise.resolve(this.hasAnyAuthorityDirect(authorities));
    }

    hasAnyAuthorityDirect(authorities: string[]): boolean {
        if (!this.userIdentity || !this.userIdentity.authorities) {
            return false;
        }

        for (let i = 0; i < authorities.length; i++) {
            if (this.userIdentity.authorities.includes(authorities[i])) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param force retrieve identity again from server
     * @param cropName retrieves authorities for crop
     * @param programUUID retrieves authorities for program
     */
    identity(force = false, cropName?, programUUID?): Promise<any> {
        if (force === true) {
            this.userIdentity = undefined;
        }

        // check and see if we have retrieved the userIdentity data from the server.
        // if we have, reuse it by immediately resolving
        if (this.userIdentity) {
            return Promise.resolve(this.userIdentity);
        }

        // retrieve the userIdentity data from the server, update the identity object, and then resolve.
        return this.account.get(cropName, programUUID).toPromise().then((account) => {
            if (account) {
                this.userIdentity = account;
            } else {
                this.userIdentity = null;
            }
            this.authenticationState.next(this.userIdentity);
            return this.userIdentity;
        }).catch((err) => {
            this.userIdentity = null;
            this.authenticationState.next(this.userIdentity);
            return null;
        });
    }

    getAuthenticationState(): Observable<any> {
        return this.authenticationState.asObservable();
    }
}
