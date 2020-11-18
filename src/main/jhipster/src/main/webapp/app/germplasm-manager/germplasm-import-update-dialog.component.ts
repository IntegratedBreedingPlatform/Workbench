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
import { forkJoin } from 'rxjs';

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
    importHeader = [];

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
        if (this.validate()) {
            // Anything outside expected headers are considered codes.
            const codes: string[] = this.importData[0].filter((column) =>
                this.EXPECTED_HEADERS.indexOf(column.toUpperCase()) < 0
            );
            forkJoin(this.germplasmService.getGermplasmAttributes(codes), this.germplasmService.getGermplasmNameTypes(codes))
                .subscribe((values) => {
                    this.attributes = values[0];
                    this.names = values[1];
                    this.germplasmService.importGermplasmUpdates(this.transform(this.importData, this.names, this.attributes)).subscribe(
                        (res: HttpResponse<any>) => this.onSaveSuccess(null),
                        (res: HttpErrorResponse) => this.onError(res)
                    );
                });
        }
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
            this.alertService.error('error.custom', { param: error }, null);
        });
    }

    private validateHeader(fileHeader: string[], errorMessage: string[]) {
        this.importHeader = [];
        // TODO: Add Method Abbr once implemented in the backend.
        const additionalColumns = [];
        this.EXPECTED_HEADERS.forEach((header) => {
            const result = fileHeader.filter((column) =>
                column.toLowerCase() === header.toLowerCase()
            );

            if (result.length === 0) {
                errorMessage.push(this.translateService.instant('error.import.header.mandatory', { param: this.EXPECTED_HEADERS.join(', ') }));
            }

            if (result.length > 1) {
                errorMessage.push(this.translateService.instant('error.import.header.duplicated', { param: header }));
            }

            const idx = fileHeader.indexOf(result[0]);
            this.importHeader.push(idx);
        });
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
        importData.forEach(rowData => {
            const namesValuesMap = {};
            const attributesValuesMap = {};

            this.names.forEach(name => {
                namesValuesMap[name.code] = rowData[rowHeader.indexOf(name.code)];
            });
            this.attributes.forEach(attribute => {
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
        if (!this.importData || this.importData.filter((row) => row.some((column) => Boolean(column.trim()))).length <= 1) {
            this.alertService.error('germplasm-import-updates.validation.file.empty');
            return false;
        }

        const errorMessage: string[] = [];
        this.validateHeader(this.importData[0], errorMessage);

        if (errorMessage.length !== 0) {
            this.alertService.error('error.custom', { param: formatErrorList(errorMessage) });
            return false;
        }
        return true;
    }

    private onSaveSuccess(result: any) {
        this.alertService.success('germplasm-import-updates.import.success', null, null);
        this.eventManager.broadcast({ name: 'columnFiltersChanged', content: 'OK' });
        this.activeModal.close(result);
    }

    private onError(error) {
        if (error) {
            this.alertService.error('error.custom', { param: error.message }, null);
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
