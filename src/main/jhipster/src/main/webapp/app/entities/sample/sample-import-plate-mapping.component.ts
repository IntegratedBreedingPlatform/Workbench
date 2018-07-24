import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ModalService} from '../../shared/modal/modal.service';

@Component({
    selector: 'jhi-sample-import-plate-mapping',
    templateUrl: './sample-import-plate-mapping.component.html'
})

export class SampleImportPlateMappingComponent {

    modalId = 'import-plate-mapping-modal';

    @Input() importData: Array<Array<any>>;
    @Input() header: Array<any>;
    @Output() onClose = new EventEmitter();

    constructor(private modalService: ModalService) {

    }

    proceed() {
    }

    close() {
        this.modalService.close(this.modalId);
        this.onClose.emit();
    }

}
