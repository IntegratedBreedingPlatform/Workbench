import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../shared/modal/popup.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CopService } from './cop.service';
import { finalize } from 'rxjs/internal/operators/finalize';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../shared/alert/format-error-list';
import { AlertService } from '../shared/alert/alert.service';
import { CopResponse } from './cop.model';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'jhi-cop-matrix',
    templateUrl: './cop-matrix.component.html'
})
export class CopMatrixComponent {
    isLoading = false;
    calculate: boolean;
    response: CopResponse;
    cancelTooltip;
    gids: number[];

    constructor(
        public activeModal: NgbActiveModal,
        private route: ActivatedRoute,
        private copService: CopService,
        private alertService: AlertService,
        private translateService: TranslateService
    ) {
        const queryParamMap = this.route.snapshot.queryParamMap;
        this.gids = queryParamMap.get('gids').split(',').map((g) => Number(g));
        this.calculate = queryParamMap.get('calculate') === 'true';

        this.isLoading = true;
        const copObservable = this.calculate
            ? this.copService.calculateCop(this.gids)
            : this.copService.getCop(this.gids);
        copObservable.pipe(
            finalize(() => this.isLoading = false)
        ).subscribe((resp) => {
            this.response = resp;
        }, (error) => this.onError(error));

        this.cancelTooltip = this.translateService.instant('cop.async.cancel.tooltip');
    }

    cancelJobs() {
        this.isLoading = true;
        this.copService.cancelJobs(this.gids).pipe(
            finalize(() => this.isLoading = false)
        ).subscribe(
            () => {
                this.response = null;
                this.alertService.success('cop.async.cancel.success');
            },
            (error) => this.onError(error)
        );
    }

    close() {
        this.activeModal.close();
    }

    size(o) {
        return Object.keys(o);
    }

    keys(o) {
        return Object.keys(o);
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general', null, null);
        }
    }
}

@Component({
    selector: 'jhi-cop-matrix-popup',
    template: ''
})
export class CopMatrixPopupComponent implements OnInit {

    constructor(
        private route: ActivatedRoute,
        private popupService: PopupService,
    ) {
    }

    ngOnInit(): void {
        this.popupService.open(CopMatrixComponent as Component);
    }
}
