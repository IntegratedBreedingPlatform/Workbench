import { Component, Input, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { StudyService } from '../shared/study/service/study.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { StudyDetails } from '../shared/study/model/study-details.model';
import { formatErrorList } from '../shared/alert/format-error-list';
import { AlertService } from '../shared/alert/alert.service';
import { ObservationVariable, ValueReference } from '../shared/model/observation-variable.model';
import { DataTypeEnum } from '../shared/ontology/data-type.enum';

@Component({
    selector: 'jhi-study-summary',
    templateUrl: './study-summary.component.html',
})
export class StudySummaryComponent implements OnInit {

    @Input()
    studyId: number;

    studyDetails: StudyDetails;

    constructor(private studyService: StudyService,
                private alertService: AlertService,
                private translateService: TranslateService) {
    }

    ngOnInit(): void {
        this.studyService.getStudyDetails(this.studyId).subscribe(
            (res: HttpResponse<StudyDetails>) => this.studyDetails = res.body,
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

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
    }

}
