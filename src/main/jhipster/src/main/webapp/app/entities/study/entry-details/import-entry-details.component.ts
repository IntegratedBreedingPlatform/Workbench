import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../../shared/modal/popup.service';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { VariableService } from '../../../shared/ontology/service/variable.service';
import { parseFile, saveFile } from '../../../shared/util/file-utils';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../../../shared/alert/format-error-list';
import { VariableTypeEnum } from '../../../shared/ontology/variable-type.enum';
import { toUpper } from '../../../shared/util/to-upper';
import { JhiAlertService, JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { ModalConfirmComponent } from '../../../shared/modal/modal-confirm.component';
import { EntryDetailsImportContext } from '../../../shared/ontology/entry-details-import.context';
import { StudyService } from '../../../shared/study/study.service';
import { DatasetVariable } from '../../../shared/study/dataset-variable';
import { EntryDetailsImportService, HEADERS } from '../../../shared/ontology/service/entry-details-import.service';
import { ModalAlertComponent } from '../../../shared/modal/modal-alert.component';

@Component({
    selector: 'jhi-import-entry-details',
    templateUrl: 'import-entry-details.component.html'
})
export class ImportEntryDetailsComponent implements OnInit {

    @ViewChild('fileUpload')
    fileUpload: ElementRef;

    studyId: number;
    fileName = '';

    rawData = new Array<any>();

    extensions = ['.xls', '.xlsx'];
    selectedFileType = this.extensions[0];

    isLoading: boolean;
    unknownColumns = {};

    isFileUploadMode = true;

    rows = [];
    page = 0;
    pageSize = 10;

    hasGeneratedDesign: boolean;

    constructor(
        private route: ActivatedRoute,
        private jhiLanguageService: JhiLanguageService,
        private translateService: TranslateService,
        private alertService: JhiAlertService,
        private modal: NgbActiveModal,
        private modalService: NgbModal,
        private variableService: VariableService,
        private studyService: StudyService,
        private context: EntryDetailsImportContext,
        private eventManager: JhiEventManager,
        private entryDetailsImportService: EntryDetailsImportService,
    ) {
    }

    ngOnInit(): void {
        this.context.resetContext();
        this.studyId = Number(this.route.snapshot.queryParamMap.get('studyId'));
        this.hasGeneratedDesign = this.route.snapshot.queryParamMap.get('hasGeneratedDesign') === 'true';
    }

    onFileChange(evt: any) {
        const target = evt.target;
        this.fileName = target.files[0].name;

        const extension = this.fileName.substring(this.fileName.lastIndexOf('.'));
        if (this.extensions.indexOf(extension.toLowerCase()) === -1) {
            this.fileName = '';
            target.value = '';
            this.alertService.error('study.import-entry-details.file.validation.extensions', { param: this.extensions.join(', ') });
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
        this.studyService.downloadImportTemplate('EntryDetails').subscribe((response) => {
            saveFile(response);
        });
    }

    next() {
        this.isLoading = true;
        this.validateFile().then((valid) => {
            this.isLoading = false;
            if (valid) {
                if (this.hasVariables()) {
                    this.showVariableMatches();
                } else {
                    this.save({});
                }
            }
        }, (res) => {
            this.isLoading = false;
            this.onError(res);
        });
    }

    back() {
        this.isFileUploadMode = true;
    }

    approveVariableMatches() {
        if (this.context.variableMatchesResult) {
            this.save(this.context.variableMatchesResult);
        } else {
            this.isFileUploadMode = true;
        }
    }

    showVariableMatches() {
        this.rows = [];
        this.rows = this.entryDetailsImportService.initializeVariableMatches();
        this.isFileUploadMode = false;
    }

    getAffectedEntriesNumber(): number {
        return this.context.data.filter((data) => {
            let count = 0;
            for (const property in data) {
                if (property !== HEADERS.ENTRY_NO && data[property]) {
                    count++;
                }
            }
            return count > 0;
        }).length;
    }

    async save(variableMatchesResult) {

        const affectedEntriesNumber: number = this.getAffectedEntriesNumber();
        if (affectedEntriesNumber === 0) {
            this.showNoValuesWarning();
            return;
        }

        const doContinue = await this.showSummaryConfirmation(affectedEntriesNumber);
        if (!doContinue) {
            return;
        }

        this.isLoading = true;
        const id = Number(this.route.snapshot.queryParamMap.get('studyId'));
        const studyEntries = [];
        const newVariables = [];

        for (const newVar of this.context.newVariables) {
            newVariables.push(new DatasetVariable(VariableTypeEnum.ENTRY_DETAILS,
                newVar.id, !newVar.alias ? newVar.name : newVar.alias));
        }

        for (const row of this.context.data) {
            const entryNo = row[HEADERS.ENTRY_NO];
            const variables = [];

            Object.keys(variableMatchesResult).forEach((variableName) => {
                variables.push({
                    variableId: variableMatchesResult[variableName],
                    value: row[variableName]
                });
            });

            studyEntries.push({ entryNumber: entryNo, data: variables });
        }

        this.studyService.importStudyEntries(id, studyEntries, newVariables).subscribe(
            () => {
                this.isLoading = false;
                this.modal.close();
                this.eventManager.broadcast({ name: id + 'StudyEntryDetailsChanged' });
                this.handleImportSuccess();
            },
            (error) => {
                this.isLoading = false;
                this.isFileUploadMode = true;
                this.onError(error);
            }
        );

    }

    private async showSummaryConfirmation(affectedEntries: number) {
        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component,
            { windowClass: 'modal-medium', backdrop: 'static' });
        confirmModalRef.componentInstance.message = this.translateService.instant('study.import-entry-details.confirmation',
            { param: affectedEntries });
        try {
            await confirmModalRef.result;
        } catch (rejected) {
            return false;
        }
        return true;
    }

    private async showNoValuesWarning() {
        const modal = this.modalService.open(ModalAlertComponent as Component,
            { windowClass: 'modal-medium', backdrop: 'static' });
        modal.componentInstance.message = this.translateService.instant('study.import-entry-details.no-entries-values');
        modal.componentInstance.showCancelButton = false;
    }

    private async validateFile() {
        if (!this.entryDetailsImportService.normalizeHeaders(this.rawData)) {
            return false;
        }

        const headers = this.rawData[0];
        // Ignore empty column headers
        const fileHeaders = headers.filter((header) => !!header);
        const data = this.getData(false);

        if (!this.entryDetailsImportService.validateFile(fileHeaders, data)) {
            return false;
        }

        // Gather unknown columns
        fileHeaders.filter((header) => Object.values(HEADERS).indexOf(header) < 0)
            .forEach((header) => this.unknownColumns[header] = 1);

        await this.processEntryDetailVariables();

        if (!this.hasVariables()) {
            this.alertService.error('study.import-entry-details.file.validation.entry.details.no.column');
            return false;
        }

        this.context.data = this.getData(true);

        return true;
    }

    private async processEntryDetailVariables() {
        const unknownColumnNames = Object.keys(this.unknownColumns);
        let variablesOfTheList = [],
            variablesFiltered = [];

        if (unknownColumnNames.length) {
            variablesFiltered = await this.variableService.filterVariables({
                variableNames: unknownColumnNames,
                variableTypeIds: [VariableTypeEnum.ENTRY_DETAILS.toString()]
            }).toPromise();

            variablesOfTheList = await this.variableService.getStudyEntryVariables(this.studyId, VariableTypeEnum.ENTRY_DETAILS)
                .map((resp) => resp.body)
                .toPromise();

            this.context.variablesOfTheList = variablesFiltered.filter((variable) =>
                (!this.hasGeneratedDesign || this.hasGeneratedDesign && !variable.metadata.usage.systemTerm) &&
                    variablesOfTheList.some((v) => Number(v.id) === Number(variable.id))
            );

            this.context.unknownVariableNames = unknownColumnNames.filter((variableName) =>
                variablesFiltered.every((v) => toUpper(v.name) !== toUpper(variableName) && toUpper(v.alias) !== toUpper(variableName))
            );

            this.context.newVariables = variablesFiltered.filter((variable) =>
                (!this.hasGeneratedDesign || this.hasGeneratedDesign && !variable.metadata.usage.systemTerm) &&
                    this.context.variablesOfTheList.every((v) => Number(v.id) !== Number(variable.id))
            );

            if (this.hasGeneratedDesign) {
                this.context.skipVariables = variablesFiltered.filter((variable) => variable.metadata.usage.systemTerm);
            }
        }

    }

    handleImportSuccess() {
        // Handle selection when this page is loaded outside Angular.
        if ((<any>window.parent).handleImportSuccess) {
            (<any>window.parent).handleImportSuccess();
        }
        if ((<any>window.parent)) {
            (<any>window.parent).postMessage({ name: 'import-success', 'value': '' }, '*');
        }
    }

    dismiss() {
        // Handle closing of modal when this page is loaded outside of Angular.
        if ((<any>window.parent).closeModal) {
            (<any>window.parent).closeModal();
        }
        if ((<any>window.parent)) {
            (<any>window.parent).postMessage({ name: 'cancel', 'value': '' }, '*');
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

    private hasVariables() {
        return [
            ...this.context.newVariables,
            ...this.context.unknownVariableNames,
            ...this.context.variablesOfTheList,
            ...this.context.skipVariables
        ].length;
    }

    private getData(cleanData: boolean) {
        const headers = this.rawData[0].map((header) => toUpper(header));
        return this.rawData.slice(1).map((fileRow, rowIndex) => {
            return fileRow.reduce((map, col, colIndex) => {
                const columnName = headers[colIndex];
                const shouldAddData = !(this.context.skipVariables.filter((variable) => variable.name === columnName).length > 0 ||
                    this.context.unknownVariableNames.includes(columnName));
                if (!cleanData || shouldAddData) {
                    map[columnName] = col;
                }
                return map;
            }, {});
        });
    }

}

@Component({
    selector: 'jhi-import-entry-details-popup',
    template: ''
})
export class ImportEntryDetailsPopupComponent implements OnInit, OnDestroy {

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
            this.popupService.open(ImportEntryDetailsComponent as Component);
        });
    }
}
