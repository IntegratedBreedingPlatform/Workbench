import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { Sample } from './sample.model';
import { SamplePopupService } from './sample-popup.service';
import { SampleService } from './sample.service';

@Component({
    selector: 'jhi-sample-delete-dialog',
    templateUrl: './sample-delete-dialog.component.html'
})
export class SampleDeleteDialogComponent {

    sample: Sample;

    constructor(
        private sampleService: SampleService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.sampleService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'sampleListModification',
                content: 'Deleted an sample'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-sample-delete-popup',
    template: ''
})
export class SampleDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private samplePopupService: SamplePopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.samplePopupService
                .open(SampleDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
