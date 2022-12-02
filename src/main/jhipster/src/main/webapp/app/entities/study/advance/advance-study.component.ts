import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { ParamContext } from '../../../shared/service/param.context';
import { AlertService } from '../../../shared/alert/alert.service';
import { BreedingMethodSearchRequest } from '../../../shared/breeding-method/model/breeding-method-search-request.model';
import { MatchType } from '../../../shared/column-filter/column-filter-text-with-match-options-component';
import { HttpResponse } from '@angular/common/http';
import { BreedingMethod } from '../../../shared/breeding-method/model/breeding-method';
import { HelpService } from '../../../shared/service/help.service';
import { BreedingMethodTypeEnum } from '../../../shared/breeding-method/model/breeding-method-type.model';
import { DatasetService } from '../../../shared/dataset/service/datasetService';
import { DatasetModel } from '../../../shared/dataset/model/dataset.model';
import { StudyInstanceModel } from '../../../shared/dataset/model/study-instance.model';
import { ObservationVariable } from '../../../shared/model/observation-variable.model';
import { VariableTypeEnum } from '../../../shared/ontology/variable-type.enum';
import { BreedingMethodService } from '../../../shared/breeding-method/service/breeding-method.service';

@Component({
    selector: 'jhi-advance-study',
    templateUrl: './advance-study.component.html'
})
export class AdvanceStudyComponent implements OnInit {

    static readonly BREEDING_METHODS_PAGE_SIZE = 300;
    static readonly BREEDING_METHOD_PROPERTY = 'Breeding method';
    static readonly SELECTION_PLANT_PROPERTY = 'Selections';

    breedingMethods: BreedingMethod[] = [];
    breedingMethodOptions: any;
    breedingMethodSelectedId: string;
    breedingMethodType = '1';
    useFavoriteBreedingMethods = false;
    methodTypes: BreedingMethodTypeEnum[] = [BreedingMethodTypeEnum.DERIVATIVE, BreedingMethodTypeEnum.MAINTENANCE];
    breedingMethodSelectedVariable: ObservationVariable;

    showBreedingMethodVariableSelection = false;
    selectionMethodVariables: ObservationVariable[] = [];

    showLinesSelection = true;
    showLinesVariableSelection = false;
    selectedLinesNumber = 1;
    selectedLinesVariable: ObservationVariable;

    showPlotVariableSelection = false;
    selectedPlotVariable: ObservationVariable;

    checkAllReplications = true;

    isLoading = false;
    isSuccess = false;
    helpLink: string;

    studyId: any;
    trialDatasetId: any;
    plotDatasetId: any;
    selectedInstances: any[];
    trialInstances: any[] = [];
    replicationNumber: number;

    replicationsOptions: any[] = [];

    selectionPlantVariables: ObservationVariable[] = [];

    constructor(private route: ActivatedRoute,
                private jhiLanguageService: JhiLanguageService,
                private paramContext: ParamContext,
                private alertService: AlertService,
                private router: Router,
                private breedingMethodService: BreedingMethodService,
                private helpService: HelpService,
                private datasetService: DatasetService
    ) {
        this.paramContext.readParams();
    }

    ngOnInit(): void {
        this.studyId = Number(this.route.snapshot.queryParamMap.get('studyId'));
        this.trialDatasetId = Number(this.route.snapshot.queryParamMap.get('trialDatasetId'));
        this.plotDatasetId = Number(this.route.snapshot.queryParamMap.get('plotDatasetId'));
        this.selectedInstances = this.route.snapshot.queryParamMap.get('trialInstances').split(',');
        this.replicationNumber = Number(this.route.snapshot.queryParamMap.get('noOfReplications'));

        this.initializeReplicationOptions(this.replicationNumber);

        this.loadBreedingMethods();
        this.loadTrialDataset();
        this.loadPlotDataset();

        // Get helplink url
        if (!this.helpLink || !this.helpLink.length) {
            this.helpService.getHelpLink('MANAGE_CROP_BREEDING_METHODS').toPromise().then((response) => {
                this.helpLink = response.body;
            }).catch((error) => {
            });
        }
    }

    initializeReplicationOptions(replicationNumber: number) {
        if (replicationNumber !== 0) {
            for (let rep = 1; rep <= replicationNumber; rep++) {
                this.replicationsOptions.push({ repIndex: rep, selected: true });
            }
        }
    }

    checkUncheckAll() {
        this.replicationsOptions.forEach((replication) => replication.selected = !this.checkAllReplications);
    }

    toggleCheck(repCheck) {
        this.checkAllReplications = !repCheck;
    }

    save(): void {
        this.router.navigate(['/', { outlets: { popup: 'germplasm-list-creation-dialog' }, }], {
            replaceUrl: true,
            queryParamsHandling: 'merge'
        });
    }

    dismiss() {
        // Handle closing of modal when this page is loaded outside of Angular.
        if ((<any>window.parent).closeModal) {
            (<any>window.parent).closeModal();
        }
        if ((<any>window.parent)) {
            (<any>window.parent).postMessage({ name: 'cancel', 'value': '' }, '*');
        }
    }

    onSelectMethodVariable(e) {
        if (this.selectionMethodVariables.length > 0) {
            this.showBreedingMethodVariableSelection = !this.showBreedingMethodVariableSelection;
        } else {
            e.preventDefault();
            this.alertService.error('advance-study.errors.breeding-method.selection.variate.not-present');
        }
    }

    onMethodChange(selectedMethodId: string) {
        if (selectedMethodId && this.breedingMethods.length > 0) {
            this.breedingMethodSelectedId = selectedMethodId;
            const selectedBreedingMethod: BreedingMethod = this.breedingMethods.find((method: BreedingMethod) => String(method.mid) === this.breedingMethodSelectedId);
            this.showLinesSelection = !selectedBreedingMethod.isBulkingMethod;
        }
    }

    onSelectLineVariable(e) {
        if (this.selectionPlantVariables.length > 0) {
            this.showLinesVariableSelection = !this.showLinesVariableSelection;
        } else {
            e.preventDefault();
            this.alertService.error('advance-study.errors.lines.selection.variate.not-present');
        }
    }

    onSelectPlotVariable(e) {
        if (this.selectionPlantVariables.length > 0) {
            this.showPlotVariableSelection = !this.showPlotVariableSelection;
        } else {
            e.preventDefault();
            this.alertService.error('advance-study.errors.lines.selection.variate.not-present');
        }
    }

    private loadTrialDataset() {
        this.datasetService.getDataset(this.studyId, this.trialDatasetId).toPromise().then((response: HttpResponse<DatasetModel>) => {
            response.body.instances.forEach((instance: StudyInstanceModel) => {
                if (this.selectedInstances.includes(instance.instanceNumber.toString())) {
                    this.trialInstances.push({
                        instanceNumber: instance.instanceNumber,
                        locAbbr: instance.locationName + ' - (' + instance.locationAbbreviation + ')',
                        abbrCode: instance.customLocationAbbreviation
                    });
                }
            });
        });
    }

    private loadPlotDataset() {
        this.datasetService.getDataset(this.studyId, this.plotDatasetId).toPromise().then((response: HttpResponse<DatasetModel>) => {
            const dataset: DatasetModel = response.body;
            this.selectionMethodVariables = this.filterVariablesByProperty(dataset.variables, AdvanceStudyComponent.BREEDING_METHOD_PROPERTY);
            this.selectionPlantVariables = this.filterVariablesByProperty(dataset.variables, AdvanceStudyComponent.SELECTION_PLANT_PROPERTY);
        });
    }

    private filterVariablesByProperty(variables: ObservationVariable[], property: string): ObservationVariable[] {
        return variables.filter((variable: ObservationVariable) =>
            (VariableTypeEnum[VariableTypeEnum.SELECTION_METHOD] === variable.variableType.toString() && variable.property === property));
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
                        size: AdvanceStudyComponent.BREEDING_METHODS_PAGE_SIZE
                    };

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
                            more: (params.page * AdvanceStudyComponent.BREEDING_METHODS_PAGE_SIZE) < this.breedingMethodsFilteredItemsCount
                        }
                    };
                }.bind(this)
            }
        };
    }

}
