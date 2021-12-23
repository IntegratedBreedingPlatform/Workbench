import { Component } from '@angular/core';
import { ParamContext } from '../shared/service/param.context';
import { JhiLanguageService } from 'ng-jhipster';

@Component({
    selector: 'jhi-program-settings-manager',
    templateUrl: './program-settings-manager.component.html'
})
export class ProgramSettingsManagerComponent {
    constructor(
        private jhiLanguageService: JhiLanguageService,
        private paramContext: ParamContext
    ) {
        this.paramContext.readParams();
    }
}
