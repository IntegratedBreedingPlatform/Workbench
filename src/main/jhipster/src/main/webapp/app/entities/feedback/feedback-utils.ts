import { FeedbackFeatureEnum } from './feedback-feature.enum';
import { FeedbackDialogComponent } from './feedback-dialog.component';
import { Component } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

export function openSurvey(modalService: NgbModal, feature: FeedbackFeatureEnum) {
    const feedbackModal = modalService.open(FeedbackDialogComponent as Component);
    feedbackModal.componentInstance.feature = feature;
}
