import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-modal',
    template: `
        <div class="container">
            <div class="modal-header">
                <h4 class="modal-title font-weight-bold">{{title}}</h4>
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true"
                        (click)="dismiss()">&times;
                </button>
            </div>
            <ng-content></ng-content>
        </div>
    `
})
export class ModalComponent {
    @Input() title: string;

    constructor(private modal: NgbActiveModal) {
    }

    dismiss() {
        this.modal.dismiss();
    }
}
