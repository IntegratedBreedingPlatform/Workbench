import { FeedbackFeatureEnum } from './feedback-feature.enum';
import { FeedbackDialogComponent } from './feedback-dialog.component';
import { Component } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FEEDBACK_SHOW } from '../../app.constants';

export function openSurvey(modalService: NgbModal, feature: FeedbackFeatureEnum) {
    if (FEEDBACK_SHOW) {
        const feedbackModal = modalService.open(FeedbackDialogComponent as Component);
        feedbackModal.componentInstance.feature = feature;
    }
}
