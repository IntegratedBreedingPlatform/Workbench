import { Component, OnDestroy, OnInit } from '@angular/core';
import { AlertService } from '../shared/alert/alert.service';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../shared/modal/popup.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiLanguageService } from 'ng-jhipster';
import { ReleaseNotesService } from './release-notes.service';
import { HttpResponse } from '@angular/common/http';
import { ReleaseNote } from './release-notes.model';
import { SafeResourceUrl } from '@angular/platform-browser';
import { DomSanitizer } from '@angular/platform-browser';
import { ReleaseNoteContext } from './release-note.context';
import { finalize } from 'rxjs/operators';

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
    releaseNoteFileName: string;
    comingSoonFileName: string;
    showAgainCheckbox: boolean;
    showDescription: boolean;

    constructor(
        private modal: NgbActiveModal,
        private releaseNoteService: ReleaseNotesService,
        private languageService: JhiLanguageService,
        private route: ActivatedRoute,
        private paramContext: ReleaseNoteContext
    ) {
        const queryParams = this.route.snapshot.queryParams;
        this.showAgainCheckbox = queryParams.showAgainCheckbox ? (queryParams.showAgainCheckbox === 'true') ? true : false : true;
        this.showDescription = this.showAgainCheckbox;

        this.paramContext.fileName = queryParams.fileName;

        this.releaseNoteFileName = queryParams.fileName;
        this.hasComingSoon = (queryParams.hasComingSoon === 'true') ? true : false;

        if (this.hasComingSoon) {
            this.comingSoonFileName = `${queryParams.fileName}_coming_soon`;
        }
    }

    ngOnInit(): void {

    }

    dismiss() {
        if (this.dontShowAgain) {
            this.releaseNoteService.dontShowAgain().pipe(finalize(() => {
                this.closeModal();
            })).subscribe(() => {});
            return;
        }

        this.closeModal();
    }

    private closeModal() {
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
                private releaseNoteService: ReleaseNotesService,
                private route: ActivatedRoute) {

        this.releaseNoteService.getLatest().subscribe((resp: HttpResponse<ReleaseNote>) => {
            const releaseNote: ReleaseNote = resp.body;
            const queryParams = this.route.snapshot.queryParams;

            let url = '/ibpworkbench/main/#release-notes-dialog?' +
                '&version=' + releaseNote.version +
                '&hasComingSoon=' + releaseNote.hasComingSoon +
                '&fileName=' + releaseNote.fileName;

            if (queryParams.showAgainCheckbox) {
                url = url + '&showAgainCheckbox=' + queryParams.showAgainCheckbox;
            }
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
