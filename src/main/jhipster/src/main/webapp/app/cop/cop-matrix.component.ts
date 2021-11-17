import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../shared/modal/popup.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CopService } from './cop.service';
import { finalize } from 'rxjs/internal/operators/finalize';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../shared/alert/format-error-list';
import { AlertService } from '../shared/alert/alert.service';

@Component({
    selector: 'jhi-cop-matrix',
    templateUrl: './cop-matrix.component.html'
})
export class CopMatrixComponent {
    isLoading = false;
    array: string[][] = [];

    constructor(
        public activeModal: NgbActiveModal,
        private route: ActivatedRoute,
        private copService: CopService,
        private alertService: AlertService
    ) {
        const gids: number[] = this.route.snapshot.queryParamMap.get('gids').split(',').map((g) => Number(g));
        // TODO async
        this.isLoading = true;
        this.copService.getCopMatrixAs2dArray(gids).pipe(
            finalize(() => this.isLoading = false)
        ).subscribe((array: string[][]) => {
            this.array = array;
        }, (error) => this.onError(error));
    }

    isProcessing() {
        return this.array.length === 1 && this.array[0].length === 1
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
