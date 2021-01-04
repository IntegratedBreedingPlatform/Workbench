import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AppModalService } from './app-modal.service';

// TODO Deprecated, use ModalConfirmComponent
@Component({
    selector: 'jhi-app-modal-confirm',
    templateUrl: './app-modal-confirm.component.html'
})

export class AppModalConfirmComponent {

    modalId = 'modal-confirm';

    @Input() modalTitle: string;
    @Input() modalMessage: string;

    @Output() onProceed = new EventEmitter();

    constructor(private modalService: AppModalService) {
    }

    close() {
        this.modalService.close(this.modalId);
    }

    proceed() {
        this.modalService.close(this.modalId);
        this.onProceed.emit();
    }

}
