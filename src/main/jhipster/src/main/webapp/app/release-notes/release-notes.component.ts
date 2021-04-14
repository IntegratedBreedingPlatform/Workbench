import { Component, OnDestroy, OnInit } from '@angular/core';
import { AlertService } from '../shared/alert/alert.service';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../shared/modal/popup.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiLanguageService } from 'ng-jhipster';
import { ReleaseNotesService } from './release-notes.service';
import { HttpResponse } from '@angular/common/http';
import { ReleaseNote } from './release-notes.model';
import { SafeResourceUrl } from '@angular/platform-browser/src/security/dom_sanitization_service';
import { DomSanitizer } from '@angular/platform-browser';
import { ReleaseNoteContext } from './release-note.context';

@Component({
    selector: 'jhi-release-notes-dialog',
    templateUrl: './release-notes.component.html',
    styleUrls: [
        'release-notes.scss'
    ],
})
export class ReleaseNotesDialogComponent implements OnInit {

    dontShowAgain;

    hasComingSoon = false;
    releaseNoteFileName: string
    comingSoonFileName: string

    constructor(
        private modal: NgbActiveModal,
        private releaseNoteService: ReleaseNotesService,
        private languageService: JhiLanguageService,
        private route: ActivatedRoute,
        private paramContext: ReleaseNoteContext
    ) {
        const queryParams = this.route.snapshot.queryParams;
        this.paramContext.fileName = queryParams.version;

        this.releaseNoteFileName = queryParams.version;
        this.comingSoonFileName = `${queryParams.version}_coming_soon`;
        this.hasComingSoon = queryParams.hasComingSoon;
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
    templateUrl: './release-note-modal.component.html'
})
export class ReleaseNotesWrapperComponent implements OnInit {

    url: SafeResourceUrl;

    constructor(private activeModal: NgbActiveModal,
                private sanitizer: DomSanitizer,
                private releaseNoteService: ReleaseNotesService) {

        this.releaseNoteService.getLatest().subscribe((resp: HttpResponse<ReleaseNote>) => {
            const releaseNote: ReleaseNote = resp.body;
            const url = '/ibpworkbench/main/#release-notes-dialog?' +
                '&version=' + releaseNote.version +
                '&hasComingSoon=' + releaseNote.hasComingSoon;

            this.url = this.sanitizer.bypassSecurityTrustResourceUrl(url);
        });
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
                .open(ReleaseNotesWrapperComponent as Component, { size: 'lg', backdrop: 'static', keyboard: false});
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }

}
