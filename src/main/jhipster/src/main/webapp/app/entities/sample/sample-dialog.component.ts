import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';

import { Observable, of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { Sample } from './sample.model';
import { SamplePopupService } from './sample-popup.service';
import { SampleService } from './sample.service';

@Component({
    selector: 'jhi-sample-dialog',
    templateUrl: './sample-dialog.component.html'
})
export class SampleDialogComponent implements OnInit {

    sample: Sample;
    isSaving: boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private sampleService: SampleService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.sample.id !== undefined) {
            this.subscribeToSaveResponse(
                this.sampleService.update(this.sample));
        } else {
            this.subscribeToSaveResponse(
                this.sampleService.create(this.sample));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<Sample>>) {
        result.subscribe((res: HttpResponse<Sample>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: Sample) {
        this.eventManager.broadcast({ name: 'sampleListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }
}

@Component({
    selector: 'jhi-sample-popup',
    template: ''
})
export class SamplePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private samplePopupService: SamplePopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.samplePopupService
                    .open(SampleDialogComponent as Component, params['id']);
            } else {
                this.samplePopupService
                    .open(SampleDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
