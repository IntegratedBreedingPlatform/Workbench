import { Component } from '@angular/core';
import { ParamContext } from '../shared/service/param.context';
import { JhiLanguageService } from 'ng-jhipster';
import { HELP_MANAGE_PROGRAM_SETTINGS } from '../app.constants';
import { HelpService } from '../shared/service/help.service';

@Component({
    selector: 'jhi-program-settings-manager',
    templateUrl: './program-settings-manager.component.html'
})
export class ProgramSettingsManagerComponent {

    helpLink: string;

    constructor(
        private jhiLanguageService: JhiLanguageService,
        private helpService: HelpService,
        private paramContext: ParamContext
    ) {
        this.paramContext.readParams();
        if (!this.helpLink || !this.helpLink.length) {
            this.helpService.getHelpLink(HELP_MANAGE_PROGRAM_SETTINGS).toPromise().then((response) => {
                if (response.body) {
                    this.helpLink = response.body;
                }
            }).catch((error) => {
            });
        }
    }
}
