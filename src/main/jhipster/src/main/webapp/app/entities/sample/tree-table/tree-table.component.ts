import { Component, HostListener, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { TreeNode } from './tree-node.model';
import { TreeNode as PrimeNgTreeNode } from 'primeng/api';
import { ModalAnimation } from '../../../shared/animations/modal.animation';
import { ActivatedRoute, Router } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { AlertService } from '../../../shared/alert/alert.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ModalConfirmComponent } from '../../../shared/modal/modal-confirm.component';
import { TranslateService } from '@ngx-translate/core';
import { SampleTreeService } from '.';
import { TreeService } from '../../../shared/tree/tree.service';

declare var $: any;

/**
 * TODO tree state persist
 * TODO: remove it. Use TreeComponent
 */
@Component({
    selector: 'jhi-tree-table',
    animations: [ModalAnimation],
    templateUrl: './tree-table.component.html',
    providers: [
        { provide: TreeService, useClass: SampleTreeService }
    ]
})
export class TreeTableComponent implements OnInit {

    readonly NAME_MAX_LENGTH: number = 50;

    public mode: Mode = Mode.None;
    public Modes = Mode;

    public nodes: PrimeNgTreeNode[] = [];
    public selectedNode: PrimeNgTreeNode;
    public name: string; // rename or add item

    private draggedNode: PrimeNgTreeNode;

    @HostListener('document:keyup', ['$event'])
    /**
     * keyup - Checks keys entered for the 'esc' key, attached to hostlistener
     */
    keyup(event: KeyboardEvent): void {
        if (event.keyCode === 27) {
            this.closeModal();
        }
    }

    private removeParent(node: PrimeNgTreeNode) {
        if (!node || !node.parent || !node.parent.children) {
            return;
        }
        const indexOf = node.parent.children.indexOf(node, 0);
        if (indexOf > -1) {
            node.parent.children.splice(indexOf, 1);
        }
    }

    constructor(private modal: NgbActiveModal,
                private languageservice: JhiLanguageService,
                private translateService: TranslateService,
                private alertService: AlertService,
                private service: TreeService,
                private activatedRoute: ActivatedRoute,
                private router: Router,
                private modalService: NgbModal) {
    }

    ngOnInit(): void {
        this.loadTree();
    }

    private loadTree() {
        this.service.expand(null).subscribe((res: TreeNode[]) => {
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

    private onSuccess(data, headers) {
    }

    private onError(error) {

    }

    onDragStart(event, node: PrimeNgTreeNode) {
        this.draggedNode = node;
    }

    onDragEnd(event, node: PrimeNgTreeNode) {
    }

    onDrop(event, node: PrimeNgTreeNode) {
        if (this.draggedNode) {
            if (this.draggedNode.children && this.draggedNode.children.length !== 0) {
                this.alertService.error('bmsjHipsterApp.tree-table.messages.folder.cannot.move.has.children', { folder: this.draggedNode.data.name });
                this.draggedNode = null;
                return;
            } else if (node.data.id === 'CROPLISTS' && !this.draggedNode.leaf) {
                this.alertService.error('bmsjHipsterApp.tree-table.messages.folder.move.to.crop.list.not.allowed');
                this.draggedNode = null;
                return;
            } else if (node.leaf) {
                this.alertService.error('bmsjHipsterApp.tree-table.messages.folder.move.not.allowed');
                this.draggedNode = null;
                return;
            }
            const isParentCropList = this.isParentCropList(node);
            this.service.move(this.draggedNode.data.id, node.data.id, isParentCropList).subscribe((res) => {
                    if (!node.children) {
                        node.children = [];
                    }
                    node.children.push(this.draggedNode);
                    this.removeParent(this.draggedNode);
                    this.draggedNode.parent = node;
                    this.redrawNodes();
                    this.draggedNode = null;
                },
                (res: HttpErrorResponse) =>
                    this.alertService.error('bmsjHipsterApp.tree-table.messages.error', { param: res.error.errors[0].message }));
        }
    }

    onDragEnter(event, node: PrimeNgTreeNode) {
    }

    onDragLeave(event, node: PrimeNgTreeNode) {
    }

    onNodeExpand(event) {
        if (event.node) {
            this.expand(event.node);
        }
    }

    onRowDblclick(event) {
        if (event.node && event.node.leaf) {
            this.openList();
        }
    }

    onNodeSelect(event) {
        const node: PrimeNgTreeNode = event.node;
        // TODO Remove when edition or deletion is implemented
        if (node.leaf) {
            this.mode = Mode.None;
            return;
        }
        if (this.isRootFolder()) {
            this.mode = Mode.None;
        }
        this.setName();
        if (!node.expanded) {
            node.expanded = true;
            this.expand(node);
        }
    }

    isRootFolder() {
        return this.selectedNode.data.id === 'CROPLISTS' || this.selectedNode.data.id === 'LISTS';
    }

    setMode(mode: Mode, iconClickEvent) {
        if (this.isDisabled(iconClickEvent)) {
            return;
        }
        this.mode = mode;
        if (this.mode === Mode.Delete) {
            this.validateDeleteFolder();
        } else {
            this.setName();
        }
    }

    validateDeleteFolder() {
        if (this.selectedNode.children && this.selectedNode.children.length !== 0) {
            this.alertService.error('bmsjHipsterApp.tree-table.messages.folder.cannot.delete.has.children',
                { folder: this.selectedNode.data.name });
            return;
        }
        this.confirmDeleteFolder();
    }

    confirmDeleteFolder() {
        let message = '';
        if (this.selectedNode) {
            message = this.translateService.instant('bmsjHipsterApp.tree-table.messages.folder.delete.question',
                {id: this.selectedNode.data.name});
        } else {
            return;
        }

        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.message = message;
        confirmModalRef.componentInstance.title = this.translateService.instant('bmsjHipsterApp.tree-table.action.folder.delete');

        confirmModalRef.result.then(() => {
            this.submitDeleteFolder();
        }, () => confirmModalRef.dismiss());
    }

    private setName() {
        if (this.mode === Mode.Add) {
            this.name = '';
        } else if (this.mode === Mode.Rename) {
            this.name = this.selectedNode.data.name;
        }
    }

    isDisabled(iconClickEvent) {
        return iconClickEvent.target.classList.contains('disable-image');
    }

    private expand(parent, selectedId?: any) {
        this.service.expand(parent.data.id)
            .subscribe((res2: TreeNode[]) => {
                parent.children = [];
                res2.forEach((node) => {
                    const primeNgTreeNode = this.toPrimeNgNode(node, parent);
                    parent.children.push(primeNgTreeNode);
                    if (selectedId === primeNgTreeNode.data.id) {
                        this.selectedNode = primeNgTreeNode;
                        this.selectedNode.expanded = true;
                    }
                });
                this.redrawNodes();
            }, (res2: HttpErrorResponse) => this.onError(res2.message));
    }

    private addNode(node: TreeNode) {
        return this.nodes.push(this.toPrimeNgNode(node));
    }

    private redrawNodes() {
        // see primefaces/primeng/issues/5966#issuecomment-402498667
        this.nodes = Object.assign([], this.nodes);
    }

    private toPrimeNgNode(node: TreeNode, parent?: PrimeNgTreeNode): PrimeNgTreeNode {
        return {
            label: node.name,
            data: {
                id: node.id,
                name: node.name || '',
                owner: node.owner || '',
                description: node.description || '',
                type: node.type || '',
                noOfEntries: node.noOfEntries || ''
            },
            draggable: node.isFolder,
            droppable: node.isFolder,
            selectable: true,
            leaf: !node.isFolder,
            parent,
        };
    }

    closeModal() {
        this.nodes = [];
        this.mode = this.Modes.None;
        this.loadTree();
        this.modal.dismiss();
    }

    openList() {
        return this.router.navigate(['/sample-manager'], {
            queryParams: {
                listId: this.selectedNode.data.id
            },
            replaceUrl: true,
            queryParamsHandling: 'merge'
        });
    }

    selectList() {
        this.openList().then(() => {
            this.closeModal();
        });
    }

    submitDeleteFolder() {
        this.mode = this.Modes.None;
        this.service.delete(this.selectedNode.data.id).subscribe(() => {
                this.expand(this.selectedNode.parent);
                this.alertService.success('bmsjHipsterApp.tree-table.messages.folder.delete.successfully');
            },
            (res: HttpErrorResponse) =>
                this.alertService.error('bmsjHipsterApp.tree-table.messages.error', { param: res.error.errors[0].message })
        );
    }

    submitAddOrRenameFolder() {
        if (this.mode === Mode.Add) {
            const isParentCropList = this.isParentCropList(this.selectedNode);
            this.service.create(this.name, this.selectedNode.data.id, isParentCropList).subscribe((res) => {
                    this.mode = this.Modes.None;
                    this.expand(this.selectedNode, res.id);
                    this.alertService.success('bmsjHipsterApp.tree-table.messages.folder.create.successfully');
                },
                (res: HttpErrorResponse) =>
                    this.alertService.error('bmsjHipsterApp.tree-table.messages.error', { param: res.error.errors[0].message }));
        } else if (this.mode === Mode.Rename) {
            this.service.rename(this.name, this.selectedNode.data.id).subscribe(() => {
                    this.mode = this.Modes.None;
                    this.selectedNode.data.name = this.name;
                    this.redrawNodes();
                    this.alertService.success('bmsjHipsterApp.tree-table.messages.folder.rename.successfully');
                },
                (res: HttpErrorResponse) =>
                    this.alertService.error('bmsjHipsterApp.tree-table.messages.error', { param: res.error.errors[0].message }));
        }
    }

    isParentCropList(node: PrimeNgTreeNode): boolean {
        if (node.parent) {
            return this.isParentCropList(node.parent);
        }
        return node.data.id === 'CROPLISTS';
    }
}

export enum Mode {
    Add,
    Rename,
    Delete,
    None
}
