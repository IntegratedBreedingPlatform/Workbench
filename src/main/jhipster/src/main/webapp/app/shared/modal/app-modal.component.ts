import {Component, EventEmitter, HostListener, Input, OnInit, Output} from '@angular/core';
import { AppModalService } from './app-modal.service';

/**
 * TODO Deprecated, use ModalComponent
 * ModalComponent - This class represents the modal component.
 * @requires Component
 */

@Component({
    selector: 'jhi-app-modal',
    templateUrl: './app-modal.component.html'
})
export class AppModalComponent implements OnInit {

    isOpen = false;
    animate = false;

    @Input() closebtn: boolean;
    @Input() modalId: string;
    @Input() modalTitle: string;
    @Input() modalSize: string;
    @Output() onClose = new EventEmitter();
    @HostListener('document:keyup', ['$event'])
    /**
     * keyup - Checks keys entered for the 'esc' key, attached to hostlistener
     */
    keyup(event: KeyboardEvent): void {
        if (event.keyCode === 27) {
            this.modalService.close(this.modalId);
        }
    }

    constructor(private modalService: AppModalService) { }

    /**
     * ngOnInit - Initiated when component loads
     */
    ngOnInit() {
        this.modalService.registerModal(this);
    }

    /**
     * close - Closes the selected modal
     */
    close(checkBlocking = false): void {
        this.modalService.close(this.modalId);
        this.onClose.emit();
    }

}
