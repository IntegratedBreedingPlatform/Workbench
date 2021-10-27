import { Component, Input, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ParamContext } from '../../shared/service/param.context';
import { PopupService } from '../../shared/modal/popup.service';
import { AlertService } from '../../shared/alert/alert.service';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { GermplasmManagerContext } from '../../germplasm-manager/germplasm-manager.context';
import { ActivatedRoute, Router } from '@angular/router';
import { JhiEventManager } from 'ng-jhipster';
import { GermplasmListImportContext } from './germplasm-list-import.context';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';
import { GermplasmListImportUpdateComponent } from './germplasm-list-import-update.component';

@Component({
    selector: 'jhi-germplasm-list-variable-matches.component',
    templateUrl: 'germplasm-list-variable-matches.component.html'
})
export class GermplasmListVariableMatchesComponent implements OnInit {

    listId: number;

    HEADERS = HEADERS;

    page = 0;
    pageSize = 10;

    isLoading: boolean;
    isSaving: boolean;


    constructor(
        private route: ActivatedRoute,
        private translateService: TranslateService,
        private modal: NgbActiveModal,
        private modalService: NgbModal,
        private paramContext: ParamContext,
        private alertService: AlertService,
        private germplasmService: GermplasmService,
        private eventManager: JhiEventManager,
        private context: GermplasmListImportContext
    ) {
    }

    ngOnInit() {
        this.listId = Number(this.route.snapshot.queryParamMap.get('listId'));
    }

    back() {
        this.modal.close();
        const modalRef = this.modalService.open(GermplasmListImportUpdateComponent as Component, { size: 'lg', backdrop: 'static' });
    }

    dismiss() {
        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component, { backdrop: 'static' });
        confirmModalRef.componentInstance.message = this.translateService.instant('germplasm-list.import.cancel.confirm');
        confirmModalRef.result.then(() => this.modal.dismiss());
    }

    save() {

    }

}

export enum HEADERS {
    'ENTRY_NO' = 'ENTRY_NO',
    'ID' = 'ID',
    'NAME' = 'NAME',
    'DESCRIPTION' = 'DESCRIPTION'

}
