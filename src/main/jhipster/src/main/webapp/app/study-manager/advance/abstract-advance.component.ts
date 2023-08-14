import { BreedingMethod } from '../../shared/breeding-method/model/breeding-method';
import { ParamContext } from '../../shared/service/param.context';
import { Component, OnInit } from '@angular/core';
import { ObservationVariable } from '../../shared/model/observation-variable.model';
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
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmListCreationComponent } from '../../shared/list-creation/germplasm-list-creation.component';
import { GermplasmListEntry } from '../../shared/list-creation/model/germplasm-list';
import { ADVANCE_SUCCESS, SELECT_INSTANCES } from '../../app.events';
import { AdvancedGermplasmPreview } from '../../shared/study/model/advanced-germplasm-preview';
import { FilterType } from '../../shared/column-filter/column-filter.component';
import { VariableDetails } from '../../shared/ontology/model/variable-details';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';
import { TemplateModel } from './template.model';
import { TemplateService } from './template.service';
import { VariableService } from '../../shared/ontology/service/variable.service';
import { SaveTemplateComponent } from './save-template.component';

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

    loadSavedSettings: boolean;
    propagateDescriptors: boolean;
    overrideDescriptorsLocation: boolean;
    locationOverrideId: number;

    templateId: number;
    templates: TemplateModel[];

    // for preview data table
    isLoadingPreview = false;
    originalTotalItems: number;
    totalItems: number;
    currentPageCount: number;
    page = 1;
    previousPage: number;
    isPreview = false;
    isAttributesPropagationView = false;

    itemsPerPage = 10;

    completePreviewList: AdvancedGermplasmPreview[];
    listPerPage: AdvancedGermplasmPreview[][];
    currentPagePreviewList: AdvancedGermplasmPreview[];

    VARIABLE_TYPE_IDS = [VariableTypeEnum.GERMPLASM_ATTRIBUTE, VariableTypeEnum.GERMPLASM_PASSPORT];
    DEFAULT_PASSPORT_DESCRIPTORS = ['PLOTCODE_AP_TEXT', 'PLOT_NUMBER_AP_TEXT', 'INSTANCE_NUMBER_AP_TEXT', 'REP_NUMBER_AP_TEXT', 'PLANT_NUMBER_AP_TEXT'];
    variable: VariableDetails;
    selectedDescriptors: VariableDetails[] = [];
    selectedDescriptorIds: number[] = [];

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
                          public advanceType: AdvanceType,
                          public templateService: TemplateService,
                          public activeModal: NgbActiveModal,
                          public variableService: VariableService) {
        this.paramContext.readParams();
    }

    ngOnInit(): void {
        this.isPreview = false;
        this.isAttributesPropagationView = true;
        this.studyId = Number(this.route.snapshot.queryParamMap.get('studyId'));
        this.selectedInstances = this.route.snapshot.queryParamMap.get('trialInstances').split(',');
        this.replicationNumber = Number(this.route.snapshot.queryParamMap.get('noOfReplications'));

        const selectedDatasetId = this.route.snapshot.queryParamMap.get('selectedDatasetId');
        if (selectedDatasetId) {
            this.selectedDatasetId = Number(selectedDatasetId);
        }

        this.loadStudyVariables();
        this.initializeReplicationOptions(this.replicationNumber);
        this.loadPresets();

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

    showPropagateAttributesView() {
        this.isAttributesPropagationView = true;
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
        this.originalTotalItems = data.length;
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

    exitAttributesPropagationView() {
        this.isAttributesPropagationView = false;
    }

    exitPreview() {
        this.resetTable();
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
        this.totalItems = list.length;

        if (this.totalItems === 0) {
            this.listPerPage = [];
            this.listPerPage.push([]);
            return;
        }

        this.listPerPage = list.reduce((resultArray, item, index) => {
            const pageIndex = Math.floor(index / this.itemsPerPage)

            if (!resultArray[pageIndex]) {
                resultArray[pageIndex] = [] // start a new page
            }

            resultArray[pageIndex].push(item)

            return resultArray
        }, []);
    }

    getReplicationNumber() {
        return this.replicationNumber ? this.replicationsOptions.filter((rep) => rep.selected).length : '-';
    }

    selectVariable(variable: VariableDetails) {
        this.variable = variable;
    }

    addDescriptor() {
        if (!this.selectedDescriptorIds.includes(parseInt(this.variable.id, 10))) {
            this.selectedDescriptors.push(this.variable);
            this.selectedDescriptorIds.push(parseInt(this.variable.id, 10));
            this.variable = null;
        }
    }

    removeFromSelectedDescriptors(toRemove: VariableDetails) {
        this.selectedDescriptorIds = this.selectedDescriptorIds.filter((id) => id !== parseInt(toRemove.id, 10));
        this.selectedDescriptors = this.selectedDescriptors.filter((descriptor) => descriptor.id !== toRemove.id);
    }

    isPropagationInvalid() {
        if (this.propagateDescriptors && this.selectedDescriptorIds.length === 0) {
            return true;
        }

        if (this.overrideDescriptorsLocation && this.locationOverrideId === null) {
            return true;
        }
        return false;
    }

    deleteSelectedSetting() {
        const templateModel = this.templates.filter((template) => template.templateId === Number(this.templateId))[0];
        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.title = 'Delete preset?';
        confirmModalRef.componentInstance.message = 'Are you sure you want to delete ' + templateModel.templateName + ' ?';
        confirmModalRef.result.then(() => {
            this.templateService.deleteTemplate(this.templateId).subscribe(() => {
                this.alertService.success('advance-study.attributes.preset.delete.success');
                this.loadPresets();
            }, (response) => {
                if (response.error.errors[0].message) {
                    this.alertService.error('error.custom', { param: response.error.errors[0].message });
                } else {
                    this.alertService.error('error.general');
                }
            });
            this.activeModal.close();
        }, () => this.activeModal.dismiss());
        return;
    }

    applySelectedSetting() {
        if (Number(this.templateId) !== 0) {
            this.selectedDescriptors = [];
            this.selectedDescriptorIds = [];
            const templateModel = this.templates.filter((template) => template.templateId === Number(this.templateId))[0];
            const variableIds = [];
            templateModel.templateDetails.forEach((templateDetail) => {
                variableIds.push(templateDetail.variableId.toString());
            });
            this.variableService.filterVariables({ variableIds,
                variableTypeIds: [VariableTypeEnum.GERMPLASM_PASSPORT.toString(), VariableTypeEnum.GERMPLASM_ATTRIBUTE.toString()],
                showObsoletes: false}).subscribe((variables) => {
                this.selectedDescriptors = variables;
                variables.forEach((variable) => {
                   this.selectedDescriptorIds.push(parseInt(variable.id, 10));
                });
            });
        }
    }

    private loadPresets() {
        this.templateService.getAllTemplates().subscribe((templates) => {
            this.templates = templates;
            this.templateId = 0;
        }, (response) => {
            if (response.error.errors[0].message) {
                this.alertService.error('error.custom', { param: response.error.errors[0].message });
            } else {
                this.alertService.error('error.general');
            }
        });
    }

    saveTemplate() {
        if (Number(this.templateId) !== 0 ) {
            this.updateTemplate();
        } else {
            const template: TemplateModel = new TemplateModel();
            template.templateDetails = [];
            this.selectedDescriptors.forEach((variable) => {
                template.templateDetails.push({
                    variableId: parseInt(variable.id, 10),
                    name: variable.name,
                    type: variable.variableTypes[0].name
                });
            })
            template.programUUID = this.paramContext.programUUID;
            template.templateType = 'DESCRIPTORS';
            const saveTemplateModalRef = this.modalService.open(SaveTemplateComponent as Component);
            saveTemplateModalRef.result.then((templateName) => {
                template.templateName = templateName;
                this.templateService.addTemplate(template).subscribe((savedTemplate ) => {
                    this.alertService.success('advance-study.attributes.preset.update.success');
                    this.templates.push(savedTemplate);
                    this.templateId = savedTemplate.templateId;
                    this.loadSavedSettings = true;
                }, (response) => {
                    if (response.error.errors[0].message) {
                        this.alertService.error('error.custom', { param: response.error.errors[0].message });
                    } else {
                        this.alertService.error('error.general');
                    }
                });
                this.activeModal.close();
            }, () => this.activeModal.dismiss());
        }
    }

    updateTemplate() {
        const templateModel = this.templates.filter((template) => template.templateId === Number(this.templateId))[0];
        templateModel.templateDetails = [];
        this.selectedDescriptors.forEach((variable) => {
            templateModel.templateDetails.push({
                variableId: parseInt(variable.id, 10),
                name: variable.name,
                type: variable.variableTypes[0].name
            });
        })
        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.title = 'Confirmation';
        confirmModalRef.componentInstance.message = '"' + templateModel.templateName + '" already exists, do you wish to overwrite the setting? ';
        confirmModalRef.result.then(() => {
            this.templateService.updateTemplate(templateModel).subscribe((res: void) => {
                this.alertService.success('advance-study.attributes.preset.update.success');
            }, (response) => {
                if (response.error.errors[0].message) {
                    this.alertService.error('error.custom', { param: response.error.errors[0].message });
                } else {
                    this.alertService.error('error.general');
                }
            });
            this.activeModal.close();
        }, () => this.activeModal.dismiss());
    }
}

enum SelectionTraitLevelTypes {
    STUDY_CONDITIONS = 'study',
    ENVIRONMENT = 'environment',
    PLOT = 'observation',
    PLANT_SUBOBSERVATIONS = 'plant-sub-observation'
}
