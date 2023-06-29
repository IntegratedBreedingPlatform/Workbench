import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {PopupService} from '../../shared/modal/popup.service';
import {TranslateService} from '@ngx-translate/core';
import {AlertService} from '../../shared/alert/alert.service';
import {NgbActiveModal, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {GermplasmService} from '../../shared/germplasm/service/germplasm.service';
import {VariableService} from '../../shared/ontology/service/variable.service';
import {parseFile, saveFile} from '../../shared/util/file-utils';
import {HttpErrorResponse} from '@angular/common/http';
import {formatErrorList} from '../../shared/alert/format-error-list';
import {GermplasmListService} from '../../shared/germplasm-list/service/germplasm-list.service';
import {VariableTypeEnum} from '../../shared/ontology/variable-type.enum';
import {toUpper} from '../../shared/util/to-upper';
import {GermplasmListVariableMatchesComponent} from './germplasm-list-variable-matches.component';
import {ListComponent} from '../list.component';
import {JhiEventManager} from 'ng-jhipster';
import {ModalConfirmComponent} from '../../shared/modal/modal-confirm.component';
import {HELP_GERMPLASM_LIST_IMPORT_UPDATE} from '../../app.constants';
import {HelpService} from '../../shared/service/help.service';
import {EntryDetailsImportContext} from '../../shared/ontology/entry-details-import.context';
import {EntryDetailsImportService, HEADERS} from '../../shared/ontology/service/entry-details-import.service';
import {map} from "rxjs/operators";

@Component({
    selector: 'jhi-germplasm-list-import-update',
    templateUrl: 'germplasm-list-import-update.component.html'
})
export class GermplasmListImportUpdateComponent implements OnInit {
    helpLink: string;

    @ViewChild('fileUpload', {static: true})
    fileUpload: ElementRef;

    listId: number;
    fileName = '';

    rawData = new Array<any>();
    unknownColumns = {};

    extensions = ['.xls', '.xlsx'];
    selectedFileType = this.extensions[0];

    isLoading: boolean;

    constructor(
        private route: ActivatedRoute,
        private translateService: TranslateService,
        private alertService: AlertService,
        private modal: NgbActiveModal,
        private modalService: NgbModal,
        private germplasmService: GermplasmService,
        private variableService: VariableService,
        private context: EntryDetailsImportContext,
        private eventManager: JhiEventManager,
        private germplasmListService: GermplasmListService,
        private helpService: HelpService,
        private entryDetailsImportService: EntryDetailsImportService,
    ) {
        this.helpService.getHelpLink(HELP_GERMPLASM_LIST_IMPORT_UPDATE).subscribe((response) => {
            if (response.body) {
                this.helpLink = response.body;
            }
        });
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
            this.alertService.error('germplasm-list.import.file.validation.extensions', {param: this.extensions.join(', ')});
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
                    const modalRef = this.modalService.open(GermplasmListVariableMatchesComponent as Component, {
                        size: 'lg',
                        backdrop: 'static'
                    });
                    modalRef.result.then((variableMatchesResult) => {
                        if (variableMatchesResult) {
                            this.save(variableMatchesResult);
                        } else {
                            this.modalService.open(GermplasmListImportUpdateComponent as Component, {
                                size: 'lg',
                                backdrop: 'static'
                            });
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
        const germplasmListGenerator = {listId: id, entries: []};
        for (const row of this.context.data) {
            const entry = {
                entryNo: row[HEADERS.ENTRY_NO],
                data: Object.keys(variableMatchesResult).reduce((mapVal, variableName) => {
                    if (row[variableName]) {
                        mapVal[variableMatchesResult[variableName]] = {value: row[variableName]};
                    }
                    return mapVal;
                }, {})
            };
            germplasmListGenerator.entries.push(entry);
        }

        this.germplasmListService.germplasmListUpdates(germplasmListGenerator).subscribe(
            () => {
                this.isLoading = false;
                this.modal.close();
                this.eventManager.broadcast({name: id + ListComponent.GERMPLASM_LIST_CHANGED});
            },
            (error) => {
                this.isLoading = false;
                this.onError(error);
            }
        );

    }

    private async showSummaryConfirmation() {
        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component,
            {windowClass: 'modal-medium', backdrop: 'static'});
        confirmModalRef.componentInstance.message = this.translateService.instant('germplasm-list.import-updates.confirmation', {param: this.context.data.length});
        try {
            await confirmModalRef.result;
        } catch (rejected) {
            return false;
        }
        return true;
    }

    private async validateFile() {
        if (!this.entryDetailsImportService.normalizeHeaders(this.rawData)) {
            return false;
        }

        const headers = this.rawData[0];
        // Ignore empty column headers
        const fileHeaders = headers.filter((header) => !!header);
        this.context.data = this.getData();
        if (!this.entryDetailsImportService.validateFile(fileHeaders, this.context.data)) {
            return false;
        }

        // Gather unknown columns
        fileHeaders.filter((header) => Object.values(HEADERS).indexOf(header) < 0)
            .forEach((header) => this.unknownColumns[header] = 1);

        await this.processEntryDetailVariables();

        if (!this.hasVariables()) {
            this.alertService.error('germplasm-list.import.file.validation.entry.details.no.column');
            return false;
        }

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

            variablesOfTheList = await this.germplasmListService.getVariables(this.listId, VariableTypeEnum.ENTRY_DETAILS)
                .pipe(map((resp) => resp.body))
                .toPromise();

            this.context.variablesOfTheList = variablesFiltered.filter((variable) =>
                variablesOfTheList.some((v) => Number(v.id) === Number(variable.id))
            );

            this.context.unknownVariableNames = unknownColumnNames.filter((variableName) =>
                variablesFiltered.every((v) => toUpper(v.name) !== toUpper(variableName) && toUpper(v.alias) !== toUpper(variableName))
            );

            this.context.newVariables = variablesFiltered.filter((variable) =>
                this.context.variablesOfTheList.every((v) => Number(v.id) !== Number(variable.id))
            );
        }

    }

    private getData() {
        const headers = this.rawData[0].map((header) => toUpper(header));
        return this.rawData.slice(1).map((fileRow, rowIndex) => {
            return fileRow.reduce((mapVal, col, colIndex) => {
                const columnName = headers[colIndex];
                mapVal[columnName] = col;
                return mapVal;
            }, {});
        });
    }

    dismiss() {
        this.modal.dismiss();
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', {param: msg});
        } else {
            this.alertService.error('error.general', null, null);
        }
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
