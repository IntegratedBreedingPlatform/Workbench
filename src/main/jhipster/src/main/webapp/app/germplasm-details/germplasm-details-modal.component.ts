import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PopupService } from '../shared/modal/popup.service';
import { ParamContext } from '../shared/service/param.context';
import { GermplasmDetailsContext } from './germplasm-details.context';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { GERMPLASM_DETAILS_URL } from '../app.constants';
import { DomSanitizer } from '@angular/platform-browser';
import { SafeResourceUrl } from '@angular/platform-browser/src/security/dom_sanitization_service';
import { GermplasmService } from '../shared/germplasm/service/germplasm.service';
import { GermplasmDto } from '../shared/germplasm/model/germplasm.model';

@Component({
    selector: 'jhi-germplasm-details-modal',
    templateUrl: './germplasm-details-modal.component.html'
})
export class GermplasmDetailsModalComponent implements OnInit {

    germplasm: GermplasmDto
    safeUrl: SafeResourceUrl;
    gid: number;

    constructor(private route: ActivatedRoute, private router: Router,
                private germplasmDetailsContext: GermplasmDetailsContext,
                public activeModal: NgbActiveModal,
                private paramContext: ParamContext,
                private sanitizer: DomSanitizer,
                private germplasmService: GermplasmService) {
        const queryParams = '?cropName=' + this.paramContext.cropName
            + '&programUUID=' + this.paramContext.programUUID
            + '&authToken=' + this.paramContext.authToken
            + '&loggedInUserId=' + this.paramContext.loggedInUserId
            + '&selectedProjectId=' + this.paramContext.selectedProjectId
            + '&modal=true'; // Tell the page that it is shown as a modal.

        this.safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(GERMPLASM_DETAILS_URL + this.germplasmDetailsContext.gid + queryParams);
    }

    ngOnInit(): void {
        this.germplasmService.getGermplasmById(this.germplasmDetailsContext.gid).toPromise().then((value) => {
            this.germplasm = value.body;
        })
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

}

@Component({
    selector: 'jhi-germplasm-details-popup',
    template: ``
})
export class GermplasmDetailsPopupComponent implements OnInit {

    germplasm: GermplasmDto;

    constructor(private route: ActivatedRoute,
                private popupService: PopupService,
                private paramContext: ParamContext,
                private germplasmDetailsContext: GermplasmDetailsContext) {
    }

    ngOnInit(): void {
        this.germplasmDetailsContext.gid = Number(this.route.snapshot.paramMap.get('gid'));
        const modal = this.popupService.open(GermplasmDetailsModalComponent as Component, { windowClass: 'modal-extra-large', backdrop: 'static' });
    }

}
