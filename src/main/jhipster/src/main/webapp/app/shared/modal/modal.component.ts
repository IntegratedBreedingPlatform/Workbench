import {Component, EventEmitter, HostListener, Input, OnInit, Output} from '@angular/core';
import { ModalService } from './modal.service';

/**
 * ModalComponent - This class represents the modal component.
 * @requires Component
 */

@Component({
    selector: 'jhi-app-modal',
    templateUrl: './modal.component.html'
})
export class ModalComponent implements OnInit {

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
        console.log('esc');
        if (event.keyCode === 27) {
            this.modalService.close(this.modalId);
        }
    }

    constructor(private modalService: ModalService) { }

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
