import { Component, Injectable } from '@angular/core';
import { NgbModal, NgbModalOptions, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';

@Injectable()
export class PopupService {
    private ngbModalRef: NgbModalRef;

    constructor(
        private modalService: NgbModal,
        private router: Router,
    ) {
        this.ngbModalRef = null;
    }

    /**
     * @param component Component to open inside modal
     * @param options NgbModalOptions optional params.
     * TODO upgrade angular 8 / ng-bootstrap 5 -> to be able to pass size: 'xl'
     */
    open(component: Component,
         options: NgbModalOptions = { size: 'lg', backdrop: 'static' }): Promise<NgbModalRef> {

        return new Promise<NgbModalRef>((resolve, reject) => {
            // setTimeout used as a workaround for getting ExpressionChangedAfterItHasBeenCheckedError
            setTimeout(() => {
                this.ngbModalRef = this.modalRef(component, options);
                resolve(this.ngbModalRef);
            }, 0);
        });
    }

    modalRef(component: Component, options: NgbModalOptions): NgbModalRef {
        const modalRef = this.modalService.open(component, options);
        modalRef.result.then(() => {
            this.close();
        }, () => {
            this.close();
        });
        return modalRef;
    }

    close() {
        this.router.navigate([{ outlets: { popup: null } }], {
            replaceUrl: true, queryParamsHandling: 'merge'
        });
        this.ngbModalRef = null;
    }

}
