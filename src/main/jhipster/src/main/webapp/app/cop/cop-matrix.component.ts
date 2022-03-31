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
import { Subscription, timer } from 'rxjs';
import { switchMap, takeWhile, tap } from 'rxjs/operators';
import { COP_ASYNC_PROGRESS_REFRESH_MILLIS } from '../app.constants';
import { saveFile } from '../shared/util/file-utils';

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
    listId: number;

    private timer: Subscription;

    constructor(
        public activeModal: NgbActiveModal,
        private route: ActivatedRoute,
        private copService: CopService,
        private alertService: AlertService,
        private translateService: TranslateService
    ) {
        const queryParamMap = this.route.snapshot.queryParamMap;
        if (queryParamMap.get('gids')) {
            this.gids =  queryParamMap.get('gids').split(',').map((g) => Number(g));
        }
        this.listId = Number(queryParamMap.get('listIdModalParam'));
        this.calculate = queryParamMap.get('calculate') === 'true';

        this.isLoading = true;
        let copObservable;
        if (this.listId && this.calculate) {
            copObservable = this.copService.calculateCopForList(this.listId);
        } else if (this.calculate) {
            copObservable = this.copService.calculateCop(this.gids);
        } else {
            copObservable = this.copService.getCop(this.gids, this.listId);
        }
        copObservable.pipe(
            finalize(() => this.isLoading = false)
        ).subscribe((resp: CopResponse) => {
            if ((this.calculate) && !this.hasResponse(resp)) {
                this.alertService.success('cop.async.started');
            }
            this.response = resp;
            if (this.isResponseBlob(resp)) {
                this.download();
                return;
            }
            this.watchProgress();
        }, (error) => this.onError(error));

        this.cancelTooltip = this.translateService.instant('cop.async.cancel.tooltip');
    }

    private download() {
        this.copService.download(this.listId).subscribe((resp) => {
            saveFile(resp);
            this.close();
        });
    }

    private hasResponse(resp) {
        return resp && (resp.array || this.isResponseBlob(resp));
    }

    private isResponseBlob(resp: CopResponse) {
        return resp.hasFile;
    }

    private watchProgress() {
        // TODO increasing intervals? (5s, 10, 15, 15...)
        this.timer = timer(0, COP_ASYNC_PROGRESS_REFRESH_MILLIS).pipe(
            switchMap(() => this.copService.getCop(this.gids, this.listId)),
            tap((resp) => {
                this.response = resp
                if (this.isResponseBlob(resp)) {
                    this.download()
                }
            }),
            takeWhile((resp: any) => !this.hasResponse(resp))
        ).subscribe(() => void 0, (error) => this.onError(error));
    }

    cancelJobs() {
        this.isLoading = true;
        this.copService.cancelJobs(this.gids, this.listId).pipe(
            finalize(() => this.isLoading = false)
        ).subscribe(
            () => {
                this.response = null;
                this.timer.unsubscribe();
                this.alertService.success('cop.async.cancel.success');
                this.close();
            },
            (error) => this.onError(error)
        );
    }

    close() {
        this.activeModal.close();
        this.timer.unsubscribe();
    }

    size(o) {
        return Object.keys(o);
    }

    keys(o) {
        return Object.keys(o);
    }

    private onError(response: HttpErrorResponse) {
        if (!response.error) {
            this.alertService.error('error.general.client');
            console.error(response);
            return;
        }
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
