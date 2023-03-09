import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ParamContext } from '../../../shared/service/param.context';
import { Study } from '../../../shared/brapi/model/study/study';
import { VariantSet } from '../../../shared/brapi/model/variantsets/variantset';
import { CropGenotypingParameter } from '../../../shared/crop/model/crop-genotyping-parameter';
import { GenotypingBrapiService } from '../../../shared/brapi/service/genotyping-brapi.service';
import { AlertService } from '../../../shared/alert/alert.service';
import { JhiAlertService, JhiLanguageService } from 'ng-jhipster';
import { SampleContext } from '../sample.context';
import { Sample } from '../../../shared/brapi/model/samples/sample';
import { SampleService } from '../sample.service';
import { SearchSamplesRequest } from '../../../shared/brapi/model/samples/search-samples-request';
import { flatMap } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { VariableDetails } from '../../../shared/ontology/model/variable-details';
import { VariableTypeEnum } from '../../../shared/ontology/variable-type.enum';
import { toUpper } from '../../../shared/util/to-upper';
import { TranslateService } from '@ngx-translate/core';
import { Call } from '../../../shared/brapi/model/calls/call';
import { GenotypeImportRequest } from './genotype.import.request';
import { GenotypeService } from './genotype.service';
import { CallSet } from '../../../shared/brapi/model/callsets/callset';
import { GenotypingParameterUtilService } from '../../../shared/genotyping/genotyping-parameter-util.service';

@Component({
    selector: 'jhi-germplasm-details-modal',
    templateUrl: './genotype.modal.component.html'
})
export class GenotypeModalComponent implements OnInit {

    listId: string;

    selectedGenotypingStudy: Study;
    selectedVariantSet: VariantSet;
    selectedVariantItem: VariantItem;
    genotypingStudies: Study[] = [];
    genotypingVariantsets: VariantSet[] = [];
    sampleUIDs: string[] = [];
    variantSelectItems: VariantItem[] = [];
    cropGenotypingParameter: CropGenotypingParameter;
    selectedVariable: VariableDetails = null;
    genotypeMarkersId: number = VariableTypeEnum.GENOTYPE_MARKER;
    mappedVariants = new Map<string, VariantToVariableEntryItem>();

    isStudyLoading = false;
    isVariantSetLoading = false;
    isVariantsLoading = false;
    isGenotypesSaving = false;
    showAddMappingRow = false;

    sampleUIDSampleIdMap = new Map<string, number>();
    sampleDbIdSampleUIDMap = new Map<string, string>();
    callsetDbIdSampleDbIdMap = new Map<string, string>();

    constructor(private route: ActivatedRoute,
                private router: Router,
                public activeModal: NgbActiveModal,
                private paramContext: ParamContext,
                private genotypingBrapiService: GenotypingBrapiService,
                private genotypingParameterUtilService: GenotypingParameterUtilService,
                public alertService: AlertService,
                public jhiAlertService: JhiAlertService,
                private sampleContext: SampleContext,
                private sampleService: SampleService,
                private genotypeService: GenotypeService,
                public jhiLanguageService: JhiLanguageService,
                public translateService: TranslateService) {

    }

    ngOnInit(): void {
        this.listId = this.route.snapshot.paramMap.get('listId');

        this.genotypingParameterUtilService.getGenotypingParametersAndAuthenticate().subscribe((cropGenotypingParameter) => {
            this.cropGenotypingParameter = cropGenotypingParameter;
            this.genotypingBrapiService.brapiEndpoint = cropGenotypingParameter.endpoint;
            this.genotypingBrapiService.baseUrl = cropGenotypingParameter.baseUrl;
            this.genotypingBrapiService.accessToken = cropGenotypingParameter.accessToken;

            this.isStudyLoading = true;
            this.loadGenotypingStudy();
        }, (error) => {
            this.alertService.error(error);
        });

        this.sampleService.getAllSamples({
            listId: this.listId
        }).toPromise().then((samples) => {
            samples.forEach((sample) => {
                this.sampleUIDSampleIdMap.set(sample.sampleBusinessKey, sample.id);
                this.sampleUIDs.push(sample.sampleBusinessKey);
            });
        });
    }

    close() {
        if ((<any>window.parent).closeModal) {
            (<any>window.parent).closeModal();
        }
    }

    loadGenotypingStudy() {
        this.genotypingBrapiService.searchStudies({
            'active': true,
            programDbIds: [this.cropGenotypingParameter.programId]
        }).toPromise().then((brapiResponse) => {
            if (brapiResponse && brapiResponse.result.data.length) {
                this.genotypingStudies = brapiResponse.result.data;
            } else {
                this.alertService.error('genotyping.no.genotyping.studies.found');
            }
            this.isStudyLoading = false;
        }, (error) => {
            // FIXME: Gigwa server throws an http 500 error if the germplasm is not found.
            this.alertService.error('genotyping.no.genotyping.germplasm.found');
            this.isStudyLoading = false;
        });
    }

    selectStudyOnChange() {
        if (this.selectedGenotypingStudy) {
            this.isVariantSetLoading = true;
            this.resetData();
            this.genotypingVariantsets = [];
            this.selectedVariantSet = null;
            this.genotypingBrapiService.searchVariantsetsGetAll({ studyDbIds: [this.selectedGenotypingStudy.studyDbId] }).toPromise().then((variantSets) => {
                if (variantSets && variantSets.length) {
                    this.genotypingVariantsets = variantSets;
                } else {
                    this.alertService.error('genotyping.no.genotyping.variantsets.found');
                }
                this.isVariantSetLoading = false;
            });
        }
    }

    selectVariantsetOnChange() {
        if (this.selectedVariantSet && this.sampleUIDs && this.sampleUIDs.length !== 0) {
            this.resetData();
            this.isVariantsLoading = true;

            // Get the genotype samples corresponding to samples in BMS.
            // The externalReferenceIds are expected to be the sampleUIDs of samples in BMS.
            const searchSamplesRequest: SearchSamplesRequest = { externalReferenceIds: this.sampleUIDs, programDbIds: [this.cropGenotypingParameter.programId] };
            // TODO: Make sure this query returns all the results and not just the first page.
            this.genotypingBrapiService.searchSamplesGetAll(searchSamplesRequest).pipe(flatMap((samples) => {
                if (samples && samples.length) {
                    // Get the Callsets (Individuals) that are associated to the specified genotype samples.
                    return this.retrieveCallsets(samples);
                }
                return Observable.empty();
            })).subscribe((callSets) => {
                if (callSets && callSets.length) {
                    // Get the variants (Markers) associated to the specified Callsets (Individuals)
                    this.loadVariants(this.selectedVariantSet.variantSetDbId, callSets);
                } else {
                    this.alertService.error('genotyping.no.genotyping.samples.found');
                    this.isVariantsLoading = false;
                }
            });

        }
    }

    retrieveCallsets(genotypingSamples: Sample[]): Observable<CallSet[]> {
        // Get the Callsets (Individuals) that are associated to the specified genotype samples.
        this.sampleDbIdSampleUIDMap = this.createSampleDbIdToSampleUIDMap(genotypingSamples);
        const sampleDbIds = Array.from(this.sampleDbIdSampleUIDMap.keys());
        return this.genotypingBrapiService.searchCallsetsGetAll({
            variantSetDbIds: [this.selectedVariantSet.variantSetDbId],
            sampleDbIds
        });
    }

    loadVariants(variantSetDbId: string, callsets: CallSet[]): void {
        // Get the variants (Markers) associated to the specified Callsets (Individuals)
        callsets.forEach((callset) => {
            this.callsetDbIdSampleDbIdMap.set(callset.callSetDbId, callset.sampleDbId);
        });

        this.genotypingBrapiService.searchVariantsGetAll({
            callSetDbIds: Array.from(this.callsetDbIdSampleDbIdMap.keys()),
            variantSetDbIds: [this.selectedVariantSet.variantSetDbId],
        }).toPromise().then((variants) => {
            if (variants && variants.length) {
                this.variantSelectItems = variants.map((variant) => new VariantItem(variant.variantDbId, variant.variantNames[0]));
                this.isVariantsLoading = false;
                this.addMapping();
            } else {
                this.alertService.error('genotyping.no.genotyping.variants.found');
                this.isVariantsLoading = false;
            }
        });
    }

    createSampleDbIdToSampleUIDMap(genotypeSamples: Sample[]): Map<string, string> {
        const map = new Map<string, string>();
        genotypeSamples.forEach((sample) => {
            sample.externalReferences.forEach((externalReference) => {
                if (this.sampleUIDs.includes(externalReference.referenceID)) {
                    map.set(sample.sampleDbId, externalReference.referenceID);
                }
            });
        });
        return map;
    }

    selectVariable(variable: VariableDetails) {
        this.selectedVariable = variable;
    }

    mapVariant() {
        if (this.mappedVariants.has(this.selectedVariantItem.variantDbId)) {
            this.mappedVariants.get(this.selectedVariantItem.variantDbId).variable = this.selectedVariable;
        } else {
            this.mappedVariants.set(this.selectedVariantItem.variantDbId, {
                variant: this.selectedVariantItem,
                variable: this.selectedVariable
            });
        }
        this.showAddMappingRow = false;
        this.selectedVariantItem = null;
        this.selectedVariable = null;
    }

    removeMappedVariant(variantDbId) {
        if (this.mappedVariants.has(variantDbId)) {
            this.mappedVariants.delete(variantDbId);
        }
    }

    searchVariant(term: string, item: any) {
        const termUpper = toUpper(term);
        return toUpper(item.variantName).includes(termUpper);
    }

    addMapping() {
        this.showAddMappingRow = true;
    }

    showAddMappingButton() {
        return !this.showAddMappingRow && this.mappedVariants && this.mappedVariants.size < 10;
    }

    importGenotypes() {
        this.isGenotypesSaving = true;
        const variantDbIds = [];
        this.mappedVariants.forEach((mappedVariant) => {
            variantDbIds.push(mappedVariant.variant.variantDbId);
        });
        const callSetDbIds: string[] = Array.from(this.callsetDbIdSampleDbIdMap.keys());
        const variantSetDbIds: string[] = [this.selectedVariantSet.variantSetDbId];
        // Retrieve the Calls (Genotype value) of the specified Markers + Individuals
        this.genotypingBrapiService.searchCallsGetAll({
            variantSetDbIds,
            callSetDbIds,
            variantDbIds
        }).toPromise().then((calls) => {
            if (calls && calls.length) {
                this.saveGenotypes(calls);
            } else {
                this.isGenotypesSaving = false;
                this.alertService.error('genotyping.no.genotyping.calls.found');
            }
            this.isVariantSetLoading = false;
        });
    }

    saveGenotypes(calls: Call[]) {
        const genotypeImportRequest: GenotypeImportRequest[] = [];
        calls.forEach((call) => {
            const mappedVariant = this.mappedVariants.get(call.variantDbId);
            const sampleDbId = this.callsetDbIdSampleDbIdMap.get(call.callSetDbId);
            const sampleUID = this.sampleDbIdSampleUIDMap.get(sampleDbId);
            const sampleId = String(this.sampleUIDSampleIdMap.get(sampleUID));
            genotypeImportRequest.push({ variableId: Number(mappedVariant.variable.id), value: call.genotypeValue, sampleId });
        });
        this.genotypeService.importGenotypes(genotypeImportRequest, this.listId).toPromise().then((genotypeIds) => {
            this.isGenotypesSaving = false;
            if ((<any>window.parent).handleImportGenotypesSuccess) {
                (<any>window.parent).handleImportGenotypesSuccess();
            }
        });
    }

    resetData() {
        this.selectedVariantItem = null;
        this.variantSelectItems = [];
        this.selectedVariable = null;
        this.mappedVariants.clear();
        this.showAddMappingRow = false;
        this.sampleDbIdSampleUIDMap.clear();
        this.callsetDbIdSampleDbIdMap.clear();
    }
}

class VariantItem {
    constructor(public variantDbId: string,
                public variantName: string) {
    }
}

class VariantToVariableEntryItem {
    constructor(public variant: VariantItem,
                public variable: VariableDetails) {
    }
}
