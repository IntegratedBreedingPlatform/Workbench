import { Injectable } from '@angular/core';
import { ParamContext } from '../../service/param.context';
import { GERMPLASM_DETAILS_URL, GERMPLASM_LIST_MANAGER_URL, STUDY_URL } from '../../../app.constants';
import { SafeResourceUrl } from '@angular/platform-browser/src/security/dom_sanitization_service';
import { DomSanitizer } from '@angular/platform-browser';

@Injectable()
export class GermplasmDetailsUrlService {

    constructor(
        private paramContext: ParamContext,
        private sanitizer: DomSanitizer
    ) {
    }

    getUrl(gid: any): SafeResourceUrl {

        const queryParams = '?cropName=' + this.paramContext.cropName
            + '&programUUID=' + this.paramContext.programUUID
            + '&authToken=' + this.paramContext.authToken
            + '&loggedInUserId=' + this.paramContext.loggedInUserId
            + '&selectedProjectId=' + this.paramContext.selectedProjectId;

        // Link to open Germplasm Details page to a new tab.
        return this.sanitizer.bypassSecurityTrustResourceUrl(GERMPLASM_DETAILS_URL + gid + queryParams);

    }

    openStudy(studyId: any) {
        window.top.postMessage({ toolSelected: STUDY_URL + studyId }, '*');
        return false;
    }

    openList(listId: any) {
        const queryParams = `?restartApplication&lists=${listId}`;
        window.top.postMessage({ toolSelected: GERMPLASM_LIST_MANAGER_URL + queryParams }, '*');
        return false;
    }

}
