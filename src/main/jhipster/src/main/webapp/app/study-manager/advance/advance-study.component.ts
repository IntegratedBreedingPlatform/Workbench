import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiEventManager, JhiLanguageService } from 'ng-jhipster';
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
import { BreedingMethod } from '../../shared/breeding-method/model/breeding-method';

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

    constructor(public paramContext: ParamContext,
                public route: ActivatedRoute,
                public breedingMethodService: BreedingMethodService,
                public helpService: HelpService,
                public datasetService: DatasetService,
                public translateService: TranslateService,
                public alertService: AlertService,
                public jhiLanguageService: JhiLanguageService,
                public advanceService: AdvanceService,
                public modalService: NgbModal,
                public eventManager: JhiEventManager
    ) {
        super(paramContext, route, breedingMethodService, helpService, datasetService, translateService, alertService, modalService, AdvanceType.STUDY, eventManager);
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

        advanceStudyRequest.propagateDescriptors = this.propagateDescriptors;
        advanceStudyRequest.descriptorIds = this.selectedDescriptorIds;
        advanceStudyRequest.overrideDescriptorsLocation = this.overrideDescriptorsLocation;
        advanceStudyRequest.locationOverrideId = this.locationOverrideId;

        this.advanceService.advanceStudy(this.studyId, advanceStudyRequest)
            .pipe(finalize(() => this.isLoading = false))
            .subscribe(
                (res: number[]) => this.onAdvanceSuccess(res),
                (res) => this.onError(res));
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

    onMethodChange(selectedBreedingMethod: BreedingMethod) {
        super.onMethodChange(selectedBreedingMethod);
        this.showLinesSelection = !selectedBreedingMethod.isBulkingMethod;
        this.showBulkingSelection = selectedBreedingMethod.isBulkingMethod;
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

    preview(forceReload = false): void {
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
                (res: AdvancedGermplasmPreview[]) => this.onSuccess(res, forceReload),
                (res) => this.onError(res));
    }
}
