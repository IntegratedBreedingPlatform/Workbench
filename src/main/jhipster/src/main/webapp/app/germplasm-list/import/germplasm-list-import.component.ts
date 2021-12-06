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
import { VariableTypeEnum } from '../../shared/ontology/variable-type.enum';
import { toUpper } from '../../shared/util/to-upper';
import { GermplasmListImportReviewComponent } from './germplasm-list-import-review.component';
import { GermplasmListVariableMatchesComponent } from './germplasm-list-variable-matches.component';

@Component({
    selector: 'jhi-germplasm-list-import',
    templateUrl: 'germplasm-list-import.component.html'
})
export class GermplasmListImportComponent implements OnInit {

    @ViewChild('fileUpload')
    fileUpload: ElementRef;

    fileName = '';

    rawData = new Array<any>();

    extensions = ['.xls', '.xlsx'];
    selectedFileType = this.extensions[0];

    isLoading: boolean;
    unknowColumns = {}

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
        this.context.resetContext();
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
        const isGermplasmListUpdateFormat = false;
        this.germplasmListService.downloadGermplasmTemplate(isGermplasmListUpdateFormat).subscribe((response) => {
            saveFile(response);
        });
    }

    next() {
        this.isLoading = true;
        this.validateFile().then((valid) => {
            this.isLoading = false;
            if (valid) {
                this.modal.close();
                const variables = [...this.context.newVariables, ...this.context.unknownVariableNames]

                if (variables && variables.length) {
                    const modalRef = this.modalService.open(GermplasmListVariableMatchesComponent as Component, { size: 'lg', backdrop: 'static' });
                    modalRef.result.then((variableMatchesResult) => {
                        if (variableMatchesResult) {
                            this.modalService.open(GermplasmListImportReviewComponent as Component, { size: 'lg', backdrop: 'static' });
                        } else {
                            this.modalService.open(GermplasmListImportComponent as Component, { size: 'lg', backdrop: 'static' });
                        }
                    });
                } else {
                    this.modalService.open(GermplasmListImportReviewComponent as Component, { size: 'lg', backdrop: 'static' });
                }
            }
        }, (res) => {
            this.isLoading = false;
            this.onError(res);
        });
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

        await this.processEntryDetailVariables();

        return true;
    }

    private async processEntryDetailVariables() {
        const unknownColumnNames = Object.keys(this.unknowColumns);
        if (unknownColumnNames.length) {
            const variablesFiltered = await this.variableService.filterVariables({
                variableNames: unknownColumnNames,
                variableTypeIds: [VariableTypeEnum.ENTRY_DETAILS.toString()]
            }).toPromise();

            this.context.unknownVariableNames = unknownColumnNames.filter((variableName) =>
                variablesFiltered.every((v) => toUpper(v.name) !== variableName && toUpper(v.alias) !== variableName)
            );

            this.context.newVariables = variablesFiltered;
        }
    }

    private validateHeader(fileHeader: string[], errorMessage: string[]) {
        // Ignore empty column headers
        fileHeader = fileHeader.filter((header) => !!header);
        this.unknowColumns = {};
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
        const templateHeader: string[] = [HEADERS.GUID, HEADERS.GID, HEADERS.DESIGNATION, HEADERS.ENTRY_CODE];
        fileHeader.filter((header) => templateHeader.indexOf(header) < 0)
            .forEach((header) => this.unknowColumns[header] = 1);
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
                if (row[HEADERS.ENTRY_CODE].length > 47) {
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
