import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { StudyService } from '../shared/study/service/study.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { StudyDetails } from '../shared/study/model/study-details.model';
import { formatErrorList } from '../shared/alert/format-error-list';
import { AlertService } from '../shared/alert/alert.service';
import { UrlService } from '../shared/service/url.service';
import { DatasetService } from '../shared/dataset/service/dataset.service';
import { DatasetModel } from '../shared/dataset/model/dataset.model';
import { toUpper } from '../shared/util/to-upper';
import { NavTab } from '../shared/nav/tab/nav-tab.model';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { DatasetTypeEnum } from '../shared/dataset/model/dataset-type.enum';
import { ObservationVariableHelperService } from '../shared/dataset/model/observation-variable.helper.service';
import { MANAGE_STUDIES_VIEW_PERMISSIONS } from '../shared/auth/permissions';

@Component({
    selector: 'jhi-study-summary',
    templateUrl: './study-summary.component.html',
    encapsulation: ViewEncapsulation.None
})
export class StudySummaryComponent implements OnInit {

    static readonly SUPPORTED_DATASETS: DatasetTypeEnum[] = [
        DatasetTypeEnum.MEANS,
        DatasetTypeEnum.PLOT,
        DatasetTypeEnum.ENVIRONMENT,
        DatasetTypeEnum.PLANT_SUBOBSERVATIONS,
        DatasetTypeEnum.QUADRAT_SUBOBSERVATIONS,
        DatasetTypeEnum.TIME_SERIES_SUBOBSERVATIONS,
        DatasetTypeEnum.CUSTOM_SUBOBSERVATIONS,
        DatasetTypeEnum.SUMMARY_STATISTICS_DATA];

    MANAGE_STUDIES_VIEW_PERMISSIONS = MANAGE_STUDIES_VIEW_PERMISSIONS;

    @Input()
    studyId: number;

    studyDetails: StudyDetails;

    datasetId: number;
    selectedDatasetId: number;
    datasets: DatasetModel[];
    datasetTabs: NavTab[] = [];

    private queryParamSubscription: Subscription;

    constructor(public studyService: StudyService,
                public alertService: AlertService,
                public translateService: TranslateService,
                public urlService: UrlService,
                public datasetService: DatasetService,
                public router: Router,
                public activatedRoute: ActivatedRoute,
                public observationVariableHelperService: ObservationVariableHelperService) {
        this.queryParamSubscription = this.activatedRoute.queryParams.subscribe((params) => {
            const studyId = parseInt(params['studyId'], 10);
            const datasetId = parseInt(params['datasetId'], 10);
            if (studyId !== this.studyId || !datasetId) {
                return;
            }

            this.datasetId = datasetId;
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
            (res: HttpResponse<DatasetModel[]>) => this.onGetDatasetsSuccess(res.body),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    getPlotSize(): number {
        return this.studyDetails.numberOfPlots / this.studyDetails.numberOfEnvironments;
    }

    openStudy(study: StudyDetails) {
        this.urlService.openStudy(study.id, study.name);
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

        // Clear dropdown selection
        if (tab.id === this.selectedDatasetId) {
            this.selectedDatasetId = null;

            if (this.datasetTabs.length === 0) {
                this.router.navigate([], {
                    queryParams: {
                        studyId: null,
                        datasetId: null,
                        datasetName: null
                    },
                    queryParamsHandling: 'merge'
                })
            }
        }
    }

    trackId(index: number, item: NavTab) {
        return item.id;
    }

    getDatasetTypeByDatasetId(datasetId: number): DatasetTypeEnum {
        return this.datasets.filter((dataset: DatasetModel) => dataset.datasetId === datasetId).map((dataset: DatasetModel) => dataset.datasetTypeId)[0];
    }

    private onGetDatasetsSuccess(datasets: DatasetModel[]) {
        this.datasets = datasets.filter((dataset: DatasetModel) => StudySummaryComponent.SUPPORTED_DATASETS.includes(dataset.datasetTypeId));
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
