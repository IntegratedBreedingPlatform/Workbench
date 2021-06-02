import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DomSanitizer } from '@angular/platform-browser';
import { SafeResourceUrl } from '@angular/platform-browser/src/security/dom_sanitization_service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ParamContext } from '../../shared/service/param.context';
import { PopupService } from '../../shared/modal/popup.service';

@Component({
    selector: 'jhi-variable-details-modal',
    templateUrl: './variable-details-modal.component.html',
})
export class VariableDetailsModalComponent implements OnInit {

    readonly url: SafeResourceUrl;

    constructor(private route: ActivatedRoute,
                private sanitizer: DomSanitizer,
                private context: ParamContext,
                private activeModal: NgbActiveModal
    ) {
        const variableId = this.route.snapshot.queryParams.variableId;
        const url = '/ibpworkbench/controller/jhipster#/variable-details?restartApplication' +
            '&cropName=' + context.cropName +
            '&programUUID=' + context.programUUID +
            '&variableId=' + variableId + '&modal';
        this.url = this.sanitizer.bypassSecurityTrustResourceUrl(url);
    }

    ngOnInit() {
        (<any>window).closeModal = () => {
            this.activeModal.close();
        };
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
        this.popupService.open(VariableDetailsModalComponent as Component);
    }

}
