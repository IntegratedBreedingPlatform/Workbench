import { Component } from '@angular/core';
import { ParamContext } from '../shared/service/param.context';
import { JhiLanguageService } from 'ng-jhipster';
import { HELP_MANAGE_PROGRAM_SETTINGS } from '../app.constants';
import { HelpService } from '../shared/service/help.service';
import { FeedbackService } from '../shared/feedback/service/feedback.service';
import { openSurvey } from '../shared/feedback/feedback-helper';
import { FeedbackFeatureEnum } from '../shared/feedback/feedback-feature.enum';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-program-settings-manager',
    templateUrl: './program-settings-manager.component.html'
})
export class ProgramSettingsManagerComponent {

    helpLink: string;

    constructor(
        private jhiLanguageService: JhiLanguageService,
        private helpService: HelpService,
        private paramContext: ParamContext,
        private modalService: NgbModal,
        private feedbackService: FeedbackService
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
        openSurvey(FeedbackFeatureEnum.MANAGE_PROGRAM_SETTINGS, this.feedbackService, this.modalService);
    }
}
