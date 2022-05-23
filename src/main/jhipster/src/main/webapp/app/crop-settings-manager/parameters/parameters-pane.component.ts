import { Component } from '@angular/core';
import { ObservationVariable } from '../../shared/model/observation-variable.model';
import { DataTypeIdEnum } from '../../shared/ontology/data-type.enum';
import { TranslateService } from '@ngx-translate/core';
import { CropParameter } from '../../shared/crop-parameter/model/crop-parameter';
import { CropParameterService } from '../../shared/crop-parameter/service/crop-parameter.service';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { AlertService } from '../../shared/alert/alert.service';

@Component({
    selector: 'jhi-parameters-pane',
    templateUrl: 'parameters-pane.component.html'
})
export class ParametersPaneComponent {

    tableTooltip;

    cropParameters: CropParameter[];

    // Only text edition supported for now
    characterVariable: ObservationVariable = <ObservationVariable>({
        dataTypeId: DataTypeIdEnum.CHARACTER
    });
    editing = {};

    constructor(
        private cropParameterService: CropParameterService,
        private translateService: TranslateService,
        private alertService: AlertService
    ) {
        this.load();
        this.tableTooltip = this.translateService.instant('crop-settings-manager.parameters.table.tooltip');
    }

    load() {
        this.cropParameterService.getCropParameters().subscribe((cropParameters) => this.cropParameters = cropParameters);
    }

    submit($event, index, cropParameter) {
        this.cropParameterService.modifyCropParameters(cropParameter.key, $event).subscribe(() => {
            cropParameter.value = $event;
            this.editing[index] = false;
        }, (error) => this.onError(error));
    }

    cancel(index, cropParameter) {
        cropParameter.value = String(cropParameter.value);
        this.editing[index] = false;
    }

    isEditing() {
        return false;
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general', null, null);
        }
    }
}