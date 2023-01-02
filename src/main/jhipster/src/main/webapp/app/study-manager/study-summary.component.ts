import { Component, Input, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { StudyService } from '../shared/study/service/study.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { StudyDetails } from '../shared/study/model/study-details.model';
import { formatErrorList } from '../shared/alert/format-error-list';
import { AlertService } from '../shared/alert/alert.service';
import { ObservationVariable, ValueReference } from '../shared/model/observation-variable.model';
import { DataTypeEnum } from '../shared/ontology/data-type.enum';
import { UrlService } from '../shared/service/url.service';
import { DatasetService } from '../shared/dataset/service/dataset.service';
import { DatasetModel } from '../shared/dataset/model/dataset.model';
import { toUpper } from '../shared/util/to-upper';
import { NavTab } from '../shared/nav/tab/nav-tab.model';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
    selector: 'jhi-study-summary',
    templateUrl: './study-summary.component.html',
})
export class StudySummaryComponent implements OnInit {

    @Input()
    studyId: number;

    studyDetails: StudyDetails;

    datasetId: number;
    selectedDatasetId: number;
    datasets: DatasetModel[];
    datasetTabs: NavTab[] = [];

    private queryParamSubscription: Subscription;

    constructor(private studyService: StudyService,
                private alertService: AlertService,
                private translateService: TranslateService,
                private urlService: UrlService,
                private datasetService: DatasetService,
                private router: Router,
                private activatedRoute: ActivatedRoute) {
        this.queryParamSubscription = this.activatedRoute.queryParams.subscribe((params) => {
            this.datasetId = parseInt(params['datasetId'], 10);

            if (!this.datasetId) {
                return;
            }

            if (!this.exists(this.datasetId)) {
                this.datasetTabs.push(new NavTab(this.datasetId, params['datasetName'], true));
            }

            this.setActive(this.datasetId);
        });
    }

    ngOnInit(): void {
        this.studyService.getStudyDetails(this.studyId).subscribe(
            (res: HttpResponse<StudyDetails>) => this.studyDetails = res.body,
            (res: HttpErrorResponse) => this.onError(res)
        );
        this.datasetService.getDatasets(this.studyId, null).subscribe(
            (res: HttpResponse<DatasetModel[]>) => this.datasets = res.body,
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    getVariableValue(variable: ObservationVariable) {
        if (variable.dataType === DataTypeEnum.CATEGORICAL && variable.possibleValues && variable.possibleValues.length > 0) {
            return variable.possibleValues.filter((valueReference: ValueReference) => String(valueReference.id) === variable.value)
                .map((value: ValueReference) => value.description);
        }
        return variable.value;
    }

    getVariableDisplayName(variable: ObservationVariable): string {
        return variable.alias ? variable.alias : variable.name;
    }

    getPlotSize(): number {
        return this.studyDetails.numberOfPlots / this.studyDetails.numberOfEnvironments;
    }

    openStudy(studyId: number) {
        this.urlService.openStudy(studyId);
    }

    searchDataset(term: string, item: DatasetModel) {
        const termUpper = toUpper(term);
        return toUpper(item.name).includes(termUpper);
    }

    onDatasetSelected() {
        this.navigateToDataset(this.selectedDatasetId);
    }

    setActive(datasetId: number) {
        this.datasetTabs.forEach((tab: NavTab) => {
            tab.active = (tab.id === datasetId);
        });
    }

    closeTab(tab: NavTab) {
        this.datasetTabs.splice(this.datasetTabs.indexOf(tab), 1);
        if (tab.active && this.datasetTabs.length > 0) {
            this.navigateToDataset(this.datasetTabs[0].id);
        }
    }

    trackId(index: number, item: NavTab) {
        return item.id;
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
    }

    private exists(datasetId: number) {
        return this.datasetTabs.some((tab: NavTab) => tab.id === datasetId);
    }

    private navigateToDataset(activeDatasetId: number) {
        const selectedDataset: DatasetModel = this.datasets.find((dataset: DatasetModel) => dataset.datasetId === activeDatasetId);
        this.router.navigate([`/study-manager/study/${this.studyId}/summary/dataset/${activeDatasetId}`], {
            queryParams: {
                studyId: this.studyId,
                datasetId: selectedDataset.datasetId,
                datasetName: selectedDataset.name
            }
        });
    }

}
