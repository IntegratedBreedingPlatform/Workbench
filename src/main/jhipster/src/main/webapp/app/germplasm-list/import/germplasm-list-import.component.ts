import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../shared/modal/popup.service';
import { TranslateService } from '@ngx-translate/core';
import { AlertService } from '../../shared/alert/alert.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { VariableService } from '../../shared/ontology/service/variable.service';
import { parseFile, saveFile } from '../../shared/util/file-utils';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { GermplasmListService } from '../../shared/germplasm-list/service/germplasm-list.service';
import { GermplasmListImportContext } from './germplasm-list-import.context';
import { listPreview } from '../../shared/util/list-preview';
import { GermplasmListImportReviewComponent } from './germplasm-list-import-review.component';

@Component({
    selector: 'jhi-germplasm-list-import',
    templateUrl: 'germplasm-list-import.component.html'
})
export class GermplasmListImportComponent implements OnInit {

    @ViewChild('fileUpload')
    fileUpload: ElementRef;

    fileName = '';

    rawData = new Array<any>();
    unknownColumn = {};

    extensions = ['.xls', '.xlsx'];
    selectedFileType = this.extensions[0];

    isLoading: boolean;
    constructor(
        private translateService: TranslateService,
        private alertService: AlertService,
        private modal: NgbActiveModal,
        private modalService: NgbModal,
        private germplasmService: GermplasmService,
        private variableService: VariableService,
        private context: GermplasmListImportContext,
        private germplasmListService: GermplasmListService
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
            this.alertService.error('germplasm-list.import.file.validation.extensions', { param: this.extensions.join(', ') });
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
        this.germplasmListService.downloadGermplasmTemplate().subscribe((response) => {
            saveFile(response);
        });
    }

    next() {
        this.isLoading = true;
        this.validateFile().then((valid) => {
            this.isLoading = false;
            if (valid) {
                this.showUnknownColumnsWarning();
                this.modal.close();
                const nextModal = this.modalService.open(GermplasmListImportReviewComponent as Component, { size: 'lg', backdrop: 'static' });
            }
        }, (res) => {
            this.isLoading = false;
            this.onError(res);
        });
    }

    showUnknownColumnsWarning(): any {
        const unknown = Object.keys(this.unknownColumn);
        if (unknown.length) {
            this.alertService.warning('germplasm-list.import.file.validation.unknown.column', { param: listPreview(unknown) }, 5000);
        }
    }

    private async validateFile() {
        if (!this.rawData || this.rawData.filter((row) => row.some((column) => Boolean(column.trim()))).length <= 1) {
            this.alertService.error('germplasm-list.import.file.validation.file.empty');
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
        this.validateHeader(headers, errorMessage);
        this.validateData(errorMessage);

        if (errorMessage.length !== 0) {
            this.alertService.error('error.custom', { param: formatErrorList(errorMessage) });
            return false;
        }

        return true;
    }

    private validateHeader(fileHeader: string[], errorMessage: string[]) {
        // Ignore empty column headers
        fileHeader = fileHeader.filter((header) => !!header);
        this.unknownColumn = {};
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
            .forEach((header) => this.unknownColumn[header] = 1);

        if (fileHeader.indexOf(HEADERS.GUID) < 0 && fileHeader.indexOf(HEADERS.GID) < 0 && fileHeader.indexOf(HEADERS.DESIGNATION) < 0) {
            errorMessage.push(this.translateService.instant('germplasm-list.import.file.validation.header'));
        }
    }

    private validateData(errorMessage: string[]) {
        // row validations
        let hasEntryCode = false;
        let hasEntryCodeEmpty = false;

        for (const row of this.context.data) {
            if (row[HEADERS.GID] && (isNaN(row[HEADERS.GID])
                || !Number.isInteger(Number(row[HEADERS.GID])) || row[HEADERS.GID] < 0)) {
                errorMessage.push(this.translateService.instant('germplasm-list.import.file.validation.gid.format'));
                break;
            }

            if (!row[HEADERS.ENTRY_CODE]) {
                hasEntryCodeEmpty = true;
            } else {
                hasEntryCode = true;
                if(row[HEADERS.ENTRY_CODE].length){
                    errorMessage.push(this.translateService.instant('germplasm-list.import.file.validation.entry.code.max.length'));
                    break;
                }
            }
        }

        if (hasEntryCode && hasEntryCodeEmpty) {
            errorMessage.push(this.translateService.instant('germplasm-list.import.file.validation.entry.code'));
        }
    }

    dismiss() {
        this.modal.dismiss();
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
    selector: 'jhi-germplasm-list-import-popup',
    template: ''
})
export class GermplasmListImportPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private popupService: PopupService
    ) {
    }

    ngOnDestroy(): void {
        this.routeSub.unsubscribe();
    }

    ngOnInit(): void {
        this.routeSub = this.route.params.subscribe(() => {
            this.popupService.open(GermplasmListImportComponent as Component);
        });
    }
}

export enum HEADERS {
    'GID' = 'GID',
    'GUID' = 'GUID',
    'DESIGNATION' = 'DESIGNATION',
    // Used internally - doesn't come in spreadsheet
    'GID_MATCHES' = 'GID MATCHES',
    'ROW_NUMBER' = 'ROW NUMBER',
    'ENTRY_CODE' = 'ENTRY_CODE'

}
