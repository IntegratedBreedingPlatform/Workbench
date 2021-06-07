import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { JhiEventManager } from 'ng-jhipster';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { PopupService } from '../shared/modal/popup.service';
import { GermplasmService } from '../shared/germplasm/service/germplasm.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { formatErrorList } from '../shared/alert/format-error-list';
import { forkJoin, Observable } from 'rxjs';
import { parseFile, saveFile } from '../shared/util/file-utils';
import { AlertService } from '../shared/alert/alert.service';
import { Attribute } from '../shared/attributes/model/attribute.model';
import { NameType } from '../shared/germplasm/model/name-type.model';

@Component({
    selector: 'jhi-germplasm-import-update-dialog',
    templateUrl: './germplasm-import-update-dialog.component.html'
})
export class GermplasmImportUpdateDialogComponent implements OnInit, OnDestroy {

    @ViewChild('fileUpload')
    fileUpload: ElementRef;

    isProcessing = false;
    fileName = '';
    rawData: Array<Array<any>>;
    data: Array<any>;
    names: NameType[] = [];
    attributes: Attribute[] = [];
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
        this.isProcessing = true;
        this.validate().subscribe((isValid) => {
            if (isValid) {
                this.germplasmService.importGermplasmUpdates(this.transform(this.data, this.names, this.attributes)).subscribe(
                    (res: HttpResponse<any>) => this.onSaveSuccess(res.body),
                    (res: HttpErrorResponse) => this.onError(res)
                );
            } else {
                this.isProcessing = false;
            }
        });
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

    private transform(importData: Array<any>, names: NameType[], attributes: Attribute[]): any[] {
        // Transform file data to JSON
        const germplasmUpdates = importData.map((row) => {
            const namesValuesMap = {};
            const attributesValuesMap = {};
            const progenitorsValuesMap = {};

            names.forEach((name) => {
                namesValuesMap[name.code] = row[name.code];
            });
            attributes.forEach((attribute) => {
                attributesValuesMap[attribute.code] = row[attribute.code];
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

    private validate() {
        return new Observable((observer) => {

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
            forkJoin(this.germplasmService.getGermplasmAttributes(codes) // <-- FIXME into IBP-4659 (Remove it and using Resource /variables/filter)
                , this.germplasmService.getGermplasmNameTypes(codes))
                .subscribe((values) => {
                    this.attributes = values[0];
                    this.names = values[1];

                    if (!this.rawData || this.rawData.filter((row) => row.some((column) => Boolean(column.trim()))).length <= 1) {
                        this.alertService.error('germplasm-import-updates.validation.file.empty');
                        observer.next(false);
                        return;
                    }

                    const errorMessage: string[] = [];
                    this.validateHeader(headers, errorMessage, codes, this.names, this.attributes);
                    this.validateData(errorMessage);

                    if (errorMessage.length !== 0) {
                        this.alertService.error('error.custom', { param: formatErrorList(errorMessage) });
                        observer.next(false);
                        return;
                    }

                    observer.next(true);
                });
        });
    }

    private validateHeader(fileHeader: string[], errorMessage: string[], codes: string[], names: NameType[], attributes: Attribute[]) {
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

        const invalidCodes = codes.filter((code) => attributes.every((attribute) => attribute.code !== code) && names.every((name) => name.code !== code));
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
