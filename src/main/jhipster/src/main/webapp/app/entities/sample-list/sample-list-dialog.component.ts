import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { SampleList } from './sample-list.model';
import { SampleListPopupService } from './sample-list-popup.service';
import { SampleListService } from './sample-list.service';

@Component({
    selector: 'jhi-sample-list-dialog',
    templateUrl: './sample-list-dialog.component.html'
})
export class SampleListDialogComponent implements OnInit {

    sampleList: SampleList;
    isSaving: boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private sampleListService: SampleListService,
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
        if (this.sampleList.id !== undefined) {
            this.subscribeToSaveResponse(
                this.sampleListService.update(this.sampleList));
        } else {
            this.subscribeToSaveResponse(
                this.sampleListService.create(this.sampleList));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<SampleList>>) {
        result.subscribe((res: HttpResponse<SampleList>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: SampleList) {
        this.eventManager.broadcast({ name: 'sampleListListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }
}

@Component({
    selector: 'jhi-sample-list-popup',
    template: ''
})
export class SampleListPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private sampleListPopupService: SampleListPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.sampleListPopupService
                    .open(SampleListDialogComponent as Component, params['id']);
            } else {
                this.sampleListPopupService
                    .open(SampleListDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
