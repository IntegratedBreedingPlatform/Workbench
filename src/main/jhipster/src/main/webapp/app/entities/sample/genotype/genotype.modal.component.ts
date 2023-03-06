import {Component, OnInit} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {ParamContext} from "../../../shared/service/param.context";
import {Study} from "../../../shared/brapi/model/study/study";
import {VariantSet} from "../../../shared/brapi/model/variantsets/variantset";
import {CropGenotypingParameter} from "../../../shared/crop/model/crop-genotyping-parameter";
import {CropParameterService} from "../../../shared/crop-parameter/service/crop-parameter.service";
import {GenotypingBrapiService} from "../../../shared/brapi/service/genotyping-brapi.service";
import {AlertService} from "../../../shared/alert/alert.service";
import {JhiAlertService, JhiLanguageService} from "ng-jhipster";
import {SampleContext} from "../sample.context";
import {Sample} from "../../../shared/brapi/model/samples/sample";
import {SampleService} from "../sample.service";
import {SearchSamplesRequest} from "../../../shared/brapi/model/samples/search-samples-request";
import {flatMap} from "rxjs/operators";
import {Observable} from "rxjs";
import {VariableDetails} from "../../../shared/ontology/model/variable-details";
import {VariableTypeEnum} from "../../../shared/ontology/variable-type.enum";
import {toUpper} from "../../../shared/util/to-upper";
import {TranslateService} from "@ngx-translate/core";
import {Call} from "../../../shared/brapi/model/calls/call";
import {GenotypeImportRequest} from "./genotype.import.request";
import {GenotypeService} from "./genotype.service";

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
    selectedVariant: any;
    rowSelectedVariant: any;
    genotypingStudies: Study[] = [];
    genotypingVariantsets: VariantSet[] = [];
    genotypeSamples: Sample[] = [];
    sampleUIDs: string[] = [];
    callsetDbIds: string[] = [];
    variants = [];
    rowVariants = [];
    variantSelectItems = [];
    rowVariantSelectItems = [];
    cropGenotypingParameter: CropGenotypingParameter;
    variable: VariableDetails = null;
    rowVariable: VariableDetails = null;
    genotypeMarkersId: number = VariableTypeEnum.GENOTYPE_MARKER;
    mappedVariants = [];
    mappedVariantsArray = [];

    isStudyLoading = false;
    isVariantSetLoading = false;
    isVariantsLoading = false;
    isGenotypesSaving = false;
    showAddMappingRow = false;

    sampleUIDSampleIdMap = [];
    sampleDbIdSampleUIDMap = [];
    callsetDbIdSampleDbIdMap = [];

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
                const cropParameterMap = cropParameters.reduce(function(map, row) {
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
                listId: this.listId,
            }).toPromise().then((samples) => {
                samples.body.forEach((sample) => {
                    this.sampleUIDSampleIdMap[sample.sampleBusinessKey] = sample.id;
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
            "active": true,
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
            const searchSamplesRequest: SearchSamplesRequest = { externalReferenceIds: this.sampleUIDs, programDbIds: [this.cropGenotypingParameter.programId] };
            this.genotypingBrapiService.searchSamples(searchSamplesRequest).pipe(flatMap((response) => {
                if (response && response.result.data.length) {
                    this.genotypeSamples = response.result.data;
                    const sampleDbIds = [];
                   this.genotypeSamples.forEach((sample) => {
                        sample.externalReferences.forEach((externalReference) =>  {
                            if (this.sampleUIDs.includes(externalReference.referenceID)) {
                                this.sampleDbIdSampleUIDMap[sample.sampleDbId] = externalReference.referenceID;
                            }
                        });
                        sampleDbIds.push(sample.sampleDbId);
                    });
                    return this.genotypingBrapiService.searchCallsets({
                        variantSetDbIds: [this.selectedVariantSet.variantSetDbId],
                        sampleDbIds: sampleDbIds
                    })
                }
                return Observable.empty();
            })).subscribe((brapiResponse) => {
                if (brapiResponse && brapiResponse.result.data.length) {
                    brapiResponse.result.data.forEach((callset) => {
                        this.callsetDbIdSampleDbIdMap[callset.callSetDbId] = callset.sampleDbId;
                        this.callsetDbIds.push(callset.callSetDbId);
                    });
                    this.genotypingBrapiService.searchVariants({ callSetDbIds: this.callsetDbIds, variantSetDbIds: [this.selectedVariantSet.variantSetDbId], }).toPromise().then((brapiResponse) => {
                        if (brapiResponse && brapiResponse.result.data.length) {
                             brapiResponse.result.data.forEach(variant => {
                                 this.variants[variant.variantDbId] = {variantDbId: variant.variantDbId, variantName: variant.variantNames[0]};
                             });
                             this.rowVariants = this.variants;
                             this.variantSelectItems = Object.values(this.variants);
                             this.rowVariantSelectItems = this.variantSelectItems;
                             this.isVariantsLoading = false;
                        } else {
                            this.alertService.error('genotyping.no.genotyping.variants.found');
                            this.isVariantsLoading = false;
                        }
                    });
                } else {
                    this.alertService.error('genotyping.no.genotyping.samples.found');
                    this.isVariantsLoading = false;
                }
            });

        }
    }

    selectVariable(variable: VariableDetails) {
        this.variable = variable;
    }

    selectRowVariable(variable: VariableDetails) {
        this.rowVariable = variable;
    }

    mapVariant() {
        if (this.mappedVariants[this.selectedVariant.variantDbId]) {
            this.mappedVariants[this.selectedVariant.variantDbId].variable = this.variable;
        } else {
            this.mappedVariants[this.selectedVariant.variantDbId] = {
                variant: this.selectedVariant,
                variable: this.variable
            };
        }
        this.mappedVariantsArray = Object.values(this.mappedVariants);
        delete this.rowVariants[this.selectedVariant.variantDbId];
        this.rowVariantSelectItems = Object.values(this.rowVariants);
        if (this.mappedVariantsArray.length === 10) {
            this.showAddMappingRow = false;
        }
    }

    removeMappedVariant(variantDbId) {
        if (this.mappedVariants[variantDbId]) {
            this.rowVariants[variantDbId] = this.mappedVariants[variantDbId].variant;
            this.rowVariantSelectItems.push(this.mappedVariants[variantDbId].variant);
            delete this.mappedVariants[variantDbId];
            this.mappedVariantsArray = Object.values(this.mappedVariants);
        }
    }

    searchVariant(term: string, item: any) {
        const termUpper = toUpper(term);
        return toUpper(item.variantName).includes(termUpper);
    }

    addMapping() {
        this.showAddMappingRow = true;
    }

    mapAddedRowVariant() {
        this.mappedVariants[this.rowSelectedVariant.variantDbId] = {
            variant: this.rowSelectedVariant,
            variable: this.rowVariable
        };
        this.mappedVariantsArray = Object.values(this.mappedVariants);
        delete this.rowVariants[this.rowSelectedVariant.variantDbId];
        this.rowVariantSelectItems = Object.values(this.rowVariants);
        this.showAddMappingRow = false;
        this.rowSelectedVariant = null;
        this.rowVariable = null;
    }

    showAddMappingButton() {
        return !this.showAddMappingRow && this.mappedVariantsArray && this.mappedVariantsArray.length < 10;
    }

    disableMapButton() {
        return (this.mappedVariantsArray && this.mappedVariantsArray.length >= 10) || !this.selectedVariant || !this.variable;
    }

    importGenotypes() {
        this.isGenotypesSaving = true;
        let variantDbIds = [];
        this.mappedVariantsArray.forEach((mappedVariant) => {
            variantDbIds.push(mappedVariant.variant.variantDbId);
        });
        this.genotypingBrapiService.searchCalls({callSetDbIds: this.callsetDbIds, variantSetDbIds: [this.selectedVariantSet.variantSetDbId], variantDbIds: variantDbIds}).toPromise().then((brapiResponse) => {
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
            const mappedVariant = this.mappedVariants[call.variantDbId];
            const sampleDbid = this.callsetDbIdSampleDbIdMap[call.callSetDbId];
            const sampleUID = this.sampleDbIdSampleUIDMap[sampleDbid];
            const sampleId = this.sampleUIDSampleIdMap[sampleUID];
            genotypeImportRequest.push({variableId: mappedVariant.variable.id, value: call.genotypeValue, sampleId: sampleId});
        });
        this.genotypeService.importGenotypes(genotypeImportRequest, this.listId).toPromise().then((genotypeIds) => {
            this.isGenotypesSaving = false;
            if ((<any>window.parent).handleImportGenotypesSuccess) {
                (<any>window.parent).handleImportGenotypesSuccess();
            }
        });
    }

    resetData() {
        this.selectedVariant = null;
        this.rowSelectedVariant = null;
        this.genotypeSamples = [];
        this.callsetDbIds = [];
        this.variants = [];
        this.rowVariants = [];
        this.variantSelectItems = [];
        this.rowVariantSelectItems = [];
        this.variable = null;
        this.rowVariable = null;
        this.mappedVariants = [];
        this.mappedVariantsArray = [];
        this.showAddMappingRow = false;
        this.sampleDbIdSampleUIDMap = [];
        this.callsetDbIdSampleDbIdMap = [];
    }
}