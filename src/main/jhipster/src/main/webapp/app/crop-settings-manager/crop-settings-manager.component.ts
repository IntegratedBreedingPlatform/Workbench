import { Component, OnInit } from '@angular/core';
import { ParamContext } from '../shared/service/param.context';
import { HelpService } from '../shared/service/help.service';
import { HELP_MANAGE_CROP_SETTINGS } from '../app.constants';

@Component({
    selector: 'jhi-crop-settings-manager',
    templateUrl: './crop-settings-manager.component.html'
})
export class CropSettingsManagerComponent implements OnInit {

    helpLink: string;

    constructor(private paramContext: ParamContext,
                private helpService: HelpService) {
        this.paramContext.readParams();
        if (!this.helpLink || !this.helpLink.length) {
            this.helpService.getHelpLink(HELP_MANAGE_CROP_SETTINGS).toPromise().then((response) => {
                if (response.body) {
                    this.helpLink = response.body;
                }
            }).catch((error) => {
            });
        }
    }

    ngOnInit() {
    }
}
