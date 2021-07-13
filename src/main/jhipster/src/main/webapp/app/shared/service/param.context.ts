import { Injectable } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Injectable()
export class ParamContext {
    cropName: string;
    programUUID: string;

    authToken: string;
    selectedProjectId: number;
    loggedInUserId: number;

    constructor(private activatedRoute: ActivatedRoute,
                private router: Router) {
    }

    readParams(): any {
        const queryParams = this.activatedRoute.snapshot.queryParams;
        this.cropName = queryParams.cropName;
        this.programUUID = queryParams.programUUID;
        this.authToken = queryParams.authToken;
        this.selectedProjectId = queryParams.selectedProjectId;
        this.loggedInUserId = queryParams.loggedInUserId;
    }

    /**
     * Workaround until https://github.com/angular/angular/issues/12664
     */
    resetQueryParams() {
        // FIXME this.router.navigate(['./'], { relativeTo: this.activatedRoute } not working
        return this.router.navigate(['/germplasm-manager/germplasm-search/'], {
            queryParams: {
                programUUID: this.programUUID,
                cropName: this.cropName,
                authToken: this.authToken,
                selectedProjectId: this.selectedProjectId,
                loggedInUserId: this.loggedInUserId
            }
        });
    }
}
