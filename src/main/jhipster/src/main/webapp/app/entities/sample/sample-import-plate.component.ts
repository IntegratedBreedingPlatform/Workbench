import {Component, ElementRef, ViewChild} from '@angular/core';
import {ModalService} from '../../shared/modal/modal.service';
import {ExcelService} from './excel.service';
import {JhiAlertService} from 'ng-jhipster';
import {JhiAlert} from 'ng-jhipster/src/service/alert.service';

@Component({
    selector: 'jhi-sample-import-plate',
    templateUrl: './sample-import-plate.component.html',
    styleUrls: ['./sample-import-plate.component.css']
})

export class SampleImportPlateComponent {

    @ViewChild('fileUpload')
    fileUpload: ElementRef;

    modalId = 'import-plate-modal';
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
        
        this.modalService.close(this.modalId);
        this.modalService.open('import-plate-mapping-modal');

    }

    onFileChange(evt: any) {

        const target: DataTransfer = <DataTransfer>(evt.target);
        this.excelService.parse(target).subscribe((value) => {
            this.importData = value;
        });

    }
}
