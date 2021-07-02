import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DomSanitizer } from '@angular/platform-browser';
import { SafeResourceUrl } from '@angular/platform-browser/src/security/dom_sanitization_service';
import { PopupService } from '../../../shared/modal/popup.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ParamContext } from '../../../shared/service/param.context';
import { GermplasmDetailsContext } from '../../germplasm-details.context';

@Component({
    selector: 'jhi-germplasm-basic-details-audit-modal',
    templateUrl: './germplasm-basic-details-audit-modal.component.html',
})
export class GermplasmBasicDetailsAuditModalComponent implements OnInit {

    gid: number;

    readonly url: SafeResourceUrl;

    constructor(private route: ActivatedRoute,
                private sanitizer: DomSanitizer,
                private activeModal: NgbActiveModal,
                private context: ParamContext,
                private germplasmDetailsContext: GermplasmDetailsContext
    ) {
        const url = '/ibpworkbench/controller/jhipster#/germplasm/audit?restartApplication&modal' +
        '&cropName=' + context.cropName +
        '&programUUID=' + context.programUUID +
        '&gid=' + this.germplasmDetailsContext.gid;
        this.url = this.sanitizer.bypassSecurityTrustResourceUrl(url);
    }

    ngOnInit() {
        (<any>window).closeModal = () => {
            this.activeModal.close();
        };
    }

}

@Component({
    selector: 'jhi-germplasm-basic-details-popup',
    template: ''
})
export class GermplasmBasicDetailsAuditPopupComponent implements OnInit {

    constructor(private popupService: PopupService,
                private germplasmDetailsContext: GermplasmDetailsContext,
                private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.germplasmDetailsContext.gid = Number(this.route.snapshot.paramMap.get('gid'));
        const modal = this.popupService.open(GermplasmBasicDetailsAuditModalComponent as Component, { windowClass: 'modal-large', backdrop: 'static' });
    }

}
