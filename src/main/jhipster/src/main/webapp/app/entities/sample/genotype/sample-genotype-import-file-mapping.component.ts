import {Component, Input, OnInit} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AlertService } from '../../../shared/alert/alert.service';
import { JhiLanguageService } from 'ng-jhipster';
import { SampleService } from '../sample.service';
import { VariableDetails } from '../../../shared/ontology/model/variable-details';
import { VariableTypeEnum } from '../../../shared/ontology/variable-type.enum';
import { toUpper } from '../../../shared/util/to-upper';
import { TranslateService } from '@ngx-translate/core';
import { SampleGenotypeImportRequest } from './sample-genotype-import-request';
import { SampleGenotypeService } from './sample-genotype.service';
import { formatErrorList } from '../../../shared/alert/format-error-list';
import {CsvFileData} from "../../../shared/util/file-utils";

@Component({
    selector: 'jhi-sample-genotype-import-file-mapping',
    templateUrl: './sample-genotype-import-file-mapping.component.html'
})
export class SampleGenotypeImportFileMappingComponent implements OnInit {

    private readonly SAMPLE_UID: string = 'SAMPLE_UID';

    readonly MARKER_MAPPING_ITEM_COUNT_LIMIT = 20;

    selectedVariable: VariableDetails = null;
    showAddMappingRow = true;
    genotypeMarkersId: number = VariableTypeEnum.GENOTYPE_MARKER;
    mappedMarkers = new Map<string, MarkerToVariableEntryItem>();
    selectedMarker: string;
    isGenotypesSaving = false;

    markerSelectItems: string[];

    @Input()
    csvFileData: CsvFileData;
    @Input()
    listId: string;
    @Input()
    studyId: string;

    constructor(private route: ActivatedRoute,
                public alertService: AlertService,
                private sampleService: SampleService,
                private genotypeService: SampleGenotypeService,
                public jhiLanguageService: JhiLanguageService,
                public translateService: TranslateService) {

    }

    ngOnInit(): void {
        // Get the studyId from the query string params.
        this.studyId = this.route.snapshot.queryParamMap.get('studyId');
        const headers = this.csvFileData.headers;
        const sampleUIDIndex = headers.indexOf(this.SAMPLE_UID);
        headers.splice(sampleUIDIndex, 1);
        this.markerSelectItems = headers;
    }

    close() {
        if ((<any>window.parent).closeModal) {
            (<any>window.parent).closeModal();
        }
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
                const value =row[mappedMarker.markerName];
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
}

class MarkerToVariableEntryItem {
    constructor(public markerName: string,
                public variable: VariableDetails) {
    }
}
