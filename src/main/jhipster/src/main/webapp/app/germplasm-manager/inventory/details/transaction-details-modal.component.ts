import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DomSanitizer } from '@angular/platform-browser';
import { SafeResourceUrl } from '@angular/platform-browser/src/security/dom_sanitization_service';
import { PopupService } from '../../../shared/modal/popup.service';
import { ParamContext } from '../../../shared/service/param.context';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { JhiLanguageService } from 'ng-jhipster';

@Component({
    selector: 'jhi-transaction-details-modal',
    templateUrl: './transaction-details-modal.component.html',
})
export class TransactionDetailsModalComponent implements OnInit {

    readonly url: SafeResourceUrl;

    constructor(private jhiLanguageService: JhiLanguageService,
                private route: ActivatedRoute,
                private sanitizer: DomSanitizer,
                private context: ParamContext,
                public activeModal: NgbActiveModal,
                private translateService: TranslateService
    ) {
        const gid = this.route.snapshot.queryParams.gid;
        const lotId = this.route.snapshot.queryParams.lotId;
        const url = '/ibpworkbench/controller/jhipster#/transaction?restartApplication' +
            '&cropName=' + context.cropName +
            '&programUUID=' + context.programUUID +
            '&gid=' + gid + '&lotId=' + lotId + '&modal';
        this.url = this.sanitizer.bypassSecurityTrustResourceUrl(url);
    }

    ngOnInit() {
        (<any>window).closeModal = () => {
            this.activeModal.close();
        };
    }

    cancel() {
        this.activeModal.dismiss();
        if ((<any>window.parent).closeModal) {
            (<any>window.parent).closeModal();
        }
    }
}
