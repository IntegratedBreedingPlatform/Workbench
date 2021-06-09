import { Component, OnInit } from '@angular/core';
import { GermplasmCodeNameBatchResultModel } from '../../shared/germplasm/model/germplasm-code-name-batch-result.model';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-germplasm-coding-result-dialog',
    templateUrl: './germplasm-coding-result-dialog.component.html'
})
export class GermplasmCodingResultDialogComponent implements OnInit {

    results: GermplasmCodeNameBatchResultModel[];

    constructor(private modal: NgbActiveModal) {
    }

    ngOnInit(): void {
    }

    close() {
        this.modal.close();
    }


}
