import {Component, ElementRef, ViewChild} from '@angular/core';
import {ModalService} from '../../shared/modal/modal.service';
import {ExcelService} from './excel.service';
import {JhiAlertService} from 'ng-jhipster';

@Component({
    selector: 'jhi-sample-import-plate',
    templateUrl: './sample-import-plate.component.html',
    styleUrls: ['./sample-import-plate.component.css']
})

export class SampleImportPlateComponent {

    modalId = 'import-plate-modal';

    @ViewChild('fileUpload') fileUpload: ElementRef;

    fileFormat = '';
    importData = new Array<Array<any>>();

    constructor(private modalService: ModalService,
                private excelService: ExcelService,
                private alertService: JhiAlertService) {

    }

    close() {
        this.modalService.close(this.modalId);
        this.fileUpload.nativeElement.value = '';
        this.importData.length = 0;
    }

    import() {
        if (this.validate()) {
            this.modalService.close(this.modalId);
            this.modalService.open('import-plate-mapping-modal');
        }
    }

    validate() {

        let errorMessage = '';
        const fileName = this.fileUpload.nativeElement.value;

        if (this.fileFormat === '') {
            errorMessage = 'bmsjHipsterApp.sample.importPlate.noSelectedFormat';
        } else if (fileName === '') {
            errorMessage = 'bmsjHipsterApp.sample.importPlate.noFileSelected';
        } else if (!fileName.toLowerCase().endsWith('.' + this.fileFormat)) {
            errorMessage = 'bmsjHipsterApp.sample.importPlate.invalidFileFormat';
        } else if (this.importData.length === 0) {
            errorMessage = 'bmsjHipsterApp.sample.importPlate.noContent';
        }
        if (errorMessage !== '') {
            this.alertService.error(errorMessage);
            return false;
        }
        return true;
    }

    onFileChange(evt: any) {
        const target: DataTransfer = <DataTransfer>(evt.target);
        this.excelService.parse(target).subscribe((value) => {
            this.importData = value;
        });
    }
}
