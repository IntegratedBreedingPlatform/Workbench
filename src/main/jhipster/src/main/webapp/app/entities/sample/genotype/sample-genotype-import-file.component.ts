import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import { AlertService } from '../../../shared/alert/alert.service';
import {JhiAlertService, JhiLanguageService} from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { formatErrorList } from '../../../shared/alert/format-error-list';
import {NgbActiveModal, NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {parseCSV, CsvFileData} from '../../../shared/util/file-utils';
import {ActivatedRoute} from '@angular/router';
import {VariableDetails} from '../../../shared/ontology/model/variable-details';
import {VariableTypeEnum} from '../../../shared/ontology/variable-type.enum';
import {toUpper} from '../../../shared/util/to-upper';
import {SampleGenotypeImportRequest} from './sample-genotype-import-request';
import {SampleGenotypeService} from './sample-genotype.service';

@Component({
    selector: 'jhi-sample-genotype-import-file',
    templateUrl: './sample-genotype-import-file.component.html'
})
export class SampleGenotypeImportFileComponent implements OnInit {
    isFileUploadMode = true;
    private readonly SAMPLE_UID: string = 'SAMPLE_UID';

    @ViewChild('fileUpload')
    fileUpload: ElementRef;

    fileName = '';
    csvFileData: CsvFileData;
    selectedFileType = '.csv';
    listId: string;
    studyId: string;

    readonly MARKER_MAPPING_ITEM_COUNT_LIMIT = 20;

    selectedVariable: VariableDetails = null;
    showAddMappingRow = true;
    genotypeMarkersId: number = VariableTypeEnum.GENOTYPE_MARKER;
    mappedMarkers = new Map<string, MarkerToVariableEntryItem>();
    selectedMarker: string;
    isGenotypesSaving = false;

    markerSelectItems: string[];

    constructor(
        private route: ActivatedRoute,
        private genotypeService: SampleGenotypeService,
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
            const headers = this.csvFileData.headers;
            const sampleUIDIndex = headers.indexOf(this.SAMPLE_UID);
            headers.splice(sampleUIDIndex, 1);
            this.markerSelectItems = headers;
            this.isFileUploadMode = false;
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
            this.alertService.error('error.custom', { param: 'The import genotypes is only available for csv'});
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

    selectVariable(variable: VariableDetails) {
        this.selectedVariable = variable;
    }

    mapMarker() {
        // Show an error if the selected ontology variable is already mapped to a marker (variant)
        const mappedMarker = Array.from(this.mappedMarkers.values()).find((v) => v.variable.id === this.selectedVariable.id);
        if (mappedMarker) {
            this.alertService.error('bmsjHipsterApp.sample.genotypes.variable.already.mapped.to.a.marker.error', {
                param1: this.selectedVariable.name,
                param2: mappedMarker.markerName
            });
            return;
        }

        if (this.mappedMarkers.has(this.selectedMarker)) {
            this.mappedMarkers.get(this.selectedMarker).variable = this.selectedVariable;
        } else {
            this.mappedMarkers.set(this.selectedMarker, {
                markerName: this.selectedMarker,
                variable: this.selectedVariable
            });
        }
        this.showAddMappingRow = false;
        this.selectedMarker = null;
        this.selectedVariable = null;
    }

    removeMappedMarker(markerName) {
        if (this.mappedMarkers.has(markerName)) {
            this.mappedMarkers.delete(markerName);
        }
    }

    searchMarker(term: string, item: any) {
        const termUpper = toUpper(term);
        return toUpper(item).includes(termUpper);
    }

    addMapping() {
        this.showAddMappingRow = true;
    }

    showAddMappingButton() {
        return !this.showAddMappingRow && this.mappedMarkers && this.mappedMarkers.size < this.MARKER_MAPPING_ITEM_COUNT_LIMIT;
    }

    importGenotypes() {
        this.isGenotypesSaving = true;
        const genotypeImportRequest: SampleGenotypeImportRequest[] = [];

        this.csvFileData.data.forEach((row) => {
            this.mappedMarkers.forEach((mappedMarker) => {
                const value = row[mappedMarker.markerName];
                if (value !== null || value !== '') {
                    genotypeImportRequest.push({
                        variableId: Number(mappedMarker.variable.id),
                        value,
                        sampleUID: row[this.SAMPLE_UID]
                    });
                }
            })
        });

        this.genotypeService.importSampleGenotypes(this.studyId, genotypeImportRequest).toPromise().then((genotypeIds) => {
            this.isGenotypesSaving = false;
            if ((<any>window.parent).handleImportGenotypesSuccess) {
                (<any>window.parent).handleImportGenotypesSuccess();
            }
        }).catch((errorResponse) => {
            const msg = formatErrorList(errorResponse.error.errors);
            if (msg) {
                this.alertService.error('error.custom', { param: msg });
            } else {
                this.alertService.error('error.general');
            }
        });

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

class MarkerToVariableEntryItem {
    constructor(public markerName: string,
                public variable: VariableDetails) {
    }
}
