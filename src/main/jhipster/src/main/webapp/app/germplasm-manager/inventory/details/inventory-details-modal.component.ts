import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DomSanitizer } from '@angular/platform-browser';
import { SafeResourceUrl } from '@angular/platform-browser/src/security/dom_sanitization_service';
import { PopupService } from '../../../shared/modal/popup.service';
import { ParamContext } from '../../../shared/service/param.context';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-inventory-details-modal',
    templateUrl: './inventory-details-modal.component.html',
})
export class InventoryDetailsModalComponent implements OnInit {

    private readonly url: SafeResourceUrl;

    constructor(private route: ActivatedRoute,
                private sanitizer: DomSanitizer,
                private context: ParamContext,
                public activeModal: NgbActiveModal
    ) {
        const gid = this.route.snapshot.queryParams.gid;
        const url = '/ibpworkbench/controller/jhipster#/inventory-details?restartApplication'
        +
        '&cropName=' + context.cropName +
        '&programUUID=' + context.programUUID +
        '&gid=' + gid;
        this.url = this.sanitizer.bypassSecurityTrustResourceUrl(url);
    }

    ngOnInit() {
        // (<any>window).closeModal = this.cancel;
    }

    // cancel() {
    //     console.log('InventoryDetailsModalComponent cancel');
    //     this.activeModal.dismiss();
    //     (<any>window.parent).closeModal();
    // }

    onInit() {

    }

    cancel() {
        // this.activeModal.dismiss();
        (<any>window.parent).closeModal();
    }

    // window.closeModal = function() {
    //     $uibModalInstance.close(null);
    // };
    //
    // $scope.cancel = function() {
    //     $uibModalInstance.close(null);
    // };

}

@Component({
    selector: 'jhi-inventory-details-popup',
    template: ''
})
export class InventoryDetailsPopupComponent implements OnInit {

    constructor(private route: ActivatedRoute,
                private popupService: PopupService,
                public activeModal: NgbActiveModal) {
    }

    ngOnInit(): void {
        const modal = this.popupService.open(InventoryDetailsModalComponent as Component);
        (<any>window).closeModal = this.cancel;
    }

    cancel() {
        console.log('InventoryDetailsPopupComponent cancel');
        this.activeModal.dismiss();
        (<any>window.parent).closeModal();
    }

}
