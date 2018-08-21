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
    @Output() onClose = new EventEmitter();
    @Output() onBack = new EventEmitter();

    sampleIdMapping = '';
    plateIdMapping = '';
    wellMapping = '';

    constructor(private modalService: ModalService,
                private alertService: JhiAlertService,
                private sampleContext: SampleContext,
                private sampleListService: SampleListService,
                private eventManager: JhiEventManager) {
    }

    proceed() {

        const activeListId = this.sampleContext.getActiveList().id;

        if (this.validate()) {

            this.sampleListService.importPlateInfo({
                listId: activeListId,
                sampleIdHeader: this.sampleIdMapping,
                plateIdHeader: this.plateIdMapping,
                wellHeader: this.wellMapping,
                importData: this.importData
            }).subscribe((observer) => {
                this.close();
                // Refresh the sample list table.
                this.eventManager.broadcast({name: 'sampleListModification', content: ''});
                this.alertService.success('bmsjHipsterApp.sample.importPlate.success');
            }, (response) => {
                if (response.status === 409) {
                    this.alertService.error('bmsjHipsterApp.sample.error', { param : response.error.errors[0].message});
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

}
