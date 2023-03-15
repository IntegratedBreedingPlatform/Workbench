import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { finalize } from 'rxjs/internal/operators/finalize';
import { AbstractAdvanceComponent, AdvanceType } from './abstract-advance.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ParamContext } from '../../shared/service/param.context';
import { BreedingMethodService } from '../../shared/breeding-method/service/breeding-method.service';
import { HelpService } from '../../shared/service/help.service';
import { DatasetService } from '../../shared/dataset/service/dataset.service';
import { AlertService } from '../../shared/alert/alert.service';
import { AdvanceService } from '../../shared/study/service/advance.service';
import { AdvanceStudyRequest, BreedingMethodSelectionRequest, BulkingRequest, LineSelectionRequest } from '../../shared/study/model/advance-study-request.model';
import { SelectionTraitRequest } from '../../shared/study/model/abstract-advance-request.model';
import { AdvancedGermplasmPreview } from '../../shared/study/model/advanced-germplasm-preview';
import { FilterType } from '../../shared/column-filter/column-filter.component';

@Component({
    selector: 'jhi-advance-study',
    templateUrl: './advance-study.component.html'
})
export class AdvanceStudyComponent extends AbstractAdvanceComponent {

    breedingMethodCheck = true;
    breedingMethodSelectedVariableId: number;

    showBreedingMethodVariableSelection = false;

    linesCheck = true;
    showLinesSelection = true;
    selectedLinesNumber = 1;
    selectedLinesVariableId: number;

    bulksCheck = true;
    showBulkingSelection = false;
    selectedPlotVariableId: number;

    isLoadingPreview = false;
    totalItems: number;
    currentPageCount: number;
    page: number = 1;
    previousPage: number;
    isPreview = false;

    private readonly itemsPerPage: number = 10;

    completePreviewList: AdvancedGermplasmPreview[];
    listPerPage: AdvancedGermplasmPreview[][];
    currentPagePreviewList: AdvancedGermplasmPreview[];
    selectedItems = [];

    filters = {
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
    }

    constructor(public paramContext: ParamContext,
                public route: ActivatedRoute,
                public breedingMethodService: BreedingMethodService,
                public helpService: HelpService,
                public datasetService: DatasetService,
                public translateService: TranslateService,
                public alertService: AlertService,
                public jhiLanguageService: JhiLanguageService,
                public advanceService: AdvanceService,
                public modalService: NgbModal
    ) {
        super(paramContext, route, breedingMethodService, helpService, datasetService, translateService, alertService, modalService, AdvanceType.STUDY);
    }

    save(): void {
        this.isLoading = true;

        const selectedInstanceIds: number[] = this.trialInstances.map((instance) => instance.instanceId);
        const selectedReplicationNumbers: number[] =
            this.replicationsOptions.filter((replication: any) => replication.selected)
                .map((replication: any) => replication.index);
        const breedingMethodSelectionRequest: BreedingMethodSelectionRequest = new BreedingMethodSelectionRequest();
        if (this.showBreedingMethodVariableSelection) {
            breedingMethodSelectionRequest.methodVariateId = this.breedingMethodSelectedVariableId;
        } else {
            breedingMethodSelectionRequest.breedingMethodId = Number(this.breedingMethodSelectedId);
        }

        const advanceStudyRequest: AdvanceStudyRequest =
            new AdvanceStudyRequest(this.selectedDatasetId, selectedInstanceIds, selectedReplicationNumbers, breedingMethodSelectionRequest);
        if (this.showSelectionTraitSelection) {
            const selectionTraitRequest: SelectionTraitRequest = new SelectionTraitRequest(this.selectedSelectionTraitDatasetId, this.selectedSelectionTraitVariableId);
            advanceStudyRequest.selectionTraitRequest = selectionTraitRequest;
        }

        if (this.showBreedingMethodVariableSelection || this.showLinesSelection) {
            const lineSelectionRequest: LineSelectionRequest = new LineSelectionRequest();
            if (this.linesCheck) {
                lineSelectionRequest.linesSelected = this.selectedLinesNumber;
            } else {
                lineSelectionRequest.lineVariateId = this.selectedLinesVariableId;
            }
            advanceStudyRequest.lineSelectionRequest = lineSelectionRequest;
        }

        if (this.showBulkingSelection) {
            const bulkingRequest: BulkingRequest = new BulkingRequest();
            if (this.bulksCheck) {
                bulkingRequest.allPlotsSelected = true;
            } else {
                bulkingRequest.plotVariateId = this.selectedPlotVariableId;
            }
            advanceStudyRequest.bulkingRequest = bulkingRequest;
        }

        this.advanceService.advanceStudy(this.studyId, advanceStudyRequest)
            .pipe(finalize(() => this.isLoading = false))
            .subscribe(
                (res: number[]) => this.onAdvanceSuccess(res),
                (res) => this.onError(res));
    }

    deleteSelectedEntries(): void {
        this.page = 1;
        this.previousPage = 1;
        this.completePreviewList = [];
        this.listPerPage = [];
        this.preview(true);
    }

    preview(isDeletingEntries=false): void {
        this.isLoadingPreview = true;

        const selectedInstanceIds: number[] = this.trialInstances.map((instance) => instance.instanceId);
        const selectedReplicationNumbers: number[] =
            this.replicationsOptions.filter((replication: any) => replication.selected)
                .map((replication: any) => replication.index);
        const breedingMethodSelectionRequest: BreedingMethodSelectionRequest = new BreedingMethodSelectionRequest();
        if (this.showBreedingMethodVariableSelection) {
            breedingMethodSelectionRequest.methodVariateId = this.breedingMethodSelectedVariableId;
        } else {
            breedingMethodSelectionRequest.breedingMethodId = Number(this.breedingMethodSelectedId);
        }

        const advanceStudyRequest: AdvanceStudyRequest =
            new AdvanceStudyRequest(this.selectedDatasetId, selectedInstanceIds, selectedReplicationNumbers, breedingMethodSelectionRequest);

        if (isDeletingEntries && this.selectedItems.length >= 1) {
            advanceStudyRequest.excludedObservations = this.selectedItems
        }

        else if (isDeletingEntries && this.selectedItems.length < 1) {
            this.alertService.error('error.custom', { param: "Please select at least 1 entry." });
        }

        if (this.showSelectionTraitSelection) {
            const selectionTraitRequest: SelectionTraitRequest = new SelectionTraitRequest(this.selectedSelectionTraitDatasetId, this.selectedSelectionTraitVariableId);
            advanceStudyRequest.selectionTraitRequest = selectionTraitRequest;
        }

        if (this.showBreedingMethodVariableSelection || this.showLinesSelection) {
            const lineSelectionRequest: LineSelectionRequest = new LineSelectionRequest();
            if (this.linesCheck) {
                lineSelectionRequest.linesSelected = this.selectedLinesNumber;
            } else {
                lineSelectionRequest.lineVariateId = this.selectedLinesVariableId;
            }
            advanceStudyRequest.lineSelectionRequest = lineSelectionRequest;
        }

        if (this.showBulkingSelection) {
            const bulkingRequest: BulkingRequest = new BulkingRequest();
            if (this.bulksCheck) {
                bulkingRequest.allPlotsSelected = true;
            } else {
                bulkingRequest.plotVariateId = this.selectedPlotVariableId;
            }
            advanceStudyRequest.bulkingRequest = bulkingRequest;
        }

        this.advanceService.advanceStudyPreview(this.studyId, advanceStudyRequest)
            .pipe(finalize(() => this.isLoadingPreview = false))
            .subscribe(
                (res: AdvancedGermplasmPreview[]) => this.onSuccess(res, isDeletingEntries),
                (res) => this.onError(res));
    }

    private onSuccess(data: AdvancedGermplasmPreview[], fromDelete=false) {
        this.completePreviewList = data;
        this.processPagination(this.completePreviewList);
        this.loadPage(1, fromDelete);
        this.isPreview = true;
    }

    onSelectMethodVariable(e) {
        if (this.selectionMethodVariables.length > 0) {
            this.showBreedingMethodVariableSelection = !this.showBreedingMethodVariableSelection;
            this.showBulkingSelection = true;

            this.showSelectionTraitSelection = this.breedingMethodCheck && this.hasSelectTraitVariables();
        } else {
            e.preventDefault();
            this.alertService.error('advance-study.errors.breeding-method.selection.variate.not-present');
        }
    }

    onMethodChange(selectedMethodId: string) {
        super.onMethodChange(selectedMethodId);
        this.showLinesSelection = !this.selectedBreedingMethod.isBulkingMethod;
        this.showBulkingSelection = this.selectedBreedingMethod.isBulkingMethod;
    }

    onSelectLineVariable(e) {
        if (this.selectionPlantVariables.length === 0) {
            e.preventDefault();
            e.stopPropagation();

            this.alertService.error('advance-study.errors.lines.selection.variate.not-present');
        }
    }

    onSelectPlotVariable(e) {
        if (this.selectionPlantVariables.length === 0) {
            e.preventDefault();
            e.stopPropagation();

            this.alertService.error('advance-study.errors.lines.selection.variate.not-present');
        }
    }

    isValid(): boolean {
        // Variable for selection method was not selected
        if (this.showBreedingMethodVariableSelection && !this.breedingMethodSelectedVariableId) {
            return false;
        }
        // No same method for each advance was selected
        if (!this.showBreedingMethodVariableSelection && !this.breedingMethodSelectedId) {
            return false;
        }

        // Selection trait was no selected
        if (this.showSelectionTraitSelection && (!this.selectedSelectionTraitDatasetId || !this.selectedSelectionTraitVariableId)) {
            return false;
        }

        if (this.showLinesSelection) {
            // Same number of lines for each plot was not defined
            if (this.linesCheck && (!this.selectedLinesNumber || this.selectedLinesNumber < 0)) {
                return false;
            }
            // Variable that defines the number of selected lines was not selected
            if (!this.linesCheck && !this.selectedLinesVariableId) {
                return false;
            }
        }

        // Variable that defines the number of lines selected from each plot was not selected
        if (this.showBulkingSelection && !this.bulksCheck && !this.selectedPlotVariableId) {
            return false;
        }

        // Replications were not selected
        if (this.replicationsOptions.length && this.replicationsOptions.filter((rep: any) => rep.selected).length === 0 && !this.checkAllReplications) {
            return false;
        }

        return true;
    }

    loadPage(page: number, forceReload = false) {
        if (page !== this.previousPage || forceReload) {
            this.previousPage = page;
            this.currentPagePreviewList = this.listPerPage[page - 1];
            var itemCount = this.currentPagePreviewList.length;
            this.currentPageCount = ((page - 1) * this.itemsPerPage) + itemCount;
        }
    }

    exitPreview() {
        this.isPreview = false;
    }

    applyFilters() {
        this.page = 1;
        this.previousPage = 1;
        let filteredList = this.completePreviewList.filter(
          row => {
              let env = (row.trialInstance + "-" + row.locationName).toLowerCase();
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

    toggleSelect = function ($event, idx, observationUnitId) {
        var idx = this.selectedItems.indexOf(observationUnitId);
        if (idx > -1) {
            this.selectedItems.splice(idx, 1)
        } else {
            this.selectedItems.push(observationUnitId);
        }

        $event.stopPropagation();
    };

    isSelected(observationUnitId: number) {
        return observationUnitId && this.selectedItems.length > 0 && this.selectedItems.find((item) => item === observationUnitId);
    }
}
