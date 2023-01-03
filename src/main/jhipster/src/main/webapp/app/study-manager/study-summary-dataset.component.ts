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

    itemsPerPage = 20;

    @Input()
    studyId: number;

    @Input()
    datasetId: number;

    @Input()
    datasetType: DatasetTypeEnum;

    header: ObservationVariable[];
    observations: ObservationUnitsSearchResponse[];

    page: number;

    constructor(public alertService: AlertService,
                public translateService: TranslateService,
                public datasetService: DatasetService,
                public observationVariableHelperService: ObservationVariableHelperService) {
        this.page = 1;
    }

    ngOnInit(): void {
        this.datasetService.getObservationSetColumns(this.studyId, this.datasetId).subscribe(
            (res: HttpResponse<ObservationVariable[]>) => this.onGetObservationSetColumnsSuccess(res.body),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    trackId(index: number, item: ObservationUnitsSearchResponse) {
        return item.observationUnitId;
    }

    getObservationByVariable(variable: ObservationVariable, index: number): any {
        let observations: Map<string, ObservationUnitData>;
        if (this.isEnvironmentDataset()) {
            if (variable.termId === StudySummaryDatasetComponent.LOCATION_ID_TERM_ID || variable.termId === StudySummaryDatasetComponent.TRIAL_INSTANCE_TERM_ID) {
                return this.observations[index].variables[variable.name].value;
            }
            observations = this.observations[index].environmentVariables;
        } else {
            observations = this.observations[index].variables;
        }

        const observationUnitData: ObservationUnitData = observations[variable.name];
        return this.observationVariableHelperService.getVariableValueFromUnitData(variable, observationUnitData);
    }

    isFactor(variable: ObservationVariable): boolean {
        return variable.variableType.toString() === VariableTypeEnum[VariableTypeEnum.ENVIRONMENT_DETAIL];
    }

    private onGetObservationSetColumnsSuccess(header: ObservationVariable[]) {
        const request: ObservationUnitsSearchRequest = new ObservationUnitsSearchRequest();
        if (this.isEnvironmentDataset()) {
            const environmentConditions: ObservationVariable[] = [];
            const environmentDetails: ObservationVariable[] = [];
            const otherVariables: ObservationVariable[] = [];
            header.forEach((variable: ObservationVariable) => {
                if (variable.variableType) {
                    if (variable.variableType.toString() === VariableTypeEnum[VariableTypeEnum.ENVIRONMENT_CONDITION]) {
                        environmentConditions.push(variable);
                    } else if (variable.variableType.toString() === VariableTypeEnum[VariableTypeEnum.ENVIRONMENT_DETAIL]) {
                        environmentDetails.push(variable);
                    } else {
                        otherVariables.push(variable);
                    }
                }
            });

            this.header = environmentDetails.concat(environmentConditions).concat(otherVariables);
            request.environmentConditions = this.transformObservationVariableToDTO(environmentConditions);
            request.environmentDetails = this.transformObservationVariableToDTO(environmentDetails);
            request.environmentDatasetId = this.datasetId;
        } else {
            this.header = header;
        }
        const pagination: any = {
            page: this.page - 1,
            size: this.itemsPerPage
        }
        this.datasetService.getObservationUnitTable(this.studyId, this.datasetId, request, pagination).subscribe(
            (observationsResponse: HttpResponse<ObservationUnitsSearchResponse[]>) => this.observations = observationsResponse.body,
            (observationsResponse: HttpErrorResponse) => this.onError(observationsResponse)
        );
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

}
