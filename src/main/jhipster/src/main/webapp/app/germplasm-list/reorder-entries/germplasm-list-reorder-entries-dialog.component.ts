import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { AlertService } from '../../shared/alert/alert.service';
import { GermplasmListService } from '../../shared/germplasm-list/service/germplasm-list.service';
import { GermplasmListReorderEntriesRequestModel } from '../../shared/germplasm-list/model/germplasm-list-reorder-entries-request.model';
import { finalize } from 'rxjs/internal/operators/finalize';
import { JhiEventManager } from 'ng-jhipster';
import { ListComponent } from '../list.component';

@Component({
    selector: 'jhi-germplasm-list-reorder-entries-dialog',
    templateUrl: './germplasm-list-reorder-entries-dialog.component.html'
})
export class GermplasmListReorderEntriesDialogComponent implements OnInit {

    @Input() listId: number;
    @Input() selectedEntries: number[];

    position: Position;
    fixedPosition: number;

    isLoading: boolean;

    Position = Position;

    constructor(private modal: NgbActiveModal,
                private alertService: AlertService,
                private activeModal: NgbActiveModal,
                private activatedRoute: ActivatedRoute,
                private germplasmListService: GermplasmListService,
                private eventManager: JhiEventManager) {
        this.isLoading = false;
    }

    ngOnInit(): void {
    }

    closeModal() {
        this.activeModal.dismiss();
    }

    reorder() {
        this.isLoading = true;
        const request: GermplasmListReorderEntriesRequestModel = this.getRequest();
        this.germplasmListService.reorderEntries(this.listId, request).pipe(finalize(() => {
            this.isLoading = false;
        })).subscribe(
            (res: HttpResponse<any>) => this.onReorderSuccess(),
            (res: HttpErrorResponse) => this.onError(res)
        );

    }

    isFormValid(f) {
        return f.form.valid && !this.isLoading && this.position;
    }

    private getRequest(): GermplasmListReorderEntriesRequestModel {
        const request: GermplasmListReorderEntriesRequestModel = new GermplasmListReorderEntriesRequestModel(this.selectedEntries);
        if (this.position === Position.START) {
            request.entryNumberPosition = 1;
            return request;
        }

        if (this.position === Position.END) {
            request.endOfList = true;
            return request;
        }

        request.entryNumberPosition = this.fixedPosition;
        return request;
    }

    private onReorderSuccess() {
        this.alertService.success('germplasm-list.list-data.reorder-entries.reorder.success');
        this.eventManager.broadcast({ name: ListComponent.GERMPLASMLIST_REORDER_EVENT_SUFFIX, content: '' });
        this.modal.close();
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
    }

}

enum Position {
    START = 'START',
    END = 'END',
    FIXED = 'FIXED'
}
