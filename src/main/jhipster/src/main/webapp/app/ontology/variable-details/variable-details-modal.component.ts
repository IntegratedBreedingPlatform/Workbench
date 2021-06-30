import { Component, OnInit } from '@angular/core';
import { PopupService } from '../../shared/modal/popup.service';
import { VariableDetailsComponent } from './variable-details.component';
import { SafeResourceUrl } from '@angular/platform-browser/src/security/dom_sanitization_service';
import { ActivatedRoute } from '@angular/router';
import { DomSanitizer } from '@angular/platform-browser';
import { ParamContext } from '../../shared/service/param.context';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-variable-details-modal',
    template: `
		<iframe width="100%" height="570" frameborder="0" [src]="url"></iframe>
    `
})
export class VariableDetailsModalComponent {
    readonly url: SafeResourceUrl;

    constructor(private route: ActivatedRoute,
                private sanitizer: DomSanitizer,
                private context: ParamContext,
                public activeModal: NgbActiveModal
    ) {
        const variableId = this.route.snapshot.queryParamMap.get('variableId');
        const url = '/ibpworkbench/controller/jhipster#/variable-details?restartApplication' +
            '&cropName=' + this.context.cropName +
            '&programUUID=' + this.context.programUUID +
            '&variableId=' + variableId + '&modal';
        this.url = this.sanitizer.bypassSecurityTrustResourceUrl(url);

        (<any>window).closeModal = () => {
            this.activeModal.dismiss();
        }
    }
}

@Component({
    selector: 'jhi-variable-details-popup',
    template: ''
})
export class VariableDetailsPopupComponent implements OnInit {

    constructor(private popupService: PopupService) {
    }

    ngOnInit(): void {
        const modal = this.popupService.open(VariableDetailsModalComponent as Component);
    }
}
