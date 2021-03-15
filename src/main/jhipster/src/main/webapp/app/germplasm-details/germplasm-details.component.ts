import { Component, OnInit } from '@angular/core';
import { ParamContext } from '../shared/service/param.context';
import { ActivatedRoute } from '@angular/router';
import { GermplasmDetailsContext } from './germplasm-details.context';
import { GERMPLASM_DETAILS_URL } from '../app.constants';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
    selector: 'jhi-germplasm-details',
    templateUrl: './germplasm-details.component.html'
})
export class GermplasmDetailsComponent implements OnInit {

    safeUrl: any;
    isModal: boolean;

    constructor(private paramContext: ParamContext, private germplasmDetailsContext: GermplasmDetailsContext,
                private route: ActivatedRoute,
                private sanitizer: DomSanitizer) {
    }

    ngOnInit(): void {
        const gid = this.route.snapshot.paramMap.get('gid');
        this.germplasmDetailsContext.gid = Number(gid);
        this.paramContext.readParams();
        const authParams = '?cropName=' + this.paramContext.cropName
            + '&programUUID=' + this.paramContext.programUUID
            + '&authToken=' + this.paramContext.authToken
            + '&loggedInUserId=' + this.paramContext.loggedInUserId
            + '&selectedProjectId=' + this.paramContext.selectedProjectId;

        // Link to open Germplasm Details page to a new tab.
        this.safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(GERMPLASM_DETAILS_URL + gid + authParams);

        // Only show 'Open to a new tab' button if the page is shown inside a modal window.
        this.isModal = this.route.snapshot.queryParamMap.has('modal');

    }

}
