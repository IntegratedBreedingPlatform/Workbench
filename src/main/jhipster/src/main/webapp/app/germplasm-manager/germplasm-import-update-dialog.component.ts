import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { JhiAlertService, JhiEventManager } from 'ng-jhipster';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { PopupService } from '../shared/modal/popup.service';
import { ExcelService } from '../shared/service/excel.service';
import { GermplasmService } from '../shared/germplasm/service/germplasm.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { formatErrorList } from '../shared/alert/format-error-list';

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
        { name: 'Excel', extension: '.xls,.xlsx' }
    ];

    constructor(
        private alertService: JhiAlertService,
        public activeModal: NgbActiveModal,
        private modalService: NgbModal,
        private eventManager: JhiEventManager,
        private translateService: TranslateService,
        private excelService: ExcelService,
        private germplasmService: GermplasmService) {

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
            this.germplasmService.importGermplasmUpdates(null).subscribe(
                (res: HttpResponse<any>) => this.onSaveSuccess(null),
                (res: HttpErrorResponse) => this.onError(res)
            );
        }
    }

    onFileChange(evt: any) {
        const target = evt.target;
        this.fileName = target.files[0].name;

        const extension = this.fileName.split('.');
        if (extension[1].toLowerCase() !== 'xls' && extension[1].toLowerCase() !== 'xlsx') {
            this.fileName = '';
            target.value = '';
            this.alertService.error('error.custom', { param: 'The import germplasm updates is only available for Excel extension xls and xlsx' }, null);
            return;
        }

        this.fileUpload.nativeElement.innerText = target.files[0].name;

        this.excelService.parse(target, 'Observation').subscribe((value) => {
            this.importData = value;
        }, (error) => {
            this.alertService.error('error.custom', { param: error }, null);
        });
    }

    private validateHeader(fileHeader: string[], errorMessage: string[]) {
        this.importHeader = [];
        // TODO: Add Method Abbr once implemented in the backend.
        const headers = ['GID', 'GUID', 'PREFERRED NAME', 'LOCATION ABBR', 'REFERENCE', 'CREATION DATE'];
        headers.forEach((header) => {
            const result = fileHeader.filter((column) =>
                column.toLowerCase() === header.toLowerCase()
            );

            if (result.length === 0) {
                errorMessage.push(this.translateService.instant('error.import.header.mandatory', { param: headers.join(', ') }));
            }

            if (result.length > 1) {
                errorMessage.push(this.translateService.instant('error.import.header.duplicated', { param: header }));
            }

            const idx = fileHeader.indexOf(result[0]);
            this.importHeader.push(idx);
        });
    }

    private validate() {
        if (!this.importData || this.importData.filter((row) => row.some((column) => Boolean(column.trim()))).length <= 1) {
            this.alertService.error('germplasm-import-updates.validation.file.empty');
            return false;
        }

        const errorMessage: string[] = [];
        this.validateHeader(this.importData[0], errorMessage);

        if (errorMessage.length !== 0) {
            this.alertService.error('error.custom', { param: formatErrorList(errorMessage) });
            return false;
        }
        return true;
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
