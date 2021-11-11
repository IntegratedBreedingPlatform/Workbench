import { Component, Inject, Input, OnInit, Renderer2 } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DOCUMENT } from '@angular/common';
import { ParamContext } from '../../shared/service/param.context';
import { FEEDBACK_EMBED_SURVEY_ID } from '../../app.constants';
import { FeedbackFeatureEnum } from './feedback-feature.enum';
import { FeedbackService } from '../../shared/feedback/service/feedback.service';
import { finalize } from 'rxjs/operators';
import { AlertService } from '../../shared/alert/alert.service';

@Component({
    selector: 'jhi-feedback-dialog-component',
    templateUrl: './feedback-dialog.component.html'
})
export class FeedbackDialogComponent implements OnInit {

    @Input() feature: FeedbackFeatureEnum;

    dontShowAgain: boolean;

    FEEDBACK_EMBED_SURVEY_ID = FEEDBACK_EMBED_SURVEY_ID;

    constructor(public context: ParamContext,
                private activeModal: NgbActiveModal,
                private feedbackService: FeedbackService,
                private alertService: AlertService,
                private renderer2: Renderer2,
                @Inject(DOCUMENT) private _document) {
    }

    ngOnInit(): void {
        const s = this.renderer2.createElement('script');
        s.type = 'text/javascript';
        s.src = `https://embed-cdn.surveyhero.com/js/user/embed.${FEEDBACK_EMBED_SURVEY_ID}.js`;
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
