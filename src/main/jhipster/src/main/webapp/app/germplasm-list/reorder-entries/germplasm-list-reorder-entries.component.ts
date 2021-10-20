import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../shared/modal/popup.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { HttpErrorResponse } from '@angular/common/http';
import { AlertService } from '../../shared/alert/alert.service';

@Component({
    selector: 'jhi-germplasm-list-reorder-entries',
    templateUrl: './germplasm-list-reorder-entries.component.html'
})
export class GermplasmListReorderEntriesComponent implements OnInit {

    position: Position;
    fixedPosition: number;

    isLoading: boolean;

    Position = Position;

    constructor(private modal: NgbActiveModal,
                private alertService: AlertService,
                private activeModal: NgbActiveModal) {
        this.isLoading = false;
    }

    ngOnInit(): void {
    }

    closeModal() {
        this.activeModal.dismiss();
    }

    reorder() {

    }

    isFormValid(f) {
        return f.form.valid && !this.isLoading && this.position;
    }

    private onSaveSuccess() {
        this.alertService.success('germplasm-list-add.success');
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

@Component({
    selector: 'jhi-germplasm-list-reorder-entries-popup',
    template: ''
})
export class GermplasmListReorderEntriesPopupComponent implements OnInit {
    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }

    ngOnInit(): void {
        const modal = this.popupService.open(GermplasmListReorderEntriesComponent as Component);
    }

}
