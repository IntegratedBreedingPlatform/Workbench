import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiLanguageService } from 'ng-jhipster';

@Component({
    selector: 'jhi-germplasm-progenitors-audit',
    templateUrl: './germplasm-progenitors-audit.component.html',
    styleUrls: [
        '../germplasm-audit.scss'
    ]
})
export class GermplasmProgenitorsAuditComponent implements OnInit {

    constructor(private activeModal: NgbActiveModal,
                private jhiLanguageService: JhiLanguageService
    ) {
    }

    ngOnInit(): void {
    }

    dismiss() {
        this.activeModal.dismiss('cancel');
        if ((<any>window.parent).closeModal) {
            (<any>window.parent).closeModal();
        }
    }

}
