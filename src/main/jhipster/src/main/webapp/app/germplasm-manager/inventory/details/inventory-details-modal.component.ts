import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DomSanitizer } from '@angular/platform-browser';
import { SafeResourceUrl } from '@angular/platform-browser';
import { PopupService } from '../../../shared/modal/popup.service';
import { ParamContext } from '../../../shared/service/param.context';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-inventory-details-modal',
    templateUrl: './inventory-details-modal.component.html',
})
export class InventoryDetailsModalComponent implements OnInit {

    readonly url: SafeResourceUrl;

    constructor(private route: ActivatedRoute,
                private sanitizer: DomSanitizer,
                private context: ParamContext,
                public activeModal: NgbActiveModal
    ) {
        const gid = this.route.snapshot.queryParams.gid;
        const url = '/ibpworkbench/controller/jhipster#/inventory-details?restartApplication' +
            '&cropName=' + context.cropName +
            '&programUUID=' + context.programUUID +
            '&gid=' + gid + '&modal';
        this.url = this.sanitizer.bypassSecurityTrustResourceUrl(url);
    }

    ngOnInit() {
        (<any>window).closeModal = () => {
            this.activeModal.close();
        };
    }

}

@Component({
    selector: 'jhi-inventory-details-popup',
    template: ''
})
export class InventoryDetailsPopupComponent implements OnInit {

    constructor(private popupService: PopupService) {
    }

    ngOnInit(): void {
        const modal = this.popupService.open(InventoryDetailsModalComponent as Component);
    }

}
