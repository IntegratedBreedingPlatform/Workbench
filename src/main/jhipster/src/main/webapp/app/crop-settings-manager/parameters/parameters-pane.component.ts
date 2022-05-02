import { Component } from '@angular/core';
import { Config } from '../../shared/config/model/config';
import { ConfigService } from '../../shared/config/service/config.service';
import { ObservationVariable } from '../../shared/model/observation-variable.model';
import { DataTypeIdEnum } from '../../shared/ontology/data-type.enum';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'jhi-parameters-pane',
    templateUrl: 'parameters-pane.component.html'
})
export class ParametersPaneComponent {

    tableTooltip;

    config: Config[];

    // Only text edition supported for now
    characterVariable: ObservationVariable = <ObservationVariable>({
        dataTypeId: DataTypeIdEnum.CHARACTER
    });
    editing = {};

    constructor(
        private configService: ConfigService,
        private translateService: TranslateService
    ) {
        this.load();
        this.tableTooltip = this.translateService.instant('crop-settings-manager.parameters.table.tooltip');
    }

    load() {
        this.configService.getConfig().subscribe((config) => this.config = config);
    }

    submit($event, index, c) {
        this.configService.modifyConfig(c.key, $event).subscribe(() => {
            c.value = $event;
            this.editing[index] = false;
        });
    }

    cancel(index, c) {
        c.value = String(c.value);
        this.editing[index] = false;
    }

    isEditing() {
        return false;
    }
}
