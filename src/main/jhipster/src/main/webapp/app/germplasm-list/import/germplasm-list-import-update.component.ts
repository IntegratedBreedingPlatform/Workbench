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
import { GermplasmListVariableMatchesComponent } from './germplasm-list-variable-matches.component';
import { ListComponent } from '../list.component';
import { JhiEventManager } from 'ng-jhipster';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';

@Component({
    selector: 'jhi-germplasm-list-import-update',
    templateUrl: 'germplasm-list-import-update.component.html'
})
export class GermplasmListImportUpdateComponent implements OnInit {

    @ViewChild('fileUpload')
    fileUpload: ElementRef;

    listId: number;
    fileName = '';

    rawData = new Array<any>();
    unknownColumn = {};

    extensions = ['.xls', '.xlsx'];
    selectedFileType = this.extensions[0];

    isLoading: boolean;
    unknowColumns = {};

    constructor(
        private route: ActivatedRoute,
        private translateService: TranslateService,
        private alertService: AlertService,
        private modal: NgbActiveModal,
        private modalService: NgbModal,
        private germplasmService: GermplasmService,
        private variableService: VariableService,
        private context: GermplasmListImportContext,
        private eventManager: JhiEventManager,
        private germplasmListService: GermplasmListService
    ) {
    }

    ngOnInit(): void {
        this.context.resetContext();
        this.listId = Number(this.route.snapshot.queryParamMap.get('listId'));
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
        const isGermplasmListUpdateFormat = true;
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
                if (this.hasVariables()) {
                    const modalRef = this.modalService.open(GermplasmListVariableMatchesComponent as Component, { size: 'lg', backdrop: 'static' });
                    modalRef.result.then((variableMatchesResult) => {
                        if (variableMatchesResult) {
                            this.save(variableMatchesResult);
                        } else {
                            this.modalService.open(GermplasmListImportUpdateComponent as Component, { size: 'lg', backdrop: 'static' });
                        }
                    });
                } else {
                    this.save({});
                }
            }
        }, (res) => {
            this.isLoading = false;
            this.onError(res);
        });
    }

    async save(variableMatchesResult) {

        const doContinue = await this.showSummaryConfirmation();
        if (!doContinue) {
            return;
        }

        this.isLoading = true;
        const id = Number(this.route.snapshot.queryParamMap.get('listId'));
        const germplasmListGenerator = { listId: id, entries: [] };
        for (const row of this.context.data) {
            const entry = {
                entryNo: row[HEADERS.ENTRY_NO],
                // Temporary workaround to allow users to edit ENTRY_CODE
                entryCode: row[HEADERS.ENTRY_CODE],
                data: Object.keys(variableMatchesResult).reduce((map, variableName) => {
                    if (row[variableName]) {
                        map[variableMatchesResult[variableName]] = { value: row[variableName] };
                    }
                    return map;
                }, {})
            };
            germplasmListGenerator.entries.push(entry);
        }

        this.germplasmListService.germplasmListUpdates(germplasmListGenerator).subscribe(
            () => {
                this.isLoading = false;
                this.modal.close();
                this.eventManager.broadcast({ name: id + ListComponent.GERMPLASM_LIST_CHANGED });
            },
            (error) => {
                this.isLoading = false;
                this.onError(error);
            }
        );

    }

    private async showSummaryConfirmation() {
        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component,
            { windowClass: 'modal-medium', backdrop: 'static' });
        confirmModalRef.componentInstance.message = this.translateService.instant('germplasm-list.import-updates.confirmation', {param: this.context.data.length});
        try {
            await confirmModalRef.result;
        } catch (rejected) {
            return false;
        }
        return true;
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

        if (errorMessage.length) {
            this.alertService.error('error.custom', { param: formatErrorList(errorMessage) });
            return false;
        }

        await this.processEntryDetailVariables();

        if (!this.hasEntryCode() && !this.hasVariables()) {
            this.alertService.error('germplasm-list.import.file.validation.entry.details.no.column');
            return false;
        }

        return true;
    }

    private async processEntryDetailVariables() {
        const unknownColumnNames = Object.keys(this.unknowColumns);
        let variablesOfTheList = [],
            variablesFiltered = [];

        if (unknownColumnNames.length) {
            variablesFiltered = await this.variableService.filterVariables({
                variableNames: unknownColumnNames,
                variableTypeIds: [VariableTypeEnum.ENTRY_DETAILS.toString()]
            }).toPromise();

            variablesOfTheList = await this.germplasmListService.getVariables(this.listId, VariableTypeEnum.ENTRY_DETAILS)
                .map((resp) => resp.body)
                .toPromise();

            this.context.variablesOfTheList = variablesFiltered.filter((variable) =>
                variablesOfTheList.some((v) => Number(v.id) === Number(variable.id))
            );

            this.context.unknownVariableNames = unknownColumnNames.filter((variableName) =>
                variablesFiltered.every((v) => toUpper(v.name) !== variableName && toUpper(v.alias) !== variableName)
            );

            this.context.newVariables = variablesFiltered.filter((variable) =>
                this.context.variablesOfTheList.every((v) => Number(v.id) !== Number(variable.id))
            );
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
        fileHeader.filter((header) => Object.values(HEADERS).indexOf(header) < 0)
            .forEach((header) => this.unknowColumns[header] = 1);
    }

    dismiss() {
        this.modal.dismiss();
    }

    private validateData(errorMessage: string[]) {
        // row validations
        for (const row of this.context.data) {
            if (!row[HEADERS.ENTRY_NO]) {
                errorMessage.push(this.translateService.instant('germplasm-list.import.file.validation.entry.no'));
                break;
            }

            if (row[HEADERS.ENTRY_NO] && (isNaN(row[HEADERS.ENTRY_NO])
                || !Number.isInteger(Number(row[HEADERS.ENTRY_NO])) || row[HEADERS.ENTRY_NO] < 0)) {
                errorMessage.push(this.translateService.instant('germplasm-list.import.file.validation.entry.no.format'));
                break;
            }
        }

        if (!errorMessage.length && this.context.data.map((row) => row[HEADERS.ENTRY_NO]).some((cell, i, col) => cell.length && col.indexOf(cell) !== i)) {
            errorMessage.push(this.translateService.instant('germplasm-list.import.file.validation.entry.no.duplicates'));
        }
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general', null, null);
        }
    }

    private hasEntryCode() {
        return this.context.data.some((row) => row[HEADERS.ENTRY_CODE]);
    }

    private hasVariables() {
        return [
            ...this.context.newVariables,
            ...this.context.unknownVariableNames,
            ...this.context.variablesOfTheList
        ].length;
    }
}

@Component({
    selector: 'jhi-germplasm-list-import-update-popup',
    template: ''
})
export class GermplasmListImportUpdatePopupComponent implements OnInit, OnDestroy {

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
            this.popupService.open(GermplasmListImportUpdateComponent as Component);
        });
    }
}

export enum HEADERS {
    'ENTRY_NO' = 'ENTRY_NO',
    'ENTRY_CODE' = 'ENTRY_CODE'
}
