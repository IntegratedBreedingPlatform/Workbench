import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ModalService } from './modal.service';

@Component({
    selector: 'jhi-modal-confirm',
    templateUrl: './modal-confirm.component.html'
})

export class ModalConfirmComponent {

    modalId = 'modal-confirm';

    @Input() modalTitle: string;
    @Input() modalMessage: string;

    @Output() onProceed = new EventEmitter();

    constructor(private modalService: ModalService) {
    }

    close() {
        this.modalService.close(this.modalId);
    }

    proceed() {
        this.modalService.close(this.modalId);
        this.onProceed.emit();
    }

}
