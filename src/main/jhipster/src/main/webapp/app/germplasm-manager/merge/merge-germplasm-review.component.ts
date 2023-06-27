import { Component, Input, OnInit } from '@angular/core';
import { GermplasmMergeRequest } from '../../shared/germplasm/model/germplasm-merge-request.model';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { AlertService } from '../../shared/alert/alert.service';
import { JhiEventManager } from 'ng-jhipster';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { GermplasmMergeSummary } from '../../shared/germplasm/model/germplasm-merge-summary.model';
import { GermplasmSearchRequest } from '../../entities/germplasm/germplasm-search-request.model';
import { finalize } from 'rxjs/internal/operators/finalize';
import { Germplasm } from '../../entities/germplasm/germplasm.model';

@Component({
    selector: 'jhi-merge-germplasm-review',
    templateUrl: './merge-germplasm-review.component.html',
})
export class MergeGermplasmReviewComponent implements OnInit {
    @Input()
    germplasmMergeRequest: GermplasmMergeRequest;

    isLoading: boolean;
    germplasmMergeSummary: GermplasmMergeSummary;

    constructor(
        private germplasmService: GermplasmService,
        private alertService: AlertService,
        private modal: NgbActiveModal,
        private eventManager: JhiEventManager) {
    }

    ngOnInit(): void {
        this.isLoading = true;
        this.germplasmService.reviewGermplasmMerge(this.germplasmMergeRequest).toPromise().then((summary: any) => {
            this.germplasmMergeSummary = summary;
            this.isLoading = false;
        }, (error) => {
            this.onError(error)
        });
    }

    dismiss() {
        this.modal.dismiss();
    }

    confirm() {
        this.germplasmService.mergeGermplasm(this.germplasmMergeRequest).toPromise()
            .then(() => {
                this.alertService.success('merge-germplasm.success')
                this.modal.dismiss();
                // Refresh the Germplasm Manager search germplasm table to reflect the changes made in germplasm.
                this.eventManager.broadcast({ name: 'germplasmDetailsChanged', content: '' });
            }, (error) => {
                this.onError(error)
            });
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
        this.isLoading = false;
    }
}
