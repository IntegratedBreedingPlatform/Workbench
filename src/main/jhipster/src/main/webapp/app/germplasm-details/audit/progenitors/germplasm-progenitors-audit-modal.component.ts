import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';
import {PopupService} from '../../../shared/modal/popup.service';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {ParamContext} from '../../../shared/service/param.context';
import {GermplasmDetailsContext} from '../../germplasm-details.context';

@Component({
    selector: 'jhi-germplasm-progenitors-audit-modal',
    templateUrl: './germplasm-progenitors-audit-modal.component.html',
})
export class GermplasmProgenitorsAuditModalComponent implements OnInit {

    gid: number;

    readonly url: SafeResourceUrl;

    constructor(private route: ActivatedRoute,
                private sanitizer: DomSanitizer,
                private activeModal: NgbActiveModal,
                private context: ParamContext,
                private germplasmDetailsContext: GermplasmDetailsContext
    ) {
        const url = '/ibpworkbench/controller/jhipster#/germplasm/progenitors/audit?restartApplication&modal' +
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
    selector: 'jhi-germplasm-progenitors-popup',
    template: ''
})
export class GermplasmBasicDetailsAuditPopupComponent implements OnInit {

    constructor(private popupService: PopupService,
                private germplasmDetailsContext: GermplasmDetailsContext,
                private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.germplasmDetailsContext.gid = Number(this.route.snapshot.paramMap.get('gid'));
        const modal = this.popupService.open(GermplasmProgenitorsAuditModalComponent as Component, {
            windowClass: 'modal-large',
            backdrop: 'static'
        });
    }

}
