import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { SampleList } from './sample-list.model';
import { SampleListPopupService } from './sample-list-popup.service';
import { SampleListService } from './sample-list.service';

@Component({
    selector: 'jhi-sample-list-delete-dialog',
    templateUrl: './sample-list-delete-dialog.component.html'
})
export class SampleListDeleteDialogComponent {

    sampleList: SampleList;

    constructor(
        private sampleListService: SampleListService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.sampleListService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'sampleListListModification',
                content: 'Deleted an sampleList'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-sample-list-delete-popup',
    template: ''
})
export class SampleListDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private sampleListPopupService: SampleListPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.sampleListPopupService
                .open(SampleListDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
