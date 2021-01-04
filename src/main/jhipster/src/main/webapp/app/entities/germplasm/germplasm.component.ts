import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../shared/modal/popup.service';
import { DomSanitizer } from '@angular/platform-browser';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { GERMPLASM_BROWSER_DEFAULT_URL } from '../../app.constants';
import { ParamContext } from '../../shared/service/param.context';

@Component({
    selector: 'jhi-germplasm',
    template: `
        <div class="container">
            <div class="modal-header">
                <h4 class="modal-title font-weight-bold">
                    <span jhiTranslate="germplasm.details.title"></span>: {{designation}} (GID: {{gid}})
                </h4>
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true"
                        (click)="clear()">&times;
                </button>
            </div>
            <div class="modal-body">
                <iframe [src]="safeUrl" style="border: 0" width="100%" height="400"></iframe>
            </div>
        </div>
    `
})
export class GermplasmComponent implements OnInit {
    safeUrl: string;
    gid: number;
    designation: string;

    constructor(private route: ActivatedRoute,
                public activeModal: NgbActiveModal,
                private sanitizer: DomSanitizer) {
    }

    ngOnInit(): void {
        this.designation = this.route.snapshot.queryParamMap.get('designation');
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }
}

@Component({
    selector: 'jhi-germplasm-popup',
    template: ``
})
export class GermplasmPopupComponent implements OnInit {

    constructor(private route: ActivatedRoute,
                private popupService: PopupService,
                private sanitizer: DomSanitizer,
                private paramContext: ParamContext) {
    }

    ngOnInit(): void {
        const gid = this.route.snapshot.paramMap.get('gid');
        const authParams = '?restartApplication'
            + '&authToken=' + this.paramContext.authToken
            + '&loggedInUserId=' + this.paramContext.loggedInUserId
            + '&selectedProjectId=' + this.paramContext.selectedProjectId;

        const modal = this.popupService.open(GermplasmComponent as Component, { windowClass: 'modal-extra-large', backdrop: 'static' });
        modal.then((modalRef) => {
            modalRef.componentInstance.safeUrl =
                this.sanitizer.bypassSecurityTrustResourceUrl(GERMPLASM_BROWSER_DEFAULT_URL + gid + authParams);
            modalRef.componentInstance.gid = gid;
        });
    }

}
