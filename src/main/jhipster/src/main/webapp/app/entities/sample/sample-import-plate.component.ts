import {Component} from '@angular/core';
import {ModalService} from '../../shared/modal/modal.service';

@Component({
    selector: 'jhi-sample-import-plate',
    templateUrl: './sample-import-plate.component.html',
    styleUrls: ['./sample-import-plate.component.css']
})

export class SampleImportPlateComponent {

    private modalId = 'import-plate-modal';

    constructor(private modalService: ModalService) {

    }

    close() {
        this.modalService.close(this.modalId);
    }
}
