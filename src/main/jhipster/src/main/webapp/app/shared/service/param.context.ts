import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Injectable()
export class ParamContext {
    cropName: string;
    programUUID: string;

    authToken: string;
    selectedProjectId: number;
    loggedInUserId: number;

    constructor(private activatedRoute: ActivatedRoute) {
    }

    readParams(): any {
        const queryParams = this.activatedRoute.snapshot.queryParams;
        this.cropName = queryParams.cropName;
        this.programUUID = queryParams.programUUID;
        this.authToken = queryParams.authToken;
        this.selectedProjectId = queryParams.selectedProjectId;
        this.loggedInUserId = queryParams.loggedInUserId;
    }
}
