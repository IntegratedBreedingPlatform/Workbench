import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ModalService} from '../../shared/modal/modal.service';
import {SampleContext} from './sample.context';
import {JhiAlertService, JhiEventManager} from 'ng-jhipster';
import {SampleListService} from './sample-list.service';

@Component({
    selector: 'jhi-sample-import-plate-mapping',
    templateUrl: './sample-import-plate-mapping.component.html'
})

export class SampleImportPlateMappingComponent {

    modalId = 'import-plate-mapping-modal';

    @Input() importData: Array<Array<any>>;
    @Input() header: Array<any>;
    @Input() sampleIdMapping: string;
    @Input() plateIdMapping: string;
    @Input() wellMapping: string;

    @Output() onClose = new EventEmitter();
    @Output() onBack = new EventEmitter();

    constructor(private modalService: ModalService,
                private alertService: JhiAlertService,
                private sampleContext: SampleContext,
                private sampleListService: SampleListService,
                private eventManager: JhiEventManager) {
    }

    proceed() {

        const activeListId = this.sampleContext.getActiveList().id;

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
        this.modalService.close(this.modalId);
        this.reset();
        this.onClose.emit();
    }

    back() {
        this.modalService.close(this.modalId);
        this.reset();
        this.modalService.open('import-plate-modal');
        this.onBack.emit();
    }

    validate() {
        if (this.sampleIdMapping === '' || this.plateIdMapping === '' || this.wellMapping === '') {
            this.alertService.error('bmsjHipsterApp.sample.importPlate.headersNotMapped');
            return false;
        } else if (this.columnHasEmptyData(this.sampleIdMapping)) {
            this.alertService.error('bmsjHipsterApp.sample.importPlate.recordHasNoSampleId');
            return false;
        }
        return true;
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
            const plateId = this.importData[i][PlateIdColumnIndex];
            const well = this.importData[i][WellColumnIndex];

            const sample = {
                sampleId: undefined
                ,entryNo: undefined
                ,gid: undefined
                ,designation: undefined
                ,sampleName: undefined
                ,sampleBusinessKey: sampleId
                ,takenBy: undefined
                ,sampleList: undefined
                ,plantNumber: undefined
                ,plantBusinessKey: undefined
                ,plateId: plateId
                ,well: well
                ,datasets: undefined
                ,samplingDate: undefined
            };

            sampleList.push(sample);
        }
        return sampleList;
    }

}
