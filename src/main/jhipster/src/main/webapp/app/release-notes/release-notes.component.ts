import { Component, OnDestroy, OnInit } from '@angular/core';
import { AlertService } from '../shared/alert/alert.service';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../shared/modal/popup.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-release-notes-dialog',
    templateUrl: './release-notes.component.html'
})
export class ReleaseNotesDialogComponent implements OnInit {

    dontShowAgain = true;

    constructor(public modal: NgbActiveModal
    ) {

    }

    ngOnInit(): void {
    }

    dismiss() {
        this.modal.dismiss();
    }

}

@Component({
    selector: 'jhi-release-notes-popup',
    template: ''
})
export class ReleaseNotesPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(private alertService: AlertService,
                private route: ActivatedRoute,
                private popupService: PopupService
    ) {
    }

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.popupService
                .open(ReleaseNotesDialogComponent as Component);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }

}
