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
import { COP_ASYNC_PROGRESS_REFRESH_MILLIS, HELP_GERMPLASM_LIST_COP_BETA } from '../app.constants';
import { saveFile } from '../shared/util/file-utils';
import { HelpService } from '../shared/service/help.service';

@Component({
    selector: 'jhi-cop-matrix',
    templateUrl: './cop-matrix.component.html'
})
export class CopMatrixComponent {
    isLoading = false;
    calculate: boolean;
    response: CopResponse;
    cancelTooltip;
    GID = 'GID';
    nameKeySelected = this.GID;
    helpLink: string;

    gids: number[];
    listId: number;

    private timer: Subscription;
    private reset: boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private route: ActivatedRoute,
        private copService: CopService,
        private alertService: AlertService,
        private translateService: TranslateService,
        private helpService: HelpService,
    ) {
        const queryParamMap = this.route.snapshot.queryParamMap;
        if (queryParamMap.get('gids')) {
            this.gids = queryParamMap.get('gids').split(',').map((g) => Number(g));
        }
        this.listId = Number(queryParamMap.get('listIdModalParam'));
        this.calculate = queryParamMap.get('calculate') === 'true';
        this.reset = queryParamMap.get('reset') === 'true';

        this.isLoading = true;
        let copObservable;
        if (this.listId && this.calculate) {
            copObservable = this.copService.calculateCopForList(this.listId);
        } else if (this.calculate) {
            copObservable = this.copService.calculateCop(this.gids, this.reset);
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
            if (this.hasResponseFile(resp)) {
                this.downloadForList();
                return;
            }
            this.watchProgress();
        }, (error) => this.onError(error));

        this.cancelTooltip = this.translateService.instant('cop.async.cancel.tooltip');

        this.helpService.getHelpLink(HELP_GERMPLASM_LIST_COP_BETA).subscribe((response) => {
            if (response.body) {
                this.helpLink = response.body;
            }
        });
    }

    private downloadForList() {
        this.copService.downloadForList(this.listId).subscribe((resp) => {
            saveFile(resp);
            this.close();
        }, (error) => this.onError(error));
    }

    downloadMatrix() {
        this.copService.downloadMatrix(this.gids, this.nameKeySelected).subscribe((resp) => {
            saveFile(resp);
        }, (error) => this.onError(error));
    }

    private hasResponse(resp: CopResponse) {
        return resp && (resp.upperTriangularMatrix || this.hasResponseFile(resp));
    }

    private hasResponseFile(resp: CopResponse) {
        return resp.hasFile;
    }

    hasResponseOnlyMatrix() {
        return this.response && (this.response.upperTriangularMatrix && !this.response.hasFile);
    }

    private watchProgress() {
        // TODO increasing intervals? (5s, 10, 15, 15...)
        this.timer = timer(0, COP_ASYNC_PROGRESS_REFRESH_MILLIS).pipe(
            switchMap(() => this.copService.getCop(this.gids, this.listId)),
            tap((resp) => {
                this.response = resp
                if (this.hasResponseFile(resp)) {
                    this.downloadForList()
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

    progressValue() {
        if (!this.response) {
            return 0;
        }
        if (this.response.hasFile) {
            return 100;
        }
        return this.response.progress;
    }

    progressLabel() {
        if (!this.response) {
            return '';
        }
        if (this.response.progress === 100) {
            return ' - ' + this.translateService.instant('cop.async.saving');
        }
        return this.response.hasFile
            ? (' - ' + this.translateService.instant('cop.async.downloading'))
            : '';
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

    getColor(val: string) {
        const cop = Number(val);

        // #E66465
        const rgb1 = [230, 100, 101];
        // #9198e5
        const rgb0 = [145, 152, 229];

        // based on https://stackoverflow.com/a/24253254/1384743
        const w = cop * 2 - 1;
        const w1 = (w + 1) / 2.0;
        const w2 = 1 - w1;
        return 'rgb('
            + (rgb1[0] * w1 + rgb0[0] * w2) + ','
            + (rgb1[1] * w1 + rgb0[1] * w2) + ','
            + (rgb1[2] * w1 + rgb0[2] * w2)
            + ')';
    }

    getName(gid: string) {
        return this.nameKeySelected === this.GID ? gid : this.response.germplasmCommonNamesMap[this.nameKeySelected][gid];
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
