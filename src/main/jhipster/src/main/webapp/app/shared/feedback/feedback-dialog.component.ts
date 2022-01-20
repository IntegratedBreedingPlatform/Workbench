import { Component, Inject, Input, OnInit, Renderer2 } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DOCUMENT } from '@angular/common';
import { ParamContext } from '../service/param.context';
import { FEEDBACK_SURVEY_ID } from '../../app.constants';
import { FeedbackFeatureEnum } from './feedback-feature.enum';
import { FeedbackService } from './service/feedback.service';
import { finalize } from 'rxjs/operators';
import { AlertService } from '../alert/alert.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'jhi-feedback-dialog-component',
    templateUrl: './feedback-dialog.component.html',
    styleUrls: ['./feedback-dialog.component.scss']
})
export class FeedbackDialogComponent implements OnInit {

    @Input() feature: FeedbackFeatureEnum;

    ratingText: string;
    dontShowAgain: boolean;

    FEEDBACK_SURVEY_ID = FEEDBACK_SURVEY_ID;

    constructor(public context: ParamContext,
                private activeModal: NgbActiveModal,
                private feedbackService: FeedbackService,
                private alertService: AlertService,
                private translateService: TranslateService,
                private renderer2: Renderer2,
                @Inject(DOCUMENT) private _document) {
    }

    ngOnInit(): void {
        this.ratingText = this.translateService.instant(`feedback.rating-text.${this.feature}`);

        const s = this.renderer2.createElement('script');
        s.type = 'text/javascript';
        s.src = `https://embed-cdn.surveyhero.com/js/user/embed.${FEEDBACK_SURVEY_ID}.js`;
        s.text = ``;
        s.async = true;
        this.renderer2.appendChild(this._document.body, s);
    }

    closeModal() {
        this.activeModal.dismiss();
    }

    dismiss() {
        if (this.dontShowAgain) {
            this.feedbackService.dontShowAgain(this.feature).pipe(finalize(() => {
                this.closeModal();
            })).subscribe(() => {});
            return;
        } else {
            this.closeModal();
        }
    }

}
