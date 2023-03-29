import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import { AlertService } from '../../../shared/alert/alert.service';
import {JhiAlertService, JhiLanguageService} from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { formatErrorList } from '../../../shared/alert/format-error-list';
import {NgbActiveModal, NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {parseCSV, CsvFileData} from "../../../shared/util/file-utils";
import {ActivatedRoute} from "@angular/router";
import {SampleGenotypeImportFileMappingComponent} from "./sample-genotype-import-file-mapping.component";

@Component({
    selector: 'jhi-sample-genotype-import-file',
    templateUrl: './sample-genotype-import-file.component.html'
})
export class SampleGenotypeImportFileComponent implements OnInit {
    private readonly SAMPLE_UID: string = 'SAMPLE_UID';

    @ViewChild('fileUpload')
    fileUpload: ElementRef;

    fileName = '';
    csvFileData: CsvFileData;

    selectedFileType = '.csv';

    listId: string;
    studyId: string;

    constructor(
        private route: ActivatedRoute,
        public alertService: AlertService,
        public activeModal: NgbActiveModal,
        public modalService: NgbModal,
        public jhiLanguageService: JhiLanguageService,
        public translateService: TranslateService,
        public jhiAlertService: JhiAlertService) {
    }

    ngOnInit(): void {
        this.listId = this.route.snapshot.paramMap.get('listId');
        // Get the studyId from the query string params.
        this.studyId = this.route.snapshot.queryParamMap.get('studyId');
    }

    close() {
        if ((<any>window.parent).closeModal) {
            (<any>window.parent).closeModal();
        }
    }

    import() {
        if (this.validate()) {
            this.activeModal.close()
            const sampleGenotypeImportFileMappingComponent = this.modalService.open(SampleGenotypeImportFileMappingComponent as Component,
                { windowClass: 'modal-large', backdrop: 'static' });
            sampleGenotypeImportFileMappingComponent.componentInstance.listId = this.listId;
            sampleGenotypeImportFileMappingComponent.componentInstance.csvFileData = this.csvFileData;
        }
    }

    onFileChange(evt: any) {
        const target = evt.target;
        this.fileName = target.files[0].name;

        const extension = this.fileName.split('.');
        if (extension[1].toLowerCase() !== 'csv') {
            this.fileName = '';
            target.value = '';
            this.fileUpload.nativeElement.innerText = this.fileName;
            this.alertService.error('The import genotypes is only available for csv');
            return;
        }
        this.fileUpload.nativeElement.innerText = this.fileName;

        parseCSV(target.files[0]).subscribe((value) => {
            this.csvFileData = value;
            target.value = '';
        });
    }

    private validate() {
        if (!this.csvFileData || !this.csvFileData.data || !this.csvFileData.data.length) {
            this.alertService.error('error.import.file.empty');
            return false;
        }
        const errorMessage: string[] = [];
        this.validateHeader(this.csvFileData.headers, errorMessage);
        this.validateData(this.csvFileData.data, errorMessage);

        if (errorMessage.length !== 0) {
            this.alertService.error('error.custom', { param: formatErrorList(errorMessage) });
            console.log(formatErrorList(errorMessage));
            return false;
        }

        return true;
    }

    private validateHeader(fileHeaders: string[], errorMessage: string[]) {
        if (!fileHeaders.includes(this.SAMPLE_UID)) {
            errorMessage.push(this.translateService.instant('error.import.header.mandatory', { param: this.SAMPLE_UID }));
        }
        if (fileHeaders.length === 1 && fileHeaders.includes(this.SAMPLE_UID)) {
            errorMessage.push(this.translateService.instant('error.import.markers.required'));
        }

        fileHeaders.forEach((header) => {
            const result = fileHeaders.filter((fileHeader) =>
                fileHeader.toLowerCase() === header.toLowerCase()
            );

            if (result.length > 1) {
                errorMessage.push(this.translateService.instant('error.import.header.duplicated', { param: header }));
            }
        });
    }

    private validateData(importData: any[], errorMessage: string[]) {
        if (importData.length === 0) {
            errorMessage.push(this.translateService.instant('error.import.file.empty'));
        }
        const sampleUIDs = [];
        importData.forEach((row) => sampleUIDs.push(row['SAMPLE_UID']));

        if (sampleUIDs.includes(null) || sampleUIDs.includes('')) {
            errorMessage.push(this.translateService.instant('error.import.sample.uid.null'));
        }

        const noDups = new Set(sampleUIDs);
        if (sampleUIDs.length !== noDups.size) {
            errorMessage.push(this.translateService.instant('error.import.duplicate.sampleUIDs'));
        }
    }
}


