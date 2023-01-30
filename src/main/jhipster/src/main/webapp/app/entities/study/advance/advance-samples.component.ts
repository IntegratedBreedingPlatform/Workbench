import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { ParamContext } from '../../../shared/service/param.context';
import { AlertService } from '../../../shared/alert/alert.service';
import { HelpService } from '../../../shared/service/help.service';
import { DatasetService } from '../../../shared/dataset/service/dataset.service';
import { BreedingMethodService } from '../../../shared/breeding-method/service/breeding-method.service';
import { TranslateService } from '@ngx-translate/core';
import { AdvanceService } from '../../../shared/study/service/advance.service';
import { finalize } from 'rxjs/internal/operators/finalize';
import { AbstractAdvanceComponent, AdvanceType } from './abstract-advance.component';
import { AdvanceSamplesRequest } from '../../../shared/study/model/advance-sample-request.model';
import { SelectionTraitRequest } from '../../../shared/study/model/abstract-advance-request.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-advance-samples',
    templateUrl: './advance-samples.component.html'
})
export class AdvanceSamplesComponent extends AbstractAdvanceComponent {

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
        super(paramContext, route, breedingMethodService, helpService, datasetService, translateService, alertService, modalService, AdvanceType.SAMPLES);
    }

    save(): void {
        this.isLoading = true;

        const selectedInstanceIds: number[] = this.trialInstances.map((instance) => instance.instanceId);
        const selectedReplicationNumbers: number[] =
            this.replicationsOptions.filter((replication: any) => replication.selected)
                .map((replication: any) => replication.index);

        const advanceSamplesRequest: AdvanceSamplesRequest = new AdvanceSamplesRequest(selectedInstanceIds, selectedReplicationNumbers, Number(this.breedingMethodSelectedId));
        if (this.showSelectionTraitSelection) {
            const selectionTraitRequest: SelectionTraitRequest = new SelectionTraitRequest(this.selectedSelectionTraitDatasetId, this.selectedSelectionTraitVariableId);
            advanceSamplesRequest.selectionTraitRequest = selectionTraitRequest;
        }

        this.advanceService.advanceSamples(this.studyId, advanceSamplesRequest)
            .pipe(finalize(() => this.isLoading = false))
            .subscribe(
            (res: number[]) => this.onAdvanceSuccess(res),
            (res) => this.onError(res));
    }

    isValid(): boolean {
        // Breeding method was not selected
        if (!this.breedingMethodSelectedId) {
            return false;
        }

        // Selection trait was no selected
        if (this.showSelectionTraitSelection && (!this.selectedSelectionTraitDatasetId || !this.selectedSelectionTraitVariableId)) {
            return false;
        }

        // Replications were not selected
        if (this.replicationsOptions.length && this.replicationsOptions.filter((rep: any) => rep.selected).length === 0 && !this.checkAllReplications) {
            return false;
        }

        return true;
    }

}
