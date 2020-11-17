import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmImportComponent } from './germplasm-import.component';
import { Attribute } from '../../shared/attributes/model/attribute.model';
import { NameType } from '../../shared/germplasm/model/name-type.model';

@Component({
    selector: 'jhi-germplasm-import',
    templateUrl: './germplasm-import-basic-details.component.html'
})
export class GermplasmImportBasicDetailsComponent implements OnInit {

    data: any;
    nameTypes: NameType[];
    attributes: Attribute[];
    nameColumnsWithData = {};

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
    }
}
