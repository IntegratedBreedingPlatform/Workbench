import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../shared/modal/popup.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiAlertService } from 'ng-jhipster';
import { TreeService } from '../../shared/tree/tree.service';
import { TreeNode } from '../../shared/tree';
import { GermplasmTreeTableService } from '../../shared/tree/germplasm/germplasm-tree-table.service';
import { ParamContext } from '../../shared/service/param.context';
import { GermplasmListService } from './germplasm-list.service';
import { GermplasmManagerContext } from '../germplasm-manager.context';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs/internal/operators/finalize';
import { TreeNode as PrimeNgTreeNode } from 'primeng/components/common/treenode';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';
import { TranslateService } from '@ngx-translate/core';

declare var $: any;

@Component({
    selector: 'jhi-germplasm-list-append',
    templateUrl: './germplasm-list-append.component.html',
    providers: [
        { provide: TreeService, useClass: GermplasmTreeTableService },
        { provide: GermplasmListService, useClass: GermplasmListService }
    ]
})
export class GermplasmListAppendComponent implements OnInit {

    nodes: PrimeNgTreeNode[] = [];
    selectedNode: PrimeNgTreeNode;

    isLoading: boolean;

    constructor(private modal: NgbActiveModal,
                public service: TreeService,
                public activeModal: NgbActiveModal,
                private paramContext: ParamContext,
                private germplasmManagerContext: GermplasmManagerContext,
                public germplasmListService: GermplasmListService,
                private jhiAlertService: JhiAlertService,
                private translateService: TranslateService,
                private modalService: NgbModal
    ) {
        if (!this.paramContext.cropName) {
            this.paramContext.readParams();
        }
    }

    ngOnInit(): void {
        this.service.expand('').subscribe((res: TreeNode[]) => {
            res.forEach((node) => this.addNode(node));
            this.redrawNodes();
            // FIXME tableStyleClass not working on primeng treetable 6?
            $('.ui-treetable-table').addClass('table table-striped table-bordered table-curved');
            this.nodes.forEach((parent) => {
                this.expand(parent);
                parent.expanded = true;
            });
        });
    }

    private addNode(node: TreeNode) {
        return this.nodes.push(this.toPrimeNgNode(node));
    }

    onNodeExpand(event) {
        if (event.node) {
            this.expand(event.node);
        }
    }

    onNodeSelect(event) {
        const node: PrimeNgTreeNode = event.node;
        if (!node.expanded) {
            this.expand(node);
            node.expanded = true;
        }
    }

    closeModal() {
        this.activeModal.dismiss();
    }

    private expand(parent) {
        if (parent.leaf) {
            return;
        }
        this.service.expand(parent.data.id)
            .subscribe((res: TreeNode[]) => {
                parent.children = [];
                res.forEach((node) => {
                    parent.children.push(this.toPrimeNgNode(node, parent));
                });
                this.redrawNodes();
            });
    }

    private redrawNodes() {
        // see primefaces/primeng/issues/5966#issuecomment-402498667
        this.nodes = Object.assign([], this.nodes);
    }

    private toPrimeNgNode(node: TreeNode, parent?: PrimeNgTreeNode): PrimeNgTreeNode {
        return {
            label: node.name,
            data: {
                id: node.key,
                name: node.name || '',
                owner: node.owner || '',
                description: node.description || (parent && '-'), // omit for root folders
                type: node.type || '',
                noOfEntries: node.noOfEntries || ''
            },
            draggable: node.isFolder,
            droppable: node.isFolder,
            selectable: !node.isFolder,
            leaf: !node.isFolder,
            parent,
        };
    }

    validate() {
        if (!this.selectedNode) {
            this.jhiAlertService.error('germplasm-list-add.error.no-list-selected');
            return;
        }

        if (this.selectedNode.data.id === 'CROPLISTS' ||
            this.selectedNode.data.id === 'LISTS' ||
            this.selectedNode.data.type === 'LIST FOLDER') {
            this.jhiAlertService.error('germplasm-list-add.error.folder-selected');
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
            this.submitAddEntries();
        }, () => confirmModalRef.dismiss());
    }

    submitAddEntries() {
        this.isLoading = true;

        this.germplasmListService.addGermplasmEntriesToList(this.selectedNode.data.id, this.germplasmManagerContext.searchComposite)
            .pipe(finalize(() => {
                this.isLoading = false;
            })).subscribe(
            (res: void) => this.onSaveSuccess(),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    private onSaveSuccess() {
        this.jhiAlertService.success('germplasm-list-add.success');
        this.modal.close();
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.jhiAlertService.error('error.custom', { param: msg });
        } else {
            this.jhiAlertService.error('error.general', null, null);
        }
    }

}

@Component({
    selector: 'jhi-germplasm-list-append-popup',
    template: ''
})
export class GermplasmListAppendPopupComponent implements OnInit {
    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }

    ngOnInit(): void {
        const modal = this.popupService.open(GermplasmListAppendComponent as Component);
    }

}
