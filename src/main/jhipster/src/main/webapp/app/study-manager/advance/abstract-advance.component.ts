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
import { BreedingMethodClassMethodEnum } from '../../shared/breeding-method/model/breeding-method-class.enum';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmListCreationComponent } from '../../shared/list-creation/germplasm-list-creation.component';
import { GermplasmListEntry } from '../../shared/list-creation/model/germplasm-list';
import { ADVANCE_SUCCESS, SELECT_INSTANCES } from '../../app.events';
import { AdvancedGermplasmPreview } from '../../shared/study/model/advanced-germplasm-preview';
import { FilterType } from '../../shared/column-filter/column-filter.component';

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

    breedingMethodSelectedId: string;

    selectionMethodVariables: ObservationVariable[] = [];

    showSelectionTraitSelection = false;

    checkAllReplications = false;

    isLoading = false;
    helpLink: string;

    studyId: any;
    environmentDatasetId: number;
    observationDatasetId: number;
    subObservationDatasetId: number;

    selectedInstances: any[];
    trialInstances: any[] = [];
    replicationNumber: number;

    selectedDatasetId: number;
    selectedDatasetName: string;
    selectedDatasetTypeId: DatasetTypeEnum;

    replicationsOptions: any[] = [];

    selectionPlantVariables: ObservationVariable[] = [];

    selectionTraitLevelOptions: any = [];
    selectionTraitVariablesByDatasetIds: Map<number, ObservationVariable[]> = new Map();
    selectionTraitVariables: ObservationVariable[] = [];
    selectedSelectionTraitDatasetId: number;
    selectedSelectionTraitVariableId: number;

    // for preview data table
    isLoadingPreview = false;
    totalItems: number;
    currentPageCount: number;
    page = 1;
    previousPage: number;
    isPreview = false;

    itemsPerPage = 10;

    completePreviewList: AdvancedGermplasmPreview[];
    listPerPage: AdvancedGermplasmPreview[][];
    currentPagePreviewList: AdvancedGermplasmPreview[];
    selectedItems = [];

    filters = this.getInitialFilters();

    private getInitialFilters() {
        return {
            environment: {
                key: 'environment',
                type: FilterType.TEXT,
                value: ''
            },
            plotNumber: {
                key: 'plotNumber',
                type: FilterType.TEXT,
                value: ''
            },
            plantNumber: {
                key: 'plantNumber',
                type: FilterType.TEXT,
                value: ''
            },
            entryNumber: {
                key: 'entryNumber',
                type: FilterType.TEXT,
                value: ''
            },
            cross: {
                key: 'cross',
                type: FilterType.TEXT,
                value: ''
            },
            immediateSource: {
                key: 'immediateSource',
                type: FilterType.TEXT,
                value: ''
            },
            breedingMethod: {
                key: 'breedingMethod',
                type: FilterType.TEXT,
                value: ''
            }
        };
    }

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

        this.loadStudyVariables();
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

    onMethodChange(selectedBreedingMethod: BreedingMethod) {
        if (selectedBreedingMethod) {
            this.breedingMethodSelectedId = String(selectedBreedingMethod.mid);
            this.showSelectionTraitSelection = this.hasSelectTraitVariables() &&
                (selectedBreedingMethod.suffix === AbstractAdvanceComponent.SELECTION_TRAIT_EXPRESSION ||
                    selectedBreedingMethod.prefix === AbstractAdvanceComponent.SELECTION_TRAIT_EXPRESSION);
        }
    }

    onSelectionTraitLevelChanged(datasetId) {
        this.selectionTraitVariables = this.selectionTraitVariablesByDatasetIds.get(Number(datasetId));
        this.selectedSelectionTraitVariableId = null;
    }

    isPlotDataset() {
        return this.selectedDatasetTypeId === DatasetTypeEnum.PLOT;
    }

    isPlantDataset() {
        return this.selectedDatasetTypeId === DatasetTypeEnum.PLANT_SUBOBSERVATIONS;
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

    private loadStudyVariables() {
        this.datasetService.getVariablesByVariableType(this.studyId, [VariableTypeEnum.STUDY_DETAIL])
            .toPromise().then((response: HttpResponse<ObservationVariable[]>) => {
            this.selectionTraitVariablesByDatasetIds.set(this.studyId, this.filterSelectionTraitVariable(response.body));
            this.loadDatasets();
        });
    }

    private initializeSelectionTraitLevels(advanceFromSubObservation: boolean) {
        this.selectionTraitLevelOptions.push(
            this.getSelectionTraitLevel(SelectionTraitLevelTypes.STUDY_CONDITIONS, this.studyId));
        this.selectionTraitLevelOptions.push(
            this.getSelectionTraitLevel(SelectionTraitLevelTypes.ENVIRONMENT, this.environmentDatasetId));

        if (advanceFromSubObservation) {
            const level: SelectionTraitLevelTypes = SelectionTraitLevelTypes[DatasetTypeEnum[this.selectedDatasetTypeId]];
            this.selectionTraitLevelOptions.push(
                this.getSelectionTraitLevel(level, this.subObservationDatasetId));
        } else {
            this.selectionTraitLevelOptions.push(
                this.getSelectionTraitLevel(SelectionTraitLevelTypes.PLOT, this.observationDatasetId));
        }
    }

    private filterSelectionTraitVariable(variables: ObservationVariable[]): ObservationVariable[] {
        return variables.filter((variable: ObservationVariable) => variable.property === AbstractAdvanceComponent.SELECTION_TRAIT_PROPERTY);
    }

    private getSelectionTraitLevel(type: SelectionTraitLevelTypes, value: number): any {
        return {
            label: this.translateService.instant('advance-study.selection-trait.level-type.' + type, { datasetName: this.selectedDatasetName }),
            show: this.selectionTraitVariablesByDatasetIds.get(value) && this.selectionTraitVariablesByDatasetIds.get(value).length > 0,
            value
        };
    }

    private loadDatasets() {
        this.datasetService.getDatasetsByTypeIds(this.studyId, [DatasetTypeEnum.ENVIRONMENT, DatasetTypeEnum.PLOT, DatasetTypeEnum.PLANT_SUBOBSERVATIONS])
            .toPromise().then((response: HttpResponse<DatasetModel[]>) => {
            let advanceFromSubObservation = false;
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

                } else if (dataset.datasetTypeId === DatasetTypeEnum.PLOT || dataset.datasetTypeId === DatasetTypeEnum.PLANT_SUBOBSERVATIONS) {
                    const isSelectedDataset = dataset.datasetId === this.selectedDatasetId;
                    if (isSelectedDataset) {
                        this.selectedDatasetName = dataset.name;
                        this.selectedDatasetTypeId = dataset.datasetTypeId;

                        if (dataset.datasetTypeId !== DatasetTypeEnum.PLOT) {
                            advanceFromSubObservation = true;
                        }
                    }

                    if (dataset.datasetTypeId === DatasetTypeEnum.PLOT) {
                        this.observationDatasetId = dataset.datasetId;
                    } else if (isSelectedDataset) {
                        this.subObservationDatasetId = dataset.datasetId;
                    }

                    if (this.selectedDatasetId && dataset.datasetId !== this.selectedDatasetId) {
                        return;
                    }

                    this.selectionMethodVariables = this.filterVariablesByProperty(dataset.variables, AbstractAdvanceComponent.BREEDING_METHOD_PROPERTY);
                    this.selectionPlantVariables = this.filterVariablesByProperty(dataset.variables, AbstractAdvanceComponent.SELECTION_PLANT_PROPERTY);

                    this.selectionTraitVariablesByDatasetIds.set(dataset.datasetId, this.filterSelectionTraitVariable(dataset.variables));

                }
            });

            this.initializeSelectionTraitLevels(advanceFromSubObservation);
        });
    }

    private filterVariablesByProperty(variables: ObservationVariable[], property: string): ObservationVariable[] {
        return variables.filter((variable: ObservationVariable) =>
            (VariableTypeEnum[VariableTypeEnum.SELECTION_METHOD] === variable.variableType.toString() && variable.property === property));
    }

    private initializeReplicationOptions(replicationNumber: number) {
        if (!!replicationNumber && replicationNumber !== 0) {
            for (let rep = 1; rep <= replicationNumber; rep++) {
                this.replicationsOptions.push({ index: rep, selected: (rep === 1) });
            }
        }
    }

    // methods for advance preview
    resetTable(): void {
        this.page = 1;
        this.previousPage = 1;
        this.listPerPage = [];
        this.filters = this.getInitialFilters();
    }

    onSuccess(data: AdvancedGermplasmPreview[], forceReload= false) {
        this.completePreviewList = data;
        this.processPagination(this.completePreviewList);
        this.loadPage(1, forceReload);
        this.isPreview = true;
    }

    loadPage(page: number, forceReload = false) {
        if (page !== this.previousPage || forceReload) {
            this.previousPage = page;
            this.currentPagePreviewList = this.listPerPage[page - 1];
            const itemCount = this.currentPagePreviewList.length;
            this.currentPageCount = ((page - 1) * this.itemsPerPage) + itemCount;
        }
    }

    exitPreview() {
        this.resetTable();
        this.selectedItems = [];
        this.isPreview = false;
    }

    applyFilters() {
        this.page = 1;
        this.previousPage = 1;
        const filteredList = this.completePreviewList.filter(
            (row) => {
                const env = (row.trialInstance + '-' + row.locationName).toLowerCase();
                if (this.filters.environment.value && !env.includes(this.filters.environment.value.toLowerCase())) {
                    return false;
                }

                if (this.filters.plotNumber.value && row.plotNumber !== this.filters.plotNumber.value) {
                    return false;
                }

                if (this.filters.plantNumber.value && row.plantNumber !== this.filters.plantNumber.value) {
                    return false;
                }

                if (this.filters.entryNumber.value && row.entryNumber !== this.filters.entryNumber.value) {
                    return false;
                }

                if (this.filters.cross.value && !row.cross.toLowerCase().includes(this.filters.cross.value.toLowerCase())) {
                    return false;
                }

                if (this.filters.immediateSource.value && !row.immediateSource.toLowerCase().includes(this.filters.immediateSource.value.toLowerCase())) {
                    return false;
                }

                if (this.filters.breedingMethod.value && !row.breedingMethodAbbr.toLowerCase().includes(this.filters.breedingMethod.value.toLowerCase())) {
                    return false;
                }

                return true;
            }
        );

        this.processPagination(filteredList);
        this.loadPage(1, true);
    }

    processPagination(list: AdvancedGermplasmPreview[]) {
        const filteredList = list.filter((row) => !row.deleted);
        this.totalItems = filteredList.length;

        if (this.totalItems === 0) {
            this.listPerPage = [];
            this.listPerPage.push([]);
            return;
        }

        this.listPerPage = filteredList.reduce((resultArray, item, index) => {
            const pageIndex = Math.floor(index / this.itemsPerPage)

            if (!resultArray[pageIndex]) {
                resultArray[pageIndex] = [] // start a new page
            }

            resultArray[pageIndex].push(item)

            return resultArray
        }, []);
    }

    toggleSelect = function($event, uniqueId) {
        const idx = this.selectedItems.indexOf(uniqueId);
        if (idx > -1) {
            this.selectedItems.splice(idx, 1)
        } else {
            this.selectedItems.push(uniqueId);
        }

        $event.stopPropagation();
    };

    isSelected(observationUnitId: number) {
        return observationUnitId && this.selectedItems.length > 0 && this.selectedItems.find((item) => item === observationUnitId);
    }

    onSelectPage() {
        if (this.isPageSelected()) {
            // remove all items
            this.currentPagePreviewList.forEach((entry: AdvancedGermplasmPreview) =>
                this.selectedItems.splice(this.selectedItems.indexOf(entry.uniqueId), 1));
        } else {
            // check remaining items
            this.currentPagePreviewList.forEach((entry: AdvancedGermplasmPreview) =>
                this.selectedItems.push(entry.uniqueId));
        }
    }

    isPageSelected() {
        return this.selectedItems.length && this.currentPagePreviewList.every(
            (p) => Boolean(this.selectedItems.indexOf(p.uniqueId)  > -1));
    }

    getReplicationNumber() {
        return this.replicationNumber ? this.replicationsOptions.filter((rep) => rep.selected).length : '-';
    }
}

enum SelectionTraitLevelTypes {
    STUDY_CONDITIONS = 'study',
    ENVIRONMENT = 'environment',
    PLOT = 'observation',
    PLANT_SUBOBSERVATIONS = 'plant-sub-observation'
}
