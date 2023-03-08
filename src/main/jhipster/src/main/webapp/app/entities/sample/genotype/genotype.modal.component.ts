import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ParamContext } from '../../../shared/service/param.context';
import { Study } from '../../../shared/brapi/model/study/study';
import { VariantSet } from '../../../shared/brapi/model/variantsets/variantset';
import { CropGenotypingParameter } from '../../../shared/crop/model/crop-genotyping-parameter';
import { CropParameterService } from '../../../shared/crop-parameter/service/crop-parameter.service';
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
import { BrapiResponse } from '../../../shared/brapi/model/common/brapi-response';

@Component({
    selector: 'jhi-germplasm-details-modal',
    templateUrl: './genotype.modal.component.html'
})
export class GenotypeModalComponent implements OnInit {

    public readonly GENOTYPING_SERVER = 'gigwa';

    public readonly ENDPOINT = this.GENOTYPING_SERVER + '_endpoint';
    public readonly TOKEN_ENDPOINT = this.GENOTYPING_SERVER + '_token_endpoint';
    public readonly PROGRAM_ID = this.GENOTYPING_SERVER + '_program_id';
    public readonly USERNAME = this.GENOTYPING_SERVER + '_username';
    public readonly PASSWORD = this.GENOTYPING_SERVER + '_password';
    public readonly BASE_URL = this.GENOTYPING_SERVER + '_base_url';

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
                private cropParameterService: CropParameterService,
                private genotypingBrapiService: GenotypingBrapiService,
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
        this.cropParameterService.getByGroupName(this.GENOTYPING_SERVER).subscribe(
            (cropParameters) => {
                const cropParameterMap = cropParameters.reduce(function (map, row) {
                    map[row.key] = row;
                    return map;
                }, {});
                this.populateGenotypingParameters(cropParameterMap);

                if (this.isGenotypingParameterConfigured()) {
                    this.genotypingBrapiService.brapiEndpoint = this.cropGenotypingParameter.endpoint;
                    this.genotypingBrapiService.baseUrl = this.cropGenotypingParameter.baseUrl;

                    this.cropParameterService.getGenotypingToken(this.GENOTYPING_SERVER).subscribe((accessToken) => {
                        this.genotypingBrapiService.accessToken = accessToken;
                        this.isStudyLoading = true;
                        this.loadGenotypingStudy();
                    }, (error) => {
                        this.alertService.error('genotyping.connection.error');
                    });
                }
            });

        this.sampleService.query({
            listId: this.listId
        }).toPromise().then((samples) => {
            samples.body.forEach((sample) => {
                this.sampleUIDSampleIdMap.set(sample.sampleBusinessKey, sample.id);
                this.sampleUIDs.push(sample.sampleBusinessKey);
            });
        });
    }

    populateGenotypingParameters(cropParameterMap) {
        if (cropParameterMap[this.ENDPOINT] && cropParameterMap[this.TOKEN_ENDPOINT] && cropParameterMap[this.PROGRAM_ID]
            && cropParameterMap[this.USERNAME] && cropParameterMap[this.PASSWORD] && cropParameterMap[this.BASE_URL]) {
            this.cropGenotypingParameter = new CropGenotypingParameter(cropParameterMap[this.ENDPOINT].value,
                cropParameterMap[this.TOKEN_ENDPOINT].value, cropParameterMap[this.USERNAME].value, cropParameterMap[this.PASSWORD].value,
                cropParameterMap[this.PROGRAM_ID].value, cropParameterMap[this.BASE_URL].value);
        }
    }

    isGenotypingParameterConfigured() {
        return this.cropGenotypingParameter && this.cropGenotypingParameter.endpoint && this.cropGenotypingParameter.tokenEndpoint && this.cropGenotypingParameter.userName
            && this.cropGenotypingParameter.password && this.cropGenotypingParameter.programId && this.cropGenotypingParameter.baseUrl;
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
            this.genotypingBrapiService.searchVariantsets({ studyDbIds: [this.selectedGenotypingStudy.studyDbId] }).toPromise().then((brapiResponse) => {
                if (brapiResponse && brapiResponse.result.data.length) {
                    this.genotypingVariantsets = brapiResponse.result.data;
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
            this.genotypingBrapiService.searchSamples(searchSamplesRequest).pipe(flatMap((response) => {
                if (response && response.result.data.length) {
                    // Get the Callsets (Individuals) that are associated to the specified genotype samples.
                    return this.retrieveCallsets(response.result.data);
                }
                return Observable.empty();
            })).subscribe((searchCallsetsResponse) => {
                if (searchCallsetsResponse && searchCallsetsResponse.result.data.length) {
                    // Get the variants (Markers) associated to the specified Callsets (Individuals)
                    this.loadVariants(this.selectedVariantSet.variantSetDbId, searchCallsetsResponse.result.data);
                } else {
                    this.alertService.error('genotyping.no.genotyping.samples.found');
                    this.isVariantsLoading = false;
                }
            });

        }
    }

    retrieveCallsets(genotypingSamples: Sample[]): Observable<BrapiResponse<CallSet>> {
        // Get the Callsets (Individuals) that are associated to the specified genotype samples.
        this.sampleDbIdSampleUIDMap = this.createSampleDbIdToSampleUIDMap(genotypingSamples);
        const sampleDbIds = Array.from(this.sampleDbIdSampleUIDMap.keys());
        return this.genotypingBrapiService.searchCallsets({
            variantSetDbIds: [this.selectedVariantSet.variantSetDbId],
            sampleDbIds
        });
    }

    loadVariants(variantSetDbId: string, callsets: CallSet[]): void {
        // Get the variants (Markers) associated to the specified Callsets (Individuals)
        callsets.forEach((callset) => {
            this.callsetDbIdSampleDbIdMap.set(callset.callSetDbId, callset.sampleDbId);
        });

        this.genotypingBrapiService.searchVariants({
            callSetDbIds: Array.from(this.callsetDbIdSampleDbIdMap.keys()),
            variantSetDbIds: [this.selectedVariantSet.variantSetDbId],
        }).toPromise().then((searchVariantsResponse) => {
            if (searchVariantsResponse && searchVariantsResponse.result.data.length) {
                this.variantSelectItems = searchVariantsResponse.result.data.map((variant) => new VariantItem(variant.variantDbId, variant.variantNames[0]));
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
        this.genotypingBrapiService.searchCalls({
            variantSetDbIds,
            callSetDbIds,
            variantDbIds
        }).toPromise().then((brapiResponse) => {
            if (brapiResponse && brapiResponse.result.data.length) {
                const calls = brapiResponse.result.data;
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
