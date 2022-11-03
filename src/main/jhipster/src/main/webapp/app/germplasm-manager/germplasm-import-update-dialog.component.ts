import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { JhiEventManager } from 'ng-jhipster';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { PopupService } from '../shared/modal/popup.service';
import { GermplasmService } from '../shared/germplasm/service/germplasm.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { formatErrorList } from '../shared/alert/format-error-list';
import { parseFile, saveFile } from '../shared/util/file-utils';
import { AlertService } from '../shared/alert/alert.service';
import { NameType } from '../shared/germplasm/model/name-type.model';
import { VariableService } from '../shared/ontology/service/variable.service';
import { VariableDetails } from '../shared/ontology/model/variable-details';
import { toUpper } from '../shared/util/to-upper';
import { VariableValidationService, VariableValidationStatusType } from '../shared/ontology/service/variable-validation.service';
import { GermplasmImportUpdateDescriptorsConfirmationDialogComponent } from './germplasm-import-update-descriptors-confirmation-dialog.component';
import { VariableTypeEnum } from '../shared/ontology/variable-type.enum';
import { HelpService } from '../shared/service/help.service';
import { HELP_MANAGE_GERMPLASM_IMPORT_UPDATES, HELP_MANAGE_GERMPLASM_IMPORT_UPDATES_TEMPLATE } from '../app.constants';

@Component({
    selector: 'jhi-germplasm-import-update-dialog',
    templateUrl: './germplasm-import-update-dialog.component.html'
})
export class GermplasmImportUpdateDialogComponent implements OnInit, OnDestroy {
    helpLink: string;
    templateHelpLink: string;

    @ViewChild('fileUpload')
    fileUpload: ElementRef;

    isProcessing = false;
    fileName = '';
    rawData: Array<Array<any>>;
    data: Array<any>;
    names: NameType[] = [];
    attributes: VariableDetails[] = [];
    attributeStatusById: { [key: number]: VariableValidationStatusType; } = {};
    importFormats = [
        { name: 'Excel', extension: '.xls,.xlsx' }
    ];
    selectedFileType = this.importFormats[0].extension; // '.xls';

    constructor(
        private alertService: AlertService,
        public activeModal: NgbActiveModal,
        private modalService: NgbModal,
        private eventManager: JhiEventManager,
        private translateService: TranslateService,
        private germplasmService: GermplasmService,
        private variableService: VariableService,
        private variableValidationService: VariableValidationService,
        private helpService: HelpService,
    ) {
        this.helpService.getHelpLink(HELP_MANAGE_GERMPLASM_IMPORT_UPDATES).subscribe((response) => {
            if (response.body) {
                this.helpLink = response.body;
            }
        });
        this.helpService.getHelpLink(HELP_MANAGE_GERMPLASM_IMPORT_UPDATES_TEMPLATE).subscribe((response) => {
            if (response.body) {
                this.templateHelpLink = response.body;
            }
        });
    }

    ngOnDestroy(): void {
    }

    ngOnInit(): void {
    }

    close() {
        this.activeModal.dismiss('cancel');
    }

    async import() {
        this.isProcessing = true;
        const isValid = await this.validate();
        if (!isValid) {
            this.isProcessing = false;
            return;
        }
        try {
            await this.confirmAttributeStatus();
        } catch (e) {
            this.isProcessing = false;
            return;
        }
        this.germplasmService.importGermplasmUpdates(this.transform(this.data, this.names, this.attributes)).subscribe(
            (res: HttpResponse<any>) => this.onSaveSuccess(res.body),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    onFileChange(evt: any) {
        const target = evt.target;
        this.fileName = target.files[0].name;

        const extension = this.fileName.split('.');
        if (extension[1].toLowerCase() !== 'xls' && extension[1].toLowerCase() !== 'xlsx') {
            this.fileName = '';
            target.value = '';
            this.alertService.error('error.custom', { param: 'The import germplasm updates is only available for Excel extension xls and xlsx' });
            return;
        }

        this.fileUpload.nativeElement.innerText = target.files[0].name;

        parseFile(target.files[0], 'Observation').subscribe((value) => {
            this.rawData = value;
            target.value = '';
        }, (error) => {
            this.fileName = '';
            target.value = '';
            this.alertService.error('error.custom', { param: error });
        });
    }

    private transform(importData: Array<any>, names: NameType[], attributes: VariableDetails[]): any[] {
        // Transform file data to JSON
        const germplasmUpdates = importData.map((row) => {
            const namesValuesMap = {};
            const attributesValuesMap = {};
            const progenitorsValuesMap = {};

            names.forEach((name) => {
                namesValuesMap[toUpper(name.code)] = row[toUpper(name.code)];
            });
            attributes.forEach((attribute) => {
                if (row[toUpper(attribute.name)]) {
                    attributesValuesMap[attribute.name] = row[toUpper(attribute.name)];
                } else if (row[toUpper(attribute.alias)]) {
                    attributesValuesMap[attribute.alias] = row[toUpper(attribute.alias)];
                }
            });

            return {
                gid: row[HEADER['GID']],
                germplasmUUID: row[HEADER['GUID']],
                preferredNameType: row[HEADER['PREFERRED NAME']],
                locationAbbreviation: row[HEADER['LOCATION ABBR']],
                creationDate: row[HEADER['CREATION DATE']],
                breedingMethodAbbr: row[HEADER['BREEDING METHOD']],
                progenitors: {
                    'PROGENITOR 1': row[HEADER['PROGENITOR 1']],
                    'PROGENITOR 2': row[HEADER['PROGENITOR 2']]
                },
                reference: row[HEADER['REFERENCE']],
                names: namesValuesMap,
                attributes: attributesValuesMap
            };
        });

        return germplasmUpdates;
    }

    private async validate() {

        // normalize headers
        this.rawData[0] = this.rawData[0].map((header) => header.toUpperCase());
        const headers = this.rawData[0];

        // Convert rawData into map
        this.data = this.rawData.slice(1).map((fileRow, rowIndex) => {
            return fileRow.reduce((map, col, colIndex) => {
                map[headers[colIndex]] = col;
                return map;
            }, {});
        });

        // Anything outside expected headers are considered codes.
        const codes: string[] = headers.filter((column) =>
            !(<any>Object).values(HEADER).includes(column)
        );

        // Determine which of the codes are names and attributes

        this.attributes = await this.variableService.filterVariables({
            variableNames: codes,
            variableTypeIds: [VariableTypeEnum.GERMPLASM_ATTRIBUTE.toString(), VariableTypeEnum.GERMPLASM_PASSPORT.toString()],
            showObsoletes: true
        }).toPromise();
        this.names = await this.germplasmService.getGermplasmNameTypes(codes).toPromise();

        if (!this.rawData || this.rawData.filter((row) => row.some((column) => Boolean(column.trim()))).length <= 1) {
            this.alertService.error('germplasm-import-updates.validation.file.empty');
            return false;
        }

        const errorMessage: string[] = [];
        this.validateHeader(headers, errorMessage, codes, this.names, this.attributes);
        this.validateData(errorMessage);

        if (errorMessage.length !== 0) {
            this.alertService.error('error.custom', { param: formatErrorList(errorMessage) });
            return false;
        }

        return true;
    }

    private validateHeader(fileHeader: string[], errorMessage: string[], codes: string[], names: NameType[], attributes: VariableDetails[]) {
        // TODO: Add Method Abbr once implemented in the backend.

        if (!(<any>Object).values(HEADER).every((header) => {
            return fileHeader.includes(header);
        })) {
            errorMessage.push(this.translateService.instant('error.import.header.mandatory', { param: (<any>Object).values(HEADER).join(', ') }));
        }

        const duplicateColumns = fileHeader.filter((header, i) => fileHeader.indexOf(header) !== i);
        if (duplicateColumns.length > 1) {
            errorMessage.push(this.translateService.instant('error.import.header.duplicated', { param: duplicateColumns.join(', ') }));
        }

        // TODO See GermplasmImportComponent.showUnknownColumnsWarning
        const invalidCodes = codes.filter((code) =>
            attributes.every((attribute) => toUpper(attribute.alias) !== code && toUpper(attribute.name) !== code)
            && names.every((name) => toUpper(name.code) !== code)
        );
        if (invalidCodes && invalidCodes.length > 0) {
            errorMessage.push(this.translateService.instant('germplasm-import-updates.validation.invalid.codes', { param: invalidCodes.join(', ') }));
        }
    }

    private validateData(errorMessage: string[]) {
        // row validations
        for (const row of this.data) {
            // Progenitors
            if ((row[HEADER['PROGENITOR 1']] && !Number.isInteger(Number(row[HEADER['PROGENITOR 1']])) || row[HEADER['PROGENITOR 1']] < 0) ||
                (row[HEADER['PROGENITOR 2']] && !Number.isInteger(Number(row[HEADER['PROGENITOR 2']])) || row[HEADER['PROGENITOR 2']] < 0)
            ) {
                errorMessage.push(this.translateService.instant('germplasm-import-updates.validation.invalid.progenitors'));
                break;
            }
            if (Boolean(row[HEADER['PROGENITOR 1']]) !== Boolean(row[HEADER['PROGENITOR 2']])) {
                errorMessage.push(this.translateService.instant('germplasm-import-updates.validation.progenitors.must.be.both.defined'));
                break;
            }
        }
    }

    private onSaveSuccess(result: any) {
        this.isProcessing = false;
        this.alertService.success('germplasm-import-updates.import.success');
        this.eventManager.broadcast({ name: 'filterByGid', content: result });
        this.activeModal.close(result);
    }

    private onError(res) {
        this.isProcessing = false;
        if (res && res.error) {
            this.alertService.error('error.custom', { param: formatErrorList(res.error.errors) });
            return;
        }
        this.alertService.error('error.general');
    }

    export($event) {
        $event.preventDefault();
        this.germplasmService.downloadGermplasmTemplate(true).subscribe((response) => {
            saveFile(response);
        });
    }

    private async confirmAttributeStatus() {
        this.computeAttributeStatus();
        const invalidAttributeIds = Object.entries(this.attributeStatusById).filter((entry) => entry[1])
            .map((entry) => entry[0]);
        if (invalidAttributeIds.length) {
            const invalidAttributes = this.attributes.filter((attribute) => invalidAttributeIds.includes(attribute.id));
            const modalRef = this.modalService.open(GermplasmImportUpdateDescriptorsConfirmationDialogComponent,
                { size: 'lg', backdrop: 'static' });
            modalRef.componentInstance.attributeStatusById = this.attributeStatusById;
            modalRef.componentInstance.attributes = invalidAttributes;
            return modalRef.result;
        }
        return true;
    }

    computeAttributeStatus() {
        this.attributeStatusById = {};
        this.attributes.forEach((attribute) => {
            this.data.some((row) => {
                const value = row[toUpper(attribute.alias)] || row[toUpper(attribute.name)];
                const validationStatus = this.variableValidationService.isValidValue(value, attribute);
                if (!validationStatus.isValid || !validationStatus.isInRange) {
                    this.attributeStatusById[attribute.id] = validationStatus;
                }
                // continue processing each row unless we found some invalid, in which case the whole column is invalid
                return !validationStatus.isValid;
            });
        })
    }
}

@Component({
    selector: 'jhi-germplasm-import-update-popup',
    template: ''
})
export class GermplasmImportUpdatePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(private alertService: AlertService,
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

enum HEADER {
    'GID' = 'GID',
    'GUID' = 'GUID',
    'PREFERRED NAME' = 'PREFERRED NAME',
    'LOCATION ABBR' = 'LOCATION ABBR',
    'REFERENCE' = 'REFERENCE',
    'CREATION DATE' = 'CREATION DATE',
    'BREEDING METHOD' = 'BREEDING METHOD',
    'PROGENITOR 1' = 'PROGENITOR 1',
    'PROGENITOR 2' = 'PROGENITOR 2'
}
