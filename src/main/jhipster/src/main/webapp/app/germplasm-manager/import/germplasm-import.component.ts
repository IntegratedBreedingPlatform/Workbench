import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { JhiAlertService } from 'ng-jhipster';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../shared/modal/popup.service';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmImportBasicDetailsComponent } from './germplasm-import-basic-details.component';
import { parseFile, saveFile } from '../../shared/util/file-utils';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { formatErrorList } from '../../shared/alert/format-error-list';

@Component({
    selector: 'jhi-germplasm-import',
    templateUrl: 'germplasm-import.component.html'
})
export class GermplasmImportComponent implements OnInit {

    @ViewChild('fileUpload')
    fileUpload: ElementRef;

    fileName = '';
    importData = new Array<any>();
    importHeader = [];
    nameCodes = {};

    extensions = ['.csv', '.xls', '.xlsx'];

    selectedFileType = this.extensions[1];

    constructor(
        private translateService: TranslateService,
        private alertService: JhiAlertService,
        private modal: NgbActiveModal,
        private modalService: NgbModal,
        private germplasmService: GermplasmService
    ) {
    }

    ngOnInit(): void {
    }

    onFileChange(evt: any) {
        const target = evt.target;
        this.fileName = target.files[0].name;

        const extension = this.fileName.substring(this.fileName.lastIndexOf('.'));
        if (this.extensions.indexOf(extension.toLowerCase()) === -1) {
            this.fileName = '';
            target.value = '';
            this.alertService.error('germplasm.import.file.validation.extensions', { param: this.extensions.join(', ') });
            return;
        }

        this.fileUpload.nativeElement.innerText = target.files[0].name;

        parseFile(target.files[0], 'Observation').subscribe((value) => {
            this.importData = value;
            target.value = '';
        });
    }

    export($event) {
        $event.preventDefault();
        this.germplasmService.downloadGermplasmTemplate().subscribe((response) => {
            saveFile(response);
        });
    }

    dismiss() {
        this.modal.dismiss();
    }

    next() {
        this.validateFile().then((valid) => {
            if (valid) {
                this.modal.close();
                const nextModal = this.modalService.open(GermplasmImportBasicDetailsComponent as Component,
                    { size: 'lg', backdrop: 'static' });
                nextModal.componentInstance.importData = this.importData;
            }
        })
    }

    // TODO Complete
    async validateFile() {
        if (!this.importData || this.importData.filter((row) => row.some((column) => Boolean(column.trim()))).length <= 1) {
            this.alertService.error('germplasm.import.file.validation.file.empty');
            return false;
        }
        const errorMessage: string[] = [];
        this.validateHeader(this.importData[0], errorMessage);
        this.validateData(this.importData.slice(1), errorMessage);

        if (errorMessage.length !== 0) {
            this.alertService.error('error.custom', { param: formatErrorList(errorMessage) });
            return false;
        }
        return Promise.all([
            this.germplasmService.getGermplasmNameTypes(Object.keys(this.nameCodes)).toPromise()
        ]).then((all) => {
            const names: any = all[0].body;
            if (!names || !names.length) {
                this.alertService.error('germplasm.import.file.validation.names');
                return false;
            }
            return true;
        });
    }

    private validateHeader(fileHeader: string[], errorMessage: string[]) {
        this.importHeader = [];
        this.nameCodes = {};
        const headers = ['ENTRY_NO', 'LNAME', 'DRVNM', 'PREFERRED NAME', 'ENTRY_CODE', 'LOCATION ABBR', 'REFERENCE', 'CREATION DATE',
            'BREEDING METHOD', 'NOTES', 'STORAGE LOCATION ABBR', 'UNITS', 'AMOUNT', 'STOCK ID', 'GUID'];
        headers.forEach((header) => {
            const result = fileHeader.filter((column) =>
                column.toLowerCase() === header.toLowerCase()
            );

            if (result.length > 1) {
                errorMessage.push(this.translateService.instant('error.import.header.duplicated', { param: header }));
            }

            const idx = fileHeader.indexOf(result[0]);
            this.importHeader.push(idx);
            if (['LNAME', 'DRVNM'].indexOf(result[0]) !== -1) {
                this.nameCodes[result[0]] = 1;
            }
        });
        fileHeader.filter((header) => headers.indexOf(header) < 0)
            .forEach((header) => this.nameCodes[header] = 1);
    }

    private validateData(importData: any[], errorMessage: string[]) {
        // row validations
        for (let i = 0; i < importData.length; i++) {
            const row = importData[i];
            // ENTRY_NO
            if (!row[this.importHeader[0]]) {
                errorMessage.push(this.translateService.instant('germplasm.import.file.validation.entryNo'));
                break;
            }
            if (row[this.importHeader[0]] && (isNaN(row[this.importHeader[0]])
                || !Number.isInteger(Number(row[this.importHeader[0]])) || row[this.importHeader[0]] < 0)) {
                errorMessage.push(this.translateService.instant('germplasm.import.file.validation.entryNo.format'));
                break;
            }
            // Amount
            if (row[this.importHeader[12]] && (isNaN(row[this.importHeader[12]]) || row[this.importHeader[12]] < 0)) {
                errorMessage.push(this.translateService.instant('germplasm.import.file.validation.amount.format'));
                break;
            }
        }
        // column validations
        // ENTRY_NO
        if (importData.map((row) => row[this.importHeader[0]]).some((cell, i, col) => col.indexOf(cell) !== i)) {
            errorMessage.push(this.translateService.instant('germplasm.import.file.validation.entryNo.duplicates'));
        }
    }
}

@Component({
    selector: 'jhi-germplasm-import-popup',
    template: ''
})
export class GermplasmImportPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private popupService: PopupService
    ) {
    }

    ngOnInit() {
        this.routeSub = this.route.params.subscribe(() => {
            this.popupService.open(GermplasmImportComponent as Component);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
