import { Component, Inject, Input, OnInit, Renderer2 } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DOCUMENT } from '@angular/common';
import { ParamContext } from '../../shared/service/param.context';
import { FEEDBACK_EMBED_SURVEY_ID } from '../../app.constants';
import { FeedbackFeatureEnum } from './feedback-feature.enum';
import { FeedbackService } from '../../shared/feedback/service/feedback.service';
import { finalize } from 'rxjs/operators';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { AlertService } from '../../shared/alert/alert.service';

@Component({
    selector: 'jhi-feedback-dialog-component',
    templateUrl: './feedback-dialog.component.html'
})
export class FeedbackDialogComponent implements OnInit {

    @Input() feature: FeedbackFeatureEnum;

    showSurvey: boolean;
    dontShowAgain: boolean;

    FEEDBACK_EMBED_SURVEY_ID = FEEDBACK_EMBED_SURVEY_ID;

    constructor(public context: ParamContext,
                private activeModal: NgbActiveModal,
                private feedbackService: FeedbackService,
                private alertService: AlertService,
                private renderer2: Renderer2,
                @Inject(DOCUMENT) private _document) {
        this.showSurvey = false;
    }

    ngOnInit(): void {
        this.feedbackService.shouldShowFeedback(this.feature).subscribe(
            (res: HttpResponse<boolean>) => this.shouldOpenSurvey(res.body),
            (res: HttpErrorResponse) => this.onError(res)
        );
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

    private shouldOpenSurvey(shouldOpen: boolean) {
        if (shouldOpen) {
            this.showSurvey = true;
            this.addSurveyScript();
        } else {
            this.activeModal.dismiss();
        }
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
    }

    private addSurveyScript() {
        const s = this.renderer2.createElement('script');
        s.type = 'text/javascript';
        s.src = `https://embed-cdn.surveyhero.com/js/user/embed.${FEEDBACK_EMBED_SURVEY_ID}.js`;
        s.text = ``;
        s.async = true;
        this.renderer2.appendChild(this._document.body, s);
    }

}
