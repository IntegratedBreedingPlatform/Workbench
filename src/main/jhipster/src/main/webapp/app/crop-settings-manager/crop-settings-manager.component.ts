import { Component, OnInit } from '@angular/core';
import { ParamContext } from '../shared/service/param.context';
import { HelpService } from '../shared/service/help.service';
import { ListBuilderContext } from '../shared/list-builder/list-builder.context';
import { HELP_MANAGE_GERMPLASM } from '../app.constants';

@Component({
    selector: 'jhi-crop-settings-manager',
    templateUrl: './crop-settings-manager.component.html'
})
export class CropSettingsManagerComponent implements OnInit {

    constructor(private paramContext: ParamContext) {
        this.paramContext.readParams();
    }

    ngOnInit() {
    }
}
