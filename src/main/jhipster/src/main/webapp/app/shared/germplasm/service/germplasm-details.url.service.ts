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

    getStudyUrl(studyId: any): SafeResourceUrl {
        const queryParams = `?loggedInUserId=${this.paramContext.loggedInUserId}`
            + `&selectedProjectId=${this.paramContext.selectedProjectId}`;
        // Link to open Study
        return this.sanitizer.bypassSecurityTrustResourceUrl(`${STUDY_URL}${studyId}` + queryParams);

    }

    getGermplasmListUrl(listId: any): SafeResourceUrl {
        const queryParams = `?restartApplication`
            + `&lists=${listId}`;
        return this.sanitizer.bypassSecurityTrustResourceUrl(GERMPLASM_LIST_MANAGER_URL + queryParams);
    }

}
