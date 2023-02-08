import { BreedingMethod } from '../../shared/breeding-method/model/breeding-method';
import { ParamContext } from '../../shared/service/param.context';
import { Component, OnInit } from '@angular/core';
import { ObservationVariable } from '../../shared/model/observation-variable.model';
import { BreedingMethodSearchRequest } from '../../shared/breeding-method/model/breeding-method-search-request.model';
import { MatchType } from '../../shared/column-filter/column-filter-text-with-match-options-component';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { VariableTypeEnum } from '../../shared/ontology/variable-type.enum';
import { DatasetTypeEnum } from '../../shared/dataset/model/dataset-type.enum';
import { DatasetModel } from '../../shared/dataset/model/dataset.model';
import { StudyInstanceModel } from '../../shared/dataset/model/study-instance.model';
import { ActivatedRoute } from '@angular/router';
import { HelpService } from '../../shared/service/help.service';
import { DatasetService } from '../../shared/dataset/service/dataset.service';
import { TranslateService } from '@ngx-translate/core';
import { BreedingMethodService } from '../../shared/breeding-method/service/breeding-method.service';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { AlertService } from '../../shared/alert/alert.service';
import { BreedingMethodTypeEnum } from '../../shared/breeding-method/model/breeding-method-type.model';
import { BreedingMethodClassMethodEnum } from '../../shared/breeding-method/model/breeding-method-class.enum';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmListCreationComponent } from '../../shared/list-creation/germplasm-list-creation.component';
import { GermplasmListEntry } from '../../shared/list-creation/model/germplasm-list';
import { ADVANCE_SUCCESS, SELECT_INSTANCES } from '../../app.events';

export enum AdvanceType {
    STUDY,
    SAMPLES
}

export abstract class AbstractAdvanceComponent implements OnInit {

    static readonly BREEDING_METHODS_PAGE_SIZE = 300;
    static readonly BREEDING_METHOD_PROPERTY = 'Breeding method';
    static readonly SELECTION_PLANT_PROPERTY = 'Selections';
    static readonly SELECTION_TRAIT_PROPERTY = 'Selection Criteria';

    static readonly SELECTION_TRAIT_EXPRESSION = '[SELTRAIT]';

    breedingMethods: BreedingMethod[] = [];
    breedingMethodOptions: any;
    breedingMethodSelectedId: string;
    breedingMethodType = '1';
    useFavoriteBreedingMethods = false;
    methodTypes: BreedingMethodTypeEnum[] = [BreedingMethodTypeEnum.DERIVATIVE, BreedingMethodTypeEnum.MAINTENANCE];
    selectedBreedingMethod: BreedingMethod;

    selectionMethodVariables: ObservationVariable[] = [];

    showSelectionTraitSelection = false;

    checkAllReplications = false;

    datasetsLoaded = false;
    studyVariablesLoaded = false;
    isLoading = false;
    helpLink: string;

    studyId: any;
    environmentDatasetId: number;
    plotDatasetId: number;

    selectedInstances: any[];
    trialInstances: any[] = [];
    replicationNumber: number;
    selectedDatasetId: number;

    replicationsOptions: any[] = [];

    selectionPlantVariables: ObservationVariable[] = [];

    selectionTraitLevelOptions: any = [];
    selectionTraitVariablesByDatasetIds: Map<number, ObservationVariable[]> = new Map();
    selectionTraitVariables: ObservationVariable[] = [];
    selectedSelectionTraitDatasetId: number;
    selectedSelectionTraitVariableId: number;

    protected constructor(public paramContext: ParamContext,
                          public route: ActivatedRoute,
                          public breedingMethodService: BreedingMethodService,
                          public helpService: HelpService,
                          public datasetService: DatasetService,
                          public translateService: TranslateService,
                          public alertService: AlertService,
                          public modalService: NgbModal,
                          public advanceType: AdvanceType) {
        this.paramContext.readParams();
    }

    ngOnInit(): void {
        this.studyId = Number(this.route.snapshot.queryParamMap.get('studyId'));
        this.selectedInstances = this.route.snapshot.queryParamMap.get('trialInstances').split(',');
        this.replicationNumber = Number(this.route.snapshot.queryParamMap.get('noOfReplications'));

        const selectedDatasetId = this.route.snapshot.queryParamMap.get('selectedDatasetId');
        if (selectedDatasetId) {
            this.selectedDatasetId = Number(selectedDatasetId);
        }

        this.loadBreedingMethods();
        this.loadStudyVariables();
        this.loadDatasets();
        this.initializeReplicationOptions(this.replicationNumber);

        // Get helplink url
        if (!this.helpLink || !this.helpLink.length) {
            this.helpService.getHelpLink('MANAGE_CROP_BREEDING_METHODS').toPromise().then((response) => {
                this.helpLink = response.body;
            }).catch((error) => {
            });
        }
    }

    abstract isValid(): boolean;

    abstract save(): void;

    toggleAll() {
        this.replicationsOptions.forEach((replication) => replication.selected = !this.checkAllReplications);
    }

    toggleCheck(repCheck) {
        this.checkAllReplications = this.checkAllReplications && !repCheck;
    }

    back(advanceType: string) {
        if ((<any>window.parent)) {
            (<any>window.parent).postMessage({ name: SELECT_INSTANCES, advanceType, selectedDatasetId: this.selectedDatasetId }, '*');
        }
    }

    onMethodChange(selectedMethodId: string) {
        if (selectedMethodId && this.breedingMethods.length > 0) {
            this.breedingMethodSelectedId = selectedMethodId;
            this.selectedBreedingMethod = this.breedingMethods.find((method: BreedingMethod) => String(method.mid) === this.breedingMethodSelectedId);
            this.showSelectionTraitSelection = this.hasSelectTraitVariables() &&
                (this.selectedBreedingMethod.suffix === AbstractAdvanceComponent.SELECTION_TRAIT_EXPRESSION ||
                    this.selectedBreedingMethod.prefix === AbstractAdvanceComponent.SELECTION_TRAIT_EXPRESSION);
        }
    }

    onSelectionTraitLevelChanged(datasetId) {
        this.selectionTraitVariables = this.selectionTraitVariablesByDatasetIds.get(Number(datasetId));
        this.selectedSelectionTraitVariableId = null;
    }

    protected hasSelectTraitVariables() {
        for (const variables of this.selectionTraitVariablesByDatasetIds.values()) {
            if (variables && variables.length > 0) {
                return true;
            }
        }
        return false;
    }

    protected onAdvanceSuccess(gids: number[]) {
        this.isLoading = false;

        if (gids.length > 0) {
            this.alertService.success('advance.success');

            if ((<any>window.parent)) {
                (<any>window.parent).postMessage({ name: ADVANCE_SUCCESS }, '*');
            }

            this.showListCreationModal(gids);
        } else {
            this.alertService.error('advance.no-entries');
        }
    }

    protected onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
        this.isLoading = false;
    }

    private showListCreationModal(gids: number[]) {
        const germplasmListCreationModalRef = this.modalService.open(GermplasmListCreationComponent as Component,
            { windowClass: 'modal-large', backdrop: 'static' });
        germplasmListCreationModalRef.componentInstance.entries = gids.map((gid: number) => {
            const entry: GermplasmListEntry = new GermplasmListEntry();
            entry.gid = gid;
            return entry;
        });
    }

    private loadBreedingMethods() {
        this.breedingMethodOptions = {
            ajax: {
                delay: 500,
                transport: function(params, success, failure) {
                    params.data.page = params.data.page || 1;

                    if (params.data.page === 1) {
                        this.breedingMethods = [];
                    }

                    const breedingMethodSearchRequest: BreedingMethodSearchRequest = new BreedingMethodSearchRequest();
                    breedingMethodSearchRequest.nameFilter = {
                        type: MatchType.STARTSWITH,
                        value: params.data.term
                    };

                    breedingMethodSearchRequest.methodTypes = this.methodTypes;
                    const pagination = {
                        page: (params.data.page - 1),
                        size: AbstractAdvanceComponent.BREEDING_METHODS_PAGE_SIZE
                    };

                    if (this.advanceType === AdvanceType.SAMPLES) {
                        breedingMethodSearchRequest.methodClassIds = [BreedingMethodClassMethodEnum.NON_BULKING_BREEDING_METHOD_CLASS];
                    }

                    this.breedingMethodService.searchBreedingMethods(
                        breedingMethodSearchRequest,
                        this.useFavoriteBreedingMethods,
                        pagination
                    ).subscribe((res: HttpResponse<BreedingMethod[]>) => {
                        this.breedingMethodsFilteredItemsCount = res.headers.get('X-Total-Count');
                        success(res.body);
                    }, failure);
                }.bind(this),
                processResults: function(methods, params) {
                    params.page = params.page || 1;

                    this.breedingMethods = this.breedingMethods.concat(...methods)

                    return {
                        results: methods.map((method: BreedingMethod) => {
                            return {
                                id: String(method.mid),
                                text: method.code + ' - ' + method.name
                            };
                        }),
                        pagination: {
                            more: (params.page * AbstractAdvanceComponent.BREEDING_METHODS_PAGE_SIZE) < this.breedingMethodsFilteredItemsCount
                        }
                    };
                }.bind(this)
            }
        };
    }

    private loadStudyVariables() {
        this.datasetService.getVariablesByVariableType(this.studyId, [VariableTypeEnum.STUDY_DETAIL]).toPromise().then((response: HttpResponse<ObservationVariable[]>) => {
            this.selectionTraitVariablesByDatasetIds.set(this.studyId, this.filterSelectionTraitVariable(response.body));
            this.studyVariablesLoaded = true;
            this.initializeSelectionTraitLevels();
        });
    }

    private initializeSelectionTraitLevels() {
        if (this.datasetsLoaded && this.studyVariablesLoaded) {
            this.selectionTraitLevelOptions.push(
                this.getSelectionTraitLevel(SelectionTraitLevelTypes.STUDY, this.studyId));
            this.selectionTraitLevelOptions.push(
                this.getSelectionTraitLevel(SelectionTraitLevelTypes.ENVIRONMENT, this.environmentDatasetId));
            this.selectionTraitLevelOptions.push(
                this.getSelectionTraitLevel(SelectionTraitLevelTypes.PLOT, this.plotDatasetId));
        }
    }

    private filterSelectionTraitVariable(variables: ObservationVariable[]): ObservationVariable[] {
        return variables.filter((variable: ObservationVariable) => variable.property === AbstractAdvanceComponent.SELECTION_TRAIT_PROPERTY);
    }

    private getSelectionTraitLevel(type: SelectionTraitLevelTypes, value: number): any {
        return {
            label: this.translateService.instant('advance-study.selection-trait.level-type.' + type),
            show: this.selectionTraitVariablesByDatasetIds.get(value) && this.selectionTraitVariablesByDatasetIds.get(value).length > 0,
            value
        };
    }

    private loadDatasets() {
        this.datasetService.getDatasetsByTypeIds(this.studyId, [DatasetTypeEnum.ENVIRONMENT, DatasetTypeEnum.PLOT]).toPromise().then((response: HttpResponse<DatasetModel[]>) => {
            response.body.forEach((dataset: DatasetModel) => {

                if (dataset.datasetTypeId === DatasetTypeEnum.ENVIRONMENT) {
                    this.environmentDatasetId = dataset.datasetId;
                    this.selectionTraitVariablesByDatasetIds.set(this.environmentDatasetId, this.filterSelectionTraitVariable(dataset.variables));
                    dataset.instances.forEach((instance: StudyInstanceModel) => {
                        if (this.selectedInstances.includes(instance.instanceNumber.toString())) {
                            this.trialInstances.push({
                                instanceId: instance.instanceId,
                                instanceNumber: instance.instanceNumber,
                                locAbbr: instance.locationName + ' - (' + instance.locationAbbreviation + ')'
                            });
                        }
                    });

                } else if (dataset.datasetTypeId === DatasetTypeEnum.PLOT) {
                    this.plotDatasetId = dataset.datasetId;

                    this.selectionMethodVariables = this.filterVariablesByProperty(dataset.variables, AbstractAdvanceComponent.BREEDING_METHOD_PROPERTY);
                    this.selectionPlantVariables = this.filterVariablesByProperty(dataset.variables, AbstractAdvanceComponent.SELECTION_PLANT_PROPERTY);

                    this.selectionTraitVariablesByDatasetIds.set(this.plotDatasetId, this.filterSelectionTraitVariable(dataset.variables));
                }
            });

            this.datasetsLoaded = true;
            this.initializeSelectionTraitLevels();
        });
    }

    private filterVariablesByProperty(variables: ObservationVariable[], property: string): ObservationVariable[] {
        return variables.filter((variable: ObservationVariable) =>
            (VariableTypeEnum[VariableTypeEnum.SELECTION_METHOD] === variable.variableType.toString() && variable.property === property));
    }

    private initializeReplicationOptions(replicationNumber: number) {
        if (replicationNumber !== 0) {
            for (let rep = 1; rep <= replicationNumber; rep++) {
                this.replicationsOptions.push({ index: rep, selected: (rep === 1) });
            }
        }
    }

}

enum SelectionTraitLevelTypes {
    STUDY = 'study',
    ENVIRONMENT = 'environment',
    PLOT = 'plot'
}
