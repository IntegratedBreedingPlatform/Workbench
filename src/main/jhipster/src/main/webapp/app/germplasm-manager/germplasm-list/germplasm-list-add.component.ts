import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../shared/modal/popup.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TreeService } from '../../shared/tree/tree.service';
import { TreeComponent } from '../../shared/tree';
import { GermplasmTreeService } from '../../shared/tree/germplasm/germplasm-tree.service';
import { ParamContext } from '../../shared/service/param.context';
import { GermplasmManagerContext } from '../germplasm-manager.context';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs/internal/operators/finalize';
import { TreeNode as PrimeNgTreeNode } from 'primeng/components/common/treenode';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';
import { TranslateService } from '@ngx-translate/core';
import { AlertService } from '../../shared/alert/alert.service';
import { GermplasmListService } from '../../shared/germplasm-list/service/germplasm-list.service';
import { JhiEventManager } from 'ng-jhipster';

declare var $: any;

@Component({
    selector: 'jhi-germplasm-list-add',
    templateUrl: './germplasm-list-add.component.html',
    providers: [
        { provide: TreeService, useClass: GermplasmTreeService },
        GermplasmListService
    ]
})
export class GermplasmListAddComponent extends TreeComponent {

    selectedNode: PrimeNgTreeNode;

    isLoading: boolean;

    constructor(private modal: NgbActiveModal,
                public service: TreeService,
                public activeModal: NgbActiveModal,
                private paramContext: ParamContext,
                private germplasmManagerContext: GermplasmManagerContext,
                public germplasmListService: GermplasmListService,
                private alertService: AlertService,
                private translateService: TranslateService,
                private modalService: NgbModal,
                private eventManager: JhiEventManager
    ) {
        super(service, modal);
        if (!this.paramContext.cropName) {
            this.paramContext.readParams();
        }
    }

    validate() {
        if (!this.selectedNode) {
            this.alertService.error('germplasm-list-add.error.no-list-selected');
            return;
        }

        if (this.selectedNode.data.id === 'CROPLISTS' ||
            this.selectedNode.data.id === 'LISTS' ||
            this.selectedNode.data.type === 'LIST FOLDER') {
            this.alertService.error('germplasm-list-add.error.folder-selected');
            return;
        }

        this.confirmAddEntries();
    }

    confirmAddEntries() {
        let message = '';
        if (this.selectedNode) {
            message = this.translateService.instant('germplasm-list-add.add-question',
                {name: this.selectedNode.data.name});
        } else {
            return;
        }

        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.message = message;
        confirmModalRef.componentInstance.title = this.translateService.instant('germplasm-list-add.header');

        confirmModalRef.result.then(() => {
            const persistPromise = this.persistTreeState();
            persistPromise.then(() => {
                this.submitAddEntries();
            });

        }, () => confirmModalRef.dismiss());
    }

    submitAddEntries() {
        this.isLoading = true;

        if (this.germplasmManagerContext.searchComposite) {
            this.germplasmListService.addGermplasmEntriesToList(this.selectedNode.data.id, this.germplasmManagerContext.searchComposite)
                .pipe(finalize(() => {
                    this.isLoading = false;
                })).subscribe(
                (res: void) => this.onSaveSuccess(),
                (res: HttpErrorResponse) => this.onError(res)
            );
        } else {
            this.germplasmListService.addGermplasmListEntriesToAnotherList(this.selectedNode.data.id, this.germplasmManagerContext.sourceListId)
                .pipe(finalize(() => {
                    this.isLoading = false;
                })).subscribe(
                (res: void) => this.onSaveSuccess(),
                (res: HttpErrorResponse) => this.onError(res)
            );
        }
    }

    private onSaveSuccess() {
        this.alertService.success('germplasm-list-add.success');
        if (this.germplasmManagerContext.sourceListId) {
            this.eventManager.broadcast({ name: 'addToGermplasmList', content: this.selectedNode.data.id });
        }
        this.modal.close();
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
    }

}

@Component({
    selector: 'jhi-germplasm-list-add-popup',
    template: ''
})
export class GermplasmListAddPopupComponent implements OnInit {
    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }

    ngOnInit(): void {
        const modal = this.popupService.open(GermplasmListAddComponent as Component);
    }

}
