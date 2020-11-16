import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmImportComponent } from './germplasm-import.component';

@Component({
    selector: 'jhi-germplasm-import',
    templateUrl: './germplasm-import-basic-details.component.html'
})
export class GermplasmImportBasicDetailsComponent implements OnInit {
    importData: any;

    constructor(
        private translateService: TranslateService,
        private modal: NgbActiveModal,
        private modalService: NgbModal
    ) {
    }

    ngOnInit(): void {
    }

    next() {
    }

    dismiss() {
        this.modal.dismiss();
    }

    back() {
        this.modal.close();
        const backModalRef = this.modalService.open(GermplasmImportComponent as Component,
            { size: 'lg', backdrop: 'static' });
        backModalRef.componentInstance.importData = this.importData;
    }
}
