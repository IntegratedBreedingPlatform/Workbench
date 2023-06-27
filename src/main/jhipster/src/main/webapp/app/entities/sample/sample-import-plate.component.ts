import { Component, ElementRef, ViewChild } from '@angular/core';
import { ExcelService } from './excel.service';
import { AlertService } from '../../shared/alert/alert.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { SampleImportPlateMappingComponent } from './sample-import-plate-mapping.component';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'jhi-sample-import-plate',
    templateUrl: './sample-import-plate.component.html'
})

export class SampleImportPlateComponent {

    @ViewChild('fileUpload', {static: true})
    fileUpload: ElementRef;

    fileName = '';

    importFormats = [
        { name: 'CSV', extension: '.csv' }, //
        { name: 'Excel', extension: '.xls,.xlsx' }
    ];

    selectedFileType = this.importFormats.map((formatExtension) => formatExtension.extension).join(',');

    importData = new Array<Array<any>>();

    constructor(private excelService: ExcelService,
                private alertService: AlertService,
                public activeModal: NgbActiveModal,
                private modalService: NgbModal,
                private translateService: TranslateService) {

    }

    close() {
        this.activeModal.dismiss('cancel');
        this.clearSelectedFile();
    }

    import() {
        if (this.validate()) {
            this.activeModal.dismiss('import');
            const confirmModalRef = this.modalService.open(SampleImportPlateMappingComponent as Component, { size: 'lg', backdrop: 'static' });
            confirmModalRef.componentInstance.importData = this.importData;

            confirmModalRef.result.then(() => {
                this.activeModal.close();
            }, () => this.activeModal.dismiss());
        }
    }

    clearSelectedFile() {
        this.fileUpload.nativeElement.value = '';
        this.importData.length = 0;
        this.fileName = '';
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

        this.fileUpload.nativeElement.innerText = this.fileName;

        const target: DataTransfer = <DataTransfer>(evt.target);
        this.excelService.parse(target).subscribe((value) => {
            this.importData = value;
        });
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
