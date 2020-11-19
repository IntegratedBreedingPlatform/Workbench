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
import { GermplasmNameTypeModel } from '../entities/germplasm/germplasm-name-type.model';
import { GermplasmAttributeModel } from '../entities/germplasm/germplasm-attribute.model';
import { forkJoin, Observable } from 'rxjs';

@Component({
    selector: 'jhi-germplasm-import-update-dialog',
    templateUrl: './germplasm-import-update-dialog.component.html'
})
export class GermplasmImportUpdateDialogComponent implements OnInit, OnDestroy {

    @ViewChild('fileUpload')
    fileUpload: ElementRef;

    fileName = '';
    importData = new Array<string[]>();
    names: GermplasmNameTypeModel[] = [];
    attributes: GermplasmAttributeModel[] = [];

    private readonly GID_COLUMN = 'GID';
    private readonly GUID_COLUMN = 'GUID';
    private readonly PREFERRED_NAME_COLUMN = 'PREFERRED NAME';
    private readonly LOCATION_ABBR_COLUMN = 'LOCATION ABBR';
    private readonly REFERENCE_COLUMN = 'REFERENCE';
    private readonly CREATION_DATE = 'CREATION DATE';
    private readonly EXPECTED_HEADERS = [this.GID_COLUMN, this.GUID_COLUMN, this.PREFERRED_NAME_COLUMN, this.LOCATION_ABBR_COLUMN, this.REFERENCE_COLUMN, this.CREATION_DATE];

    importFormats = [
        { name: 'Excel', extension: '.xls,.xlsx' }
    ];
    selectedFileType = this.importFormats[0].extension; // '.xls';

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
        this.validate().subscribe((isValid) => {
            if (isValid) {
                this.germplasmService.importGermplasmUpdates(this.transform(this.importData, this.names, this.attributes)).subscribe(
                    (res: HttpResponse<any>) => this.onSaveSuccess(null),
                    (res: HttpErrorResponse) => this.onError(res)
                );
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
            this.alertService.error('error.custom', { param: 'The import germplasm updates is only available for Excel extension xls and xlsx' }, null);
            return;
        }

        this.fileUpload.nativeElement.innerText = target.files[0].name;

        this.excelService.parse(target, 'Observation').subscribe((value) => {
            this.importData = value;
        }, (error) => {
            this.fileName = '';
            target.value = '';
            this.alertService.error('error.custom', { param: error }, null);
        });
    }

    private validateHeader(fileHeader: string[], errorMessage: string[], codes: string[], names: GermplasmNameTypeModel[], attributes: GermplasmAttributeModel[]) {
        // TODO: Add Method Abbr once implemented in the backend.
        if (!this.EXPECTED_HEADERS.every((expectedHeader) => {
            return fileHeader.find((header) => expectedHeader.toLowerCase() === header.toLowerCase()) !== undefined;
        })) {
            errorMessage.push(this.translateService.instant('error.import.header.mandatory', { param: this.EXPECTED_HEADERS.join(', ') }));
        }

        const duplicateColumns = fileHeader.filter((header, i) => fileHeader.indexOf(header) !== i)
        if (duplicateColumns.length > 1) {
            errorMessage.push(this.translateService.instant('error.import.header.duplicated', { param: duplicateColumns.join(', ') }));
        }

        const invalidCodes = codes.filter((code) => attributes.every((attribute) => attribute.code !== code) && names.every((name) => name.code !== code));
        if (invalidCodes && invalidCodes.length > 0) {
            errorMessage.push(this.translateService.instant('germplasm-import-updates.validation.invalid.codes', { param: invalidCodes.join(', ') }));
        }
    }

    private transform(importData: Array<string[]>, names: GermplasmNameTypeModel[], attributes: GermplasmAttributeModel[]): any[] {
        const germplasmUpdateList: any[] = [];

        const rowHeader = Object.assign([], importData[0]);
        const gidIndex = rowHeader.indexOf(this.GID_COLUMN);
        const guidIndex = rowHeader.indexOf(this.GUID_COLUMN);
        const preferredNameIndex = rowHeader.indexOf(this.PREFERRED_NAME_COLUMN);
        const locationAbbrIndex = rowHeader.indexOf(this.LOCATION_ABBR_COLUMN);
        const referenceIndex = rowHeader.indexOf(this.REFERENCE_COLUMN);
        const creationDateIndex = rowHeader.indexOf(this.CREATION_DATE);

        // Skip header
        importData.splice(0, 1);

        // Transform file data to JSON
        importData.forEach((rowData) => {
            const namesValuesMap = {};
            const attributesValuesMap = {};

            names.forEach((name) => {
                namesValuesMap[name.code] = rowData[rowHeader.indexOf(name.code)];
            });
            attributes.forEach((attribute) => {
                attributesValuesMap[attribute.code] = rowData[rowHeader.indexOf(attribute.code)];
            });

            const germplasmUpdate = {
                gid: rowData[gidIndex],
                germplasmUUID: rowData[guidIndex],
                preferredName: rowData[preferredNameIndex],
                locationAbbreviation: rowData[locationAbbrIndex],
                creationDate: rowData[creationDateIndex],
                // TODO: Implement Breeding Method Update
                breedingMethodAbbr: null,
                reference: rowData[referenceIndex],
                names: namesValuesMap,
                attributes: attributesValuesMap
            };
            germplasmUpdateList.push(germplasmUpdate);
        });

        return germplasmUpdateList;
    }

    private validate() {
        return new Observable((observer) => {
            // Anything outside expected headers are considered codes.
            const codes: string[] = this.importData[0].filter((column) =>
                this.EXPECTED_HEADERS.indexOf(column.toUpperCase()) < 0
            );
            forkJoin(this.germplasmService.getGermplasmAttributes(codes), this.germplasmService.getGermplasmNameTypes(codes))
                .subscribe((values) => {
                    this.attributes = values[0];
                    this.names = values[1];

                    if (!this.importData || this.importData.filter((row) => row.some((column) => Boolean(column.trim()))).length <= 1) {
                        this.alertService.error('germplasm-import-updates.validation.file.empty');
                        observer.next(false);
                        return;
                    }

                    const errorMessage: string[] = [];
                    this.validateHeader(this.importData[0], errorMessage, codes, this.names, this.attributes);

                    if (errorMessage.length !== 0) {
                        this.alertService.error('error.custom', { param: formatErrorList(errorMessage) });
                        observer.next(false);
                        return;
                    }

                    observer.next(true);
                });
        });
    }

    private onSaveSuccess(result: any) {
        this.alertService.success('germplasm-import-updates.import.success', null, null);
        this.eventManager.broadcast({ name: 'columnFiltersChanged', content: 'OK' });
        this.activeModal.close(result);
    }

    private onError(res) {
        if (res && res.error) {
            this.alertService.error('error.custom', { param: formatErrorList(res.error.errors) }, null);
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
