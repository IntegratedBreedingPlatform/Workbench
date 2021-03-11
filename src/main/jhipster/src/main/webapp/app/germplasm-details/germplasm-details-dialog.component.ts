import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PopupService } from '../shared/modal/popup.service';
import { ParamContext } from '../shared/service/param.context';
import { GermplasmDetailsContext } from './germplasm-details.context';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { GERMPLASM_DETAILS_URL } from '../app.constants';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
    selector: 'jhi-germplasm-details-dialog',
    templateUrl: './germplasm-details-dialog.component.html'
})
export class GermplasmDetailsDialogComponent implements OnInit {

    safeUrl: string;
    gid: number;

    constructor(private route: ActivatedRoute, private router: Router,
                private germplasmDetailsContext: GermplasmDetailsContext,
                public activeModal: NgbActiveModal) {
    }

    ngOnInit(): void {
        this.router.navigate(['/germplasm-basic-details']);
        this.germplasmDetailsContext.gid = this.gid;
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

    constructor(private route: ActivatedRoute,
                private popupService: PopupService,
                private paramContext: ParamContext,
                private sanitizer: DomSanitizer) {
    }

    ngOnInit(): void {
        const gid = this.route.snapshot.paramMap.get('gid');
        const authParams = '?cropName=' + this.paramContext.cropName
            + '&programUUID=' + this.paramContext.programUUID
            + '&authToken=' + this.paramContext.authToken
            + '&loggedInUserId=' + this.paramContext.loggedInUserId
            + '&selectedProjectId=' + this.paramContext.selectedProjectId;

        const modal = this.popupService.open(GermplasmDetailsDialogComponent as Component, { windowClass: 'modal-extra-large', backdrop: 'static' });
        modal.then((modalRef) => {
            modalRef.componentInstance.safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(GERMPLASM_DETAILS_URL + gid + authParams);
            modalRef.componentInstance.gid = gid;
        });
    }

}
