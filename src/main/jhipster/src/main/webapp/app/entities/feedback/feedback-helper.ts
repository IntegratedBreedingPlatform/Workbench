import { FeedbackFeatureEnum } from './feedback-feature.enum';
import { FeedbackDialogComponent } from './feedback-dialog.component';
import { Component } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FEEDBACK_ENABLED } from '../../app.constants';
import { FeedbackService } from '../../shared/feedback/service/feedback.service';
import { HttpResponse } from '@angular/common/http';

export function openSurvey(feature: FeedbackFeatureEnum, feedbackService: FeedbackService, modalService: NgbModal) {
    if (FEEDBACK_ENABLED) {
        feedbackService.shouldShowFeedback(feature).subscribe(
            (res: HttpResponse<boolean>) => {
                if (res.body) {
                    const feedbackModal = modalService.open(FeedbackDialogComponent as Component, { size: 'lg', backdrop: 'static' });
                    feedbackModal.componentInstance.feature = feature;
                }
            }
        );
    }
}
