import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { HttpResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { SampleList } from './sample-list.model';
import { SampleListService } from './sample-list.service';

@Injectable()
export class SampleListPopupService {
    private ngbModalRef: NgbModalRef;

    constructor(
        private datePipe: DatePipe,
        private modalService: NgbModal,
        private router: Router,
        private sampleListService: SampleListService

    ) {
        this.ngbModalRef = null;
    }

    open(component: Component, id?: number | any): Promise<NgbModalRef> {
        return new Promise<NgbModalRef>((resolve, reject) => {
            const isOpen = this.ngbModalRef !== null;
            if (isOpen) {
                resolve(this.ngbModalRef);
            }

            if (id) {
                this.sampleListService.find(id)
                    .subscribe((sampleListResponse: HttpResponse<SampleList>) => {
                        const sampleList: SampleList = sampleListResponse.body;
                        sampleList.createdDate = this.datePipe
                            .transform(sampleList.createdDate, 'yyyy-MM-ddTHH:mm:ss');
                        this.ngbModalRef = this.sampleListModalRef(component, sampleList);
                        resolve(this.ngbModalRef);
                    });
            } else {
                // setTimeout used as a workaround for getting ExpressionChangedAfterItHasBeenCheckedError
                setTimeout(() => {
                    this.ngbModalRef = this.sampleListModalRef(component, new SampleList());
                    resolve(this.ngbModalRef);
                }, 0);
            }
        });
    }

    sampleListModalRef(component: Component, sampleList: SampleList): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.sampleList = sampleList;
        modalRef.result.then((result) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true, queryParamsHandling: 'merge' });
            this.ngbModalRef = null;
        }, (reason) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true, queryParamsHandling: 'merge' });
            this.ngbModalRef = null;
        });
        return modalRef;
    }
}
