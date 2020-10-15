import {Component, ElementRef, ViewChild} from '@angular/core';
import {AppModalService} from '../../shared/modal/app-modal.service';
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

    selectedFileType = '.csv'; // Set the default file type to CSV.
    fileName = '';
    sampleIdMapping = '';
    plateIdMapping = '';
    wellMapping = '';
    importData = new Array<Array<any>>();

    constructor(private modalService: AppModalService,
                private excelService: ExcelService,
                private alertService: JhiAlertService) {

    }

    close() {
        this.modalService.close(this.modalId);
        this.clearSelectedFile();
    }

    import() {
        if (this.validate()) {
            this.modalService.close(this.modalId);
            this.modalService.open('import-plate-mapping-modal');
        }
    }

    clearSelectedFile() {
        this.fileUpload.nativeElement.value = '';
        this.importData.length = 0;
        this.fileName = '';
        this.sampleIdMapping = '';
        this.plateIdMapping = '';
        this.wellMapping = '';
    }

    onFileTypeChange() {
        if (this.selectedFileType !== '') {
            this.fileUpload.nativeElement.accept = this.selectedFileType;
        } else {
            this.clearSelectedFile();
        }
    }

    onFileChange(evt: any) {

        const file = evt.target.files[0];
        this.fileName = file.name;

        const target: DataTransfer = <DataTransfer>(evt.target);
        this.excelService.parse(target).subscribe((value) => {
            this.importData = value;
            this.sampleIdMapping = this.mappingHeader(this.importData[0], 'SAMPLE_UID');
            this.plateIdMapping = this.mappingHeader(this.importData[0], 'PLATE_ID');
            this.wellMapping = this.mappingHeader(this.importData[0], 'WELL');
        });
    }

    mappingHeader(header: Array<any>, mapping: string) {
        for (const column of header) {
            if (column === mapping) {
                return mapping;
            }
        }
        return '';
    }

    validate() {

        let errorMessage = '';
        const fileName = this.fileUpload.nativeElement.value;

        if (this.selectedFileType === '') {
            errorMessage = 'bmsjHipsterApp.sample.importPlate.noSelectedFormat';
        } else if (fileName === '') {
            errorMessage = 'bmsjHipsterApp.sample.importPlate.noFileSelected';
        } else if (this.importData.length === 0) {
            errorMessage = 'bmsjHipsterApp.sample.importPlate.noContent';
        }
        if (errorMessage !== '') {
            this.alertService.error(errorMessage);
            return false;
        }
        return true;
    }
}
