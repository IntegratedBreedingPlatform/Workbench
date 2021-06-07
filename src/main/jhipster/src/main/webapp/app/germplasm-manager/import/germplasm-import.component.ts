import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../shared/modal/popup.service';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmImportBasicDetailsComponent } from './germplasm-import-basic-details.component';
import { parseFile, saveFile } from '../../shared/util/file-utils';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { HttpErrorResponse } from '@angular/common/http';
import { AlertService } from '../../shared/alert/alert.service';
import { NameType } from '../../shared/germplasm/model/name-type.model';
import { Attribute } from '../../shared/attributes/model/attribute.model';
import { GermplasmImportValidationPayload } from '../../shared/germplasm/model/germplasm-import-request.model';
import { listPreview } from '../../shared/util/list-preview';
import { GermplasmImportContext } from './germplasm-import.context';

@Component({
    selector: 'jhi-germplasm-import',
    templateUrl: 'germplasm-import.component.html'
})
export class GermplasmImportComponent implements OnInit {

    @ViewChild('fileUpload')
    fileUpload: ElementRef;

    fileName = '';

    rawData = new Array<any>();
    codes = {};

    extensions = ['.xls', '.xlsx'];
    selectedFileType = this.extensions[0];

    isLoading: boolean;

    constructor(
        private translateService: TranslateService,
        private alertService: AlertService,
        private modal: NgbActiveModal,
        private modalService: NgbModal,
        private germplasmService: GermplasmService,
        public context: GermplasmImportContext
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
            this.rawData = value;
            target.value = '';
        });
    }

    export($event) {
        $event.preventDefault();
        this.germplasmService.downloadGermplasmTemplate(false).subscribe((response) => {
            saveFile(response);
        });
    }

    dismiss() {
        this.modal.dismiss();
    }

    next() {
        this.isLoading = true;
        this.validateFile().then((valid) => {
            this.isLoading = false;
            if (valid) {
                this.sortNameTypes();
                this.modal.close();
                const nextModal = this.modalService.open(GermplasmImportBasicDetailsComponent as Component,
                    { size: 'lg', backdrop: 'static' });
            }
        }, (res) => {
            this.isLoading = false;
            this.onError(res);
        });
    }

    private async validateFile() {
        if (!this.rawData || this.rawData.filter((row) => row.some((column) => Boolean(column.trim()))).length <= 1) {
            this.alertService.error('germplasm.import.file.validation.file.empty');
            return false;
        }

        // normalize headers
        this.rawData[0] = this.rawData[0].map((header) => header.toUpperCase());
        const headers = this.rawData[0];
        this.context.data = this.rawData.slice(1).map((fileRow, rowIndex) => {
            return fileRow.reduce((map, col, colIndex) => {
                map[headers[colIndex]] = col;
                return map;
            }, {});
        });

        const errorMessage: string[] = [];
        this.validateHeader(this.rawData[0], errorMessage);
        this.validateData(errorMessage);

        if (errorMessage.length !== 0) {
            this.alertService.error('error.custom', { param: formatErrorList(errorMessage) });
            return false;
        }
        if (!Object.keys(this.codes).length) {
            this.alertService.error('germplasm.import.file.validation.names.no.column');
            return false;
        }
        this.context.nameTypes = await this.germplasmService.getGermplasmNameTypes(Object.keys(this.codes)).toPromise();
        // FIXME into IBP-4659 (using Resource /variables/filter)
        this.context.attributes = await this.germplasmService.getGermplasmAttributes(Object.keys(this.codes)).toPromise();
        if (!this.context.nameTypes || !this.context.nameTypes.length) {
            this.alertService.error('germplasm.import.file.validation.names.no.column');
            return false;
        }
        this.validateNameTypes(errorMessage);
        if (errorMessage.length !== 0) {
            this.alertService.error('error.custom', { param: formatErrorList(errorMessage) });
            return false;
        }
        return this.validateServerSide();
    }

    private validateHeader(fileHeader: string[], errorMessage: string[]) {
        this.codes = {};
        Object.keys(fileHeader
            // get duplicates
            .filter((header, i, self) => self.indexOf(header) !== i)
            // Show duplicates only once
            .reduce((dupesMap, header) => {
                dupesMap[header] = true;
                return dupesMap;
            }, {})
        ).forEach((header) => {
            errorMessage.push(this.translateService.instant('error.import.header.duplicated', { param: header }));
        });
        // Gather unknown columns
        fileHeader.filter((header) => Object.values(HEADERS).indexOf(header) < 0)
            .forEach((header) => this.codes[header] = 1);
        // Known name types
        fileHeader.filter((header) => {
            return [HEADERS.LNAME.toString(), HEADERS.DRVNM.toString()].indexOf(header.toUpperCase()) !== -1;
        }).forEach((header) => this.codes[header] = 1);
    }

    private validateData(errorMessage: string[]) {
        // row validations
        for (const row of this.context.data) {
            if (!row[HEADERS.ENTRY_NO]) {
                errorMessage.push(this.translateService.instant('germplasm.import.file.validation.entryNo'));
                break;
            }
            if (row[HEADERS.ENTRY_NO] && (isNaN(row[HEADERS.ENTRY_NO])
                || !Number.isInteger(Number(row[HEADERS.ENTRY_NO])) || row[HEADERS.ENTRY_NO] < 0)) {
                errorMessage.push(this.translateService.instant('germplasm.import.file.validation.entryNo.format'));
                break;
            }
            // Amount
            if (row[HEADERS.AMOUNT] && (isNaN(row[HEADERS.AMOUNT]) || row[HEADERS.AMOUNT] < 0)) {
                errorMessage.push(this.translateService.instant('germplasm.import.file.validation.amount.format'));
                break;
            }
            // Progenitors
            if (Boolean(row[HEADERS['PROGENITOR 1']]) !== Boolean(row[HEADERS['PROGENITOR 2']])) {
                errorMessage.push(this.translateService.instant('germplasm.import.file.validation.progenitors.both'));
                break;
            }
            // Reference
            if (row[HEADERS.REFERENCE].length > 255) {
                errorMessage.push(this.translateService.instant('germplasm.import.file.validation.reference'));
                break;
            }
        }
        // column validations
        if (this.context.data.map((row) => row[HEADERS.ENTRY_NO]).some((cell, i, col) => col.indexOf(cell) !== i)) {
            errorMessage.push(this.translateService.instant('germplasm.import.file.validation.entryNo.duplicates'));
        }
        if (this.context.data.map((row) => row[HEADERS.GUID]).filter((cell) => cell).some((cell, i, col) => col.indexOf(cell) !== i)) {
            errorMessage.push(this.translateService.instant('germplasm.import.file.validation.guid.duplicates'));
        }
    }

    private validateNameTypes(errorMessage: string[]) {
        const rowWithMissingNameData = [];
        const preferredNameInvalid = {};
        for (const row of this.context.data) {
            const nameColumns = this.context.nameTypes.filter((nameType) => row[nameType.code]);
            if (!nameColumns.length) {
                rowWithMissingNameData.push(row);
                continue;
            }
            nameColumns.forEach((n) => this.context.nameColumnsWithData[n.code] = true);
            const preferredName = row[HEADERS['PREFERRED NAME']];
            if (preferredName && !nameColumns.some((col) => col.code === preferredName.toUpperCase())) {
                preferredNameInvalid[preferredName] = true;
            }
        }
        if (rowWithMissingNameData.length) {
            const error = 'germplasm.import.file.validation.names.missing.data';
            const message = this.translateService.instant(error, {
                param: listPreview(rowWithMissingNameData.map((r) => r[HEADERS.ENTRY_NO]))
            });
            errorMessage.push(message);
        }
        if (Object.keys(preferredNameInvalid).length) {
            const error = 'germplasm.import.file.validation.names.preferred.invalid';
            const message = this.translateService.instant(error, {
                param: listPreview(Object.keys(preferredNameInvalid))
            });
            errorMessage.push(message);
        }
    }

    sortNameTypes(): any {
        // sort as in the file. TODO Different institutes may have name priorities
        this.context.nameTypes.sort((a, b) => {
            const header = this.rawData[0];
            if (header.indexOf(a.code) > header.indexOf(b.code)) {
                return 1;
            }
            return -1;
        });
    }

    private validateServerSide() {
        const extendedGermplasmImportRequest: GermplasmImportValidationPayload[] = this.context.data.map((row) => {
            return <GermplasmImportValidationPayload>({
                locationAbbr: row[HEADERS['LOCATION ABBR']],
                storageLocationAbbr: row[HEADERS['STORAGE LOCATION ABBR']],
                breedingMethodAbbr: row[HEADERS['BREEDING METHOD']],
                creationDate: row[HEADERS['CREATION DATE']],
                unit: row[HEADERS.UNITS],
                stockId: row[HEADERS['STOCK ID']],
                germplasmUUID: row[HEADERS.GUID],
                progenitor1: row[HEADERS['PROGENITOR 1']],
                progenitor2: row[HEADERS['PROGENITOR 2']]
            });
        });
        return this.germplasmService.validateImportGermplasmData(extendedGermplasmImportRequest).toPromise()
            .then(() => true);
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general', null, null);
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

export enum HEADERS {
    'ENTRY_NO' = 'ENTRY_NO',
    'LNAME' = 'LNAME',
    'DRVNM' = 'DRVNM',
    'PREFERRED NAME' = 'PREFERRED NAME',
    'ENTRY_CODE' = 'ENTRY_CODE',
    'LOCATION ABBR' = 'LOCATION ABBR',
    'REFERENCE' = 'REFERENCE',
    'CREATION DATE' = 'CREATION DATE',
    'BREEDING METHOD' = 'BREEDING METHOD',
    'PROGENITOR 1' = 'PROGENITOR 1',
    'PROGENITOR 2' = 'PROGENITOR 2',
    // Attribute
    // 'NOTES' = 'NOTES',
    'STORAGE LOCATION ABBR' = 'STORAGE LOCATION ABBR',
    'UNITS' = 'UNITS',
    'AMOUNT' = 'AMOUNT',
    'STOCK ID' = 'STOCK ID',
    // Used internally - doesn't come in spreadsheet
    'STOCK ID PREFIX' = 'STOCK ID PREFIX',
    'GUID' = 'GUID',
    // Used internally - doesn't come in spreadsheet
    'GID MATCHES' = 'GID MATCHES',
}
