import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { JhiAlertService, JhiEventManager } from 'ng-jhipster';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { PopupService } from '../shared/modal/popup.service';

@Component({
    selector: 'jhi-germplasm-import-update-dialog',
    templateUrl: './germplasm-import-update-dialog.component.html'
})
export class GermplasmImportUpdateDialogComponent implements OnInit, OnDestroy {

    @ViewChild('fileUpload')
    fileUpload: ElementRef;

    fileName = '';
    importData = new Array<any>();
    importHeader = [];

    importFormats = [
        { name: 'CSV', extension: '.csv' }, //
        { name: 'Excel', extension: '.xls,.xlsx' }
    ];

    selectedFileType = this.importFormats[1].extension; // '.xls'; // Set the default file type to CSV.

    constructor(
        private alertService: JhiAlertService,
        public activeModal: NgbActiveModal,
        private modalService: NgbModal,
        private eventManager: JhiEventManager,
        private translateService: TranslateService) {

    }

    ngOnDestroy(): void {
    }

    ngOnInit(): void {
    }

    close() {
        this.activeModal.dismiss('cancel');
    }

    import() {
        if (this.validate()) {
        }
    }

    onFileChange(evt: any) {
        const target = evt.target;
        this.fileName = target.files[0].name;

        const extension = this.fileName.split('.');
        if (extension[1].toLowerCase() !== 'xls' && extension[1].toLowerCase() !== 'xlsx') {
            this.fileName = '';
            target.value = '';
            this.alertService.error('error.custom', { param: 'The import lots is only available for Excel extension xls and xlsx' }, null);
            return;
        }

        this.fileUpload.nativeElement.innerText = target.files[0].name;

        // TODO: parse file

    }

    private validateHeader(fileHeader: string[], errorMessage: string[]) {
        // TODO: Validate header
    }

    private validateData(importData: any[], errorMessage: string[]) {
        // TODO: Validate data
    }

    private validate() {
        if (!this.importData || this.importData.filter((row) => row.some((column) => Boolean(column.trim()))).length <= 1) {
            this.alertService.error('germplasm-import-updates.validation.file.empty');
            return false;
        }

        const errorMessage: string[] = [];
        this.validateHeader(this.importData[0], errorMessage);
        this.validateData(this.importData.slice(1), errorMessage);

        if (errorMessage.length !== 0) {
            this.alertService.error('error.custom', { param: [] });
            return false;
        }
        return true;
    }

    private validateStockIDInput(importData: any[]) {
        return importData.slice(1).some((column) => !column[4]);
    }

    private onSaveSuccess(result: any) {
        this.alertService.success('germplasm-import-updates.import.success', null, null);
        this.eventManager.broadcast({ name: 'columnFiltersChanged', content: 'OK' });
        this.activeModal.close(result);
    }

    private onError(error) {
        if (error) {
            this.alertService.error('error.custom', { param: error.message }, null);
            return;
        }
        this.alertService.error('error.general', null, null);
    }

    export($event) {
        $event.preventDefault();
        // TODO: Download template.
    }
}

@Component({
    selector: 'jhi-germplasm-import-update-popup',
    template: ''
})
export class GermplasmImportUpdatePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(private jhiAlertService: JhiAlertService,
                private route: ActivatedRoute,
                private popupService: PopupService
    ) {
    }

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.popupService
                .open(GermplasmImportUpdateDialogComponent as Component);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }

}
