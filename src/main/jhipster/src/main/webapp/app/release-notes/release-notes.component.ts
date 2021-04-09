import { Component, OnDestroy, OnInit } from '@angular/core';
import { AlertService } from '../shared/alert/alert.service';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../shared/modal/popup.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiLanguageService } from 'ng-jhipster';
import { ReleaseNotesService } from './release-notes.service';

@Component({
    selector: 'jhi-release-notes-dialog',
    templateUrl: './release-notes.component.html'
})
export class ReleaseNotesDialogComponent implements OnInit {

    dontShowAgain = true;

    constructor(
        private modal: NgbActiveModal,
        private releaseNoteService: ReleaseNotesService,
        private languageService: JhiLanguageService
    ) {
    }

    ngOnInit(): void {
    }

    dismiss() {
        if (this.dontShowAgain) {
            this.releaseNoteService.dontShowAgain().subscribe(() => {});
        }

        this.modal.dismiss();
        if ((<any>window.parent).closeModal) {
            (<any>window.parent).closeModal();
        }
    }

}

@Component({
    selector: 'jhi-release-notes-wrapper',
    template: `
        <iframe src="/ibpworkbench/main/#release-notes-dialog" style="border: 0; min-height: 300px;" width="100%"></iframe>
    `
})
export class ReleaseNotesWrapperComponent implements OnInit {

    constructor(private activeModal: NgbActiveModal) {

    }

    ngOnInit() {
        (<any>window).closeModal = () => {
            this.activeModal.close();
        };
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
                .open(ReleaseNotesWrapperComponent as Component);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }

}
