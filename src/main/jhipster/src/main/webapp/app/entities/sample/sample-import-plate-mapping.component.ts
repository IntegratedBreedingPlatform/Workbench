import { Component, Input, OnInit } from '@angular/core';
import { SampleContext } from './sample.context';
import { JhiEventManager } from 'ng-jhipster';
import { SampleListService } from './sample-list.service';
import { AlertService } from '../../shared/alert/alert.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { SampleImportPlateComponent } from './sample-import-plate.component';
import { TranslateService } from '@ngx-translate/core';
import { formatErrorList } from '../../shared/alert/format-error-list';

@Component({
    selector: 'jhi-sample-import-plate-mapping',
    templateUrl: './sample-import-plate-mapping.component.html'
})

export class SampleImportPlateMappingComponent implements OnInit  {

    @Input() importData: Array<Array<any>>;
    header: Array<any>;
    sampleIdMapping;
    plateIdMapping;
    wellMapping;

    constructor(private alertService: AlertService,
                private sampleContext: SampleContext,
                private sampleListService: SampleListService,
                private eventManager: JhiEventManager,
                public activeModal: NgbActiveModal,
                private modalService: NgbModal,
                private translateService: TranslateService) {
    }

    ngOnInit(): void {
        this.header = this.importData[0];
        this.sampleIdMapping = this.mappingHeader(this.importData[0], 'SAMPLE_UID');
        this.plateIdMapping = this.mappingHeader(this.importData[0], 'PLATE_ID');
        this.wellMapping = this.mappingHeader(this.importData[0], 'WELL');

    }

    mappingHeader(header: Array<string>, mapping: string) {
        for (const column of header) {
            if (column.toLowerCase() === mapping.toLowerCase()) {
                return column;
            }
        }
        return '';
    }

    proceed() {
        const activeListId = this.sampleContext.activeList.id;

        if (this.validate()) {
            const sampleList = this.buildSampleList();
            this.sampleListService.importPlateInfo(activeListId, sampleList).subscribe((observer) => {
                this.close();
                // Refresh the sample list table.
                this.eventManager.broadcast({ name: 'sampleListModification', content: '' });
                this.alertService.success('bmsjHipsterApp.sample.importPlate.success');
            }, (response) => {
                if (response.error.errors[0].message) {
                    this.alertService.error('bmsjHipsterApp.sample.error', { param: response.error.errors[0].message });
                } else {
                    this.alertService.error('bmsjHipsterApp.sample.importPlate.error');
                }
            });

        }

    }

    reset() {
        this.sampleIdMapping = '';
        this.plateIdMapping = '';
        this.wellMapping = '';
    }

    close() {
        this.activeModal.dismiss('Close');
        this.reset();
    }

    back() {
        this.activeModal.dismiss('Back Import');
        this.reset();
        const confirmModalRef = this.modalService.open(SampleImportPlateComponent as Component, { size: 'lg', backdrop: 'static' });
        confirmModalRef.result.then(() => {
            this.activeModal.close();
        }, () => this.activeModal.dismiss());
    }

    validate() {
        const errorMessage: string[] = [];
        this.validateData(this.importData.slice(1), errorMessage);

        if (errorMessage.length !== 0) {
            this.alertService.error('error.custom', { param: formatErrorList(errorMessage) });
            return false;
        }
        return true;
    }

    private validateData(importData: any[], errorMessage: string[]) {
        for (let i = 0; i < importData.length; i++) {
            if (this.sampleIdMapping === '' || this.plateIdMapping === '' || this.wellMapping === '') {
                errorMessage.push(this.translateService.instant('bmsjHipsterApp.sample.importPlate.headersNotMapped'));
                break;
            }
            if (this.columnHasEmptyData(this.sampleIdMapping)) {
                errorMessage.push(this.translateService.instant('bmsjHipsterApp.sample.importPlate.recordHasNoSampleId'));
                return false;
            }
        }
    }

    columnHasEmptyData(headerName: string) {
        const headerRow = this.importData[0];
        const columnIndex = headerRow.indexOf(headerName);
        return this.importData.some((row) => {
            return !row[columnIndex];
        });
    }

    buildSampleList() {
        const headerRow = this.importData[0];
        const sampleUidColumnIndex = headerRow.indexOf(this.sampleIdMapping);
        const PlateIdColumnIndex = headerRow.indexOf(this.plateIdMapping);
        const WellColumnIndex = headerRow.indexOf(this.wellMapping);

        const sampleList = [];
        for (let i = 1; i < this.importData.length; i++) {
            const sampleId = this.importData[i][sampleUidColumnIndex];
            const plateInfo = this.importData[i][PlateIdColumnIndex];
            const wellInfo = this.importData[i][WellColumnIndex];

            const sample = {
                sampleBusinessKey: sampleId,
                plateId: plateInfo,
                well: wellInfo
            };

            sampleList.push(sample);
        }
        return sampleList;
    }

    private getSampleIdColumnIndex() {
        return this.header.indexOf(this.sampleIdMapping);
    }

    private getPlateColumnIndex() {
        return this.header.indexOf(this.plateIdMapping);
    }

    private getWellColumnIndex() {
        return this.header.indexOf(this.wellMapping);
    }

    isFormValid(f) {
        const form = f.form;
        if (Object.values(form.controls).filter((control: any) => !control.disabled).length === 1) {
            return false;
        }
        return f.form.valid;
    }
}
