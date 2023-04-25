import { Component, Input, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { formatErrorList } from '../shared/alert/format-error-list';
import { AlertService } from '../shared/alert/alert.service';
import { DatasetService } from '../shared/dataset/service/dataset.service';
import { ObservationVariable } from '../shared/model/observation-variable.model';
import { ObservationUnitsSearchResponse } from '../shared/dataset/model/observation-units-search-response.model';
import { ObservationUnitsSearchRequest, ObservationVariableDTO } from '../shared/dataset/model/observation-units-search-request.model';
import { VariableTypeEnum } from '../shared/ontology/variable-type.enum';
import { DatasetTypeEnum } from '../shared/dataset/model/dataset-type.enum';
import { ObservationUnitData } from '../shared/dataset/model/observation-unit-data.model';
import { ObservationVariableHelperService } from '../shared/dataset/model/observation-variable.helper.service';
import { finalize } from 'rxjs/internal/operators/finalize';
import { ActivatedRoute, Router } from '@angular/router';
import { isObservationOrSubObservationDataset } from '../shared/dataset/model/dataset.util';
import { MANAGE_STUDIES_PERMISSIONS } from '../shared/auth/permissions';
import { DatasetModel } from '../shared/dataset/model/dataset.model';
import { FileDownloadHelper } from '../entities/sample/file-download.helper';

@Component({
    selector: 'jhi-study-summary-dataset',
    templateUrl: './study-summary-dataset.component.html',
    styleUrls: [
        './study-summary-dataset.component.scss'
    ]
})
export class StudySummaryDatasetComponent implements OnInit {

    static readonly TRIAL_INSTANCE_TERM_ID = 8170;
    static readonly LOCATION_ID_TERM_ID = 8190;
    static readonly GID_TERM_ID = 8240;
    static readonly DESIGNATION_TERM_ID = 8250;
    static readonly SAMPLES = -2;

    GID_TERM_ID = StudySummaryDatasetComponent.GID_TERM_ID;
    DESIGNATION_TERM_ID = StudySummaryDatasetComponent.DESIGNATION_TERM_ID;

    MANAGE_STUDIES_PERMISSIONS = [...MANAGE_STUDIES_PERMISSIONS];

    isObservationOrSubObservationDataset = isObservationOrSubObservationDataset;

    itemsPerPage = 20;

    @Input()
    studyId: number;

    @Input()
    datasetId: number;

    @Input()
    datasetType: DatasetTypeEnum;

    dataset: DatasetModel;

    header: ObservationVariable[];
    observations: ObservationUnitsSearchResponse[];

    totalItems: number;
    page: number;
    previousPage: number;
    isLoading: boolean;

    constructor(public alertService: AlertService,
                public translateService: TranslateService,
                public datasetService: DatasetService,
                public observationVariableHelperService: ObservationVariableHelperService,
                public router: Router,
                public activatedRoute: ActivatedRoute,
                private fileDownloadHelper: FileDownloadHelper) {
        this.page = 1;
        this.totalItems = 0;
    }

    ngOnInit(): void {
        this.isLoading = true;
        this.datasetService.getObservationSetColumns(this.studyId, this.datasetId).subscribe(
            (res: HttpResponse<ObservationVariable[]>) => this.onGetObservationSetColumnsSuccess(res.body),
            (res: HttpErrorResponse) => this.onError(res)
        );
        this.datasetService.getDataset(this.studyId, this.datasetId).subscribe(
            (res: HttpResponse<DatasetModel>) => this.dataset = res.body,
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    trackId(index: number, item: ObservationUnitsSearchResponse) {
        return item.observationUnitId;
    }

    getObservationByVariable(variable: ObservationVariable, index: number): any {
        if (variable.termId === StudySummaryDatasetComponent.LOCATION_ID_TERM_ID || variable.termId === StudySummaryDatasetComponent.TRIAL_INSTANCE_TERM_ID) {
            return this.observations[index].variables[variable.name].value;
        }

        if (variable.termId === StudySummaryDatasetComponent.SAMPLES || variable.name === 'SUM_OF_SAMPLES') {
            return this.observations[index].samplesCount;
        }

        let observations: Map<string, ObservationUnitData>;
        if (this.isEnvironmentDataset()) {
            observations = this.observations[index].environmentVariables;
        } else {
            observations = this.observations[index].variables;
        }

        const observationUnitData: ObservationUnitData = observations[variable.name];
        return this.observationVariableHelperService.getVariableValueFromUnitData(variable, observationUnitData);
    }

    loadPage(page: number) {
        if (page !== this.previousPage) {
            this.previousPage = page;
            this.transition();
        }
    }

    transition() {
        this.router.navigate([`./study/${this.studyId}/summary/dataset/${this.datasetId}`], {
            queryParamsHandling: 'merge',
            queryParams: {
                studyId: this.studyId,
                datasetId: this.datasetId,
                sort: this.getSort(),
                page: this.page,
                size: this.itemsPerPage,
            },
            relativeTo: this.activatedRoute
        });
        this.loadAll();
    }

    loadAll() {
        this.isLoading = true;

        const request: ObservationUnitsSearchRequest = new ObservationUnitsSearchRequest();
        if (this.isEnvironmentDataset()) {
            const environmentConditions: ObservationVariable[] = this.getEnvironmentConditionsVariables(this.header);
            const environmentDetails: ObservationVariable[] = this.getEnvironmentDetailsVariables(this.header);
            request.environmentConditions = this.transformObservationVariableToDTO(environmentConditions);
            request.environmentDetails = this.transformObservationVariableToDTO(environmentDetails);
            request.environmentDatasetId = this.datasetId;
        }

        const pagination: any = {
            page: this.page - 1,
            size: this.itemsPerPage,
            sort: this.getSort(),
        }
        this.datasetService.getObservationUnitTable(this.studyId, this.datasetId, request, pagination)
            .pipe(finalize(() => {
                this.isLoading = false;
            }))
            .subscribe(
                (observationsResponse: HttpResponse<ObservationUnitsSearchResponse[]>) => this.onGetObservationUnitTableSuccess(observationsResponse),
                (observationsResponse: HttpErrorResponse) => this.onError(observationsResponse)
            );
    }

    resetTable() {
        this.page = 1;
        this.previousPage = 1;
        this.loadAll();
    }

    getTraits(): ObservationVariable[] {
        if (!this.header) {
            return [];
        }

        return this.filterVariables(this.header, VariableTypeEnum.TRAIT);
    }

    exportDataset() {
        const instanceIds = [];
        this.dataset.instances.forEach(instance => instanceIds.push(instance.instanceId));
        this.datasetService.exportDataset(this.studyId, this.datasetId, instanceIds).pipe(finalize(() => {
            this.isLoading = false;
        })).subscribe((response: any) => {
            const fileName = this.fileDownloadHelper.getFileNameFromResponseContentDisposition(response);
            this.fileDownloadHelper.save(response.body, fileName);

        });
    }

    private getSort() {
        return ['8170, asc'];
    }

    private onGetObservationSetColumnsSuccess(header: ObservationVariable[]) {
        if (this.isEnvironmentDataset()) {
            const environmentConditions: ObservationVariable[] = this.getEnvironmentConditionsVariables(header);
            const environmentDetails: ObservationVariable[] = this.getEnvironmentDetailsVariables(header);
            const otherVariables: ObservationVariable[] =
                header.filter((variable: ObservationVariable) => variable.variableType &&
                    (variable.variableType.toString() !== VariableTypeEnum[VariableTypeEnum.ENVIRONMENT_CONDITION] &&
                        variable.variableType.toString() !== VariableTypeEnum[VariableTypeEnum.ENVIRONMENT_DETAIL]));

            this.header = environmentDetails.concat(environmentConditions).concat(otherVariables);
        } else {
            this.header = header;
        }
        this.loadAll();
    }

    private onGetObservationUnitTableSuccess(response: HttpResponse<ObservationUnitsSearchResponse[]>) {
        this.totalItems = Number(response.headers.get('X-Total-Count'));
        this.observations = response.body;
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
    }

    private transformObservationVariableToDTO(variables: ObservationVariable[]): ObservationVariableDTO[] {
        return variables.map((variable: ObservationVariable) => new ObservationVariableDTO(variable.termId, variable.name));
    }

    private isEnvironmentDataset(): boolean {
        return this.datasetType === DatasetTypeEnum.ENVIRONMENT;
    }

    private getEnvironmentConditionsVariables(header: ObservationVariable[]): ObservationVariable[] {
        return this.filterVariables(header, VariableTypeEnum.ENVIRONMENT_CONDITION);
    }

    private getEnvironmentDetailsVariables(header: ObservationVariable[]): ObservationVariable[] {
        return this.filterVariables(header, VariableTypeEnum.ENVIRONMENT_DETAIL);
    }

    private filterVariables(header: ObservationVariable[], variableType: VariableTypeEnum): ObservationVariable[] {
        return header.filter((variable: ObservationVariable) => variable.variableType && variable.variableType.toString() === VariableTypeEnum[variableType]);
    }

}
