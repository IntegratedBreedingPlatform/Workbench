import { Component, OnInit } from '@angular/core';
import { TreeNode } from './tree-node.model';
import { TreeNode as PrimeNgTreeNode } from 'primeng/api';
import { TreeService } from './tree.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AlertService } from '../alert/alert.service';
import { ModalConfirmComponent } from '../modal/modal-confirm.component';
import { TranslateService } from '@ngx-translate/core';
import { HttpErrorResponse } from '@angular/common/http';

declare var $: any;
export type SelectionMode = 'single' | 'multiple';

export class TreeComponent implements OnInit {

    private readonly CROP_LIST_FOLDER = 'CROPLISTS';
    private readonly PROGRAM_LIST_FOLDER = 'LISTS';
    readonly NAME_MAX_LENGTH: number = 50;

    mode: Mode = Mode.None;
    Modes = Mode;
    isFolderSelectionMode = false;

    public nodes: PrimeNgTreeNode[] = [];
    name: string;
    // primeng sets either one or the depending on the mode
    _selectedNodes: PrimeNgTreeNode[] | PrimeNgTreeNode;

    get selectedNodes(): PrimeNgTreeNode[] {
        if (!this._selectedNodes) {
            return [];
        }
        if (this.selectionMode === 'multiple' && 'length' in this._selectedNodes) {
            return this._selectedNodes as PrimeNgTreeNode[];
        }
        return [this._selectedNodes as PrimeNgTreeNode];
    }

    disabledAddActionMessage: string;
    disabledRenameActionMessage: string;
    disabledDeleteActionMessage: string;

    private draggedNode: PrimeNgTreeNode;

    constructor(public isReadOnly: boolean,
                public selectionMode: SelectionMode,
                public service: TreeService,
                public activeModal: NgbActiveModal,
                public alertService: AlertService,
                public translateService: TranslateService,
                public modalService: NgbModal) {
        this.disabledAddActionMessage = '';
        this.disabledRenameActionMessage = '';
        this.disabledDeleteActionMessage = '';
    }

    ngOnInit(): void {
        this.service.init()
            .subscribe((nodes: TreeNode[]) => {
                nodes.forEach((node) => this.addNode(node));
                // FIXME tableStyleClass not working on primeng treetable 6?
                $('.ui-treetable-table').addClass('table table-striped table-bordered table-curved');
                this.nodes.forEach((rootNode) => {
                    const node = nodes.find((c) => rootNode.data.id === c.key);
                    if (node && node.children) {
                        this.addChildren(rootNode, node.children)
                    }
                });

                this.redrawNodes();
            });
    }

    addNode(node: TreeNode) {
        if (this.isFolderSelectionMode && !node.isFolder) {
            return;
        }
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
        }
    }

    finish() {
        const persistPromise = this.persistTreeState();
        persistPromise.then(() => {
            const selected: TreeComponentResult[] = this.selectedNodes.filter((node: PrimeNgTreeNode) => {
                const isFolder = !Boolean(node.leaf);
                return this.isFolderSelectionMode ? isFolder : !isFolder;
            }).map((node: PrimeNgTreeNode) => {
                return <TreeComponentResult>({
                    id: node.data.id,
                    name: node.data.name
                });
            });
            this.activeModal.close(selected);
        });
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

    private removeParent(node: PrimeNgTreeNode) {
        if (!node || !node.parent || !node.parent.children) {
            return;
        }
        const indexOf = node.parent.children.indexOf(node, 0);
        if (indexOf > -1) {
            node.parent.children.splice(indexOf, 1);
        }
    }

    private collectExpandedNodes(expandedNodes: string[], node: PrimeNgTreeNode) {
        if (!node || !node.expanded) {
            return;
        }
        expandedNodes.push(node.data.id);
        if (node.children.length > 0) {
            node.children.forEach((c) => this.collectExpandedNodes(expandedNodes, c));
        }
    }

    async persistTreeState() {
        const expandedNodes = [];
        // Only nodes under Program Lists. Nodes under Crop Lists are not persisted
        const programNode = this.nodes.find((node: PrimeNgTreeNode) => this.PROGRAM_LIST_FOLDER === node.data.id);
        this.collectExpandedNodes(expandedNodes, programNode);
        // Ensure that Program Lists node is always saved as expanded
        if (programNode && expandedNodes.length === 0) {
            expandedNodes.push(programNode.data.id);
        }
        await this.service.persist(expandedNodes).subscribe();
        return Promise.resolve()
    }

    closeModal() {
        const persistPromise = this.persistTreeState();
        persistPromise.then(() => {
            this.activeModal.dismiss();
        });
    }

    protected expand(parent) {
        if (parent.leaf) {
            return;
        }
        this.service.expand(parent.data.id)
            .subscribe((res: TreeNode[]) => {
                this.addChildren(parent, res);
                this.redrawNodes();
            });
    }

    addChildren(parent: any, children: TreeNode[]) {
        parent.children = [];
        if (children.length > 0) {
            parent.expanded = true;
            // Recursively add "grand" children nodes as well
            children.forEach((node) => {
                if (this.isFolderSelectionMode && !node.isFolder) {
                    return;
                }
                const child = this.toPrimeNgNode(node, parent);
                parent.children.push(child);
                if (node.children) {
                    this.addChildren(child, node.children)
                }
            });
        }
    }

    redrawNodes() {
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
                isLocked: node.isLocked || '',
                description: node.description || (parent && '-'), // omit for root folders
                type: node.type || '',
                noOfEntries: node.noOfEntries || ''
            },
            draggable: node.isFolder,
            droppable: node.isFolder,
            selectable: this.isSelectable(node),
            leaf: !node.isFolder,
            parent,
        };
    }

    isSelectable(node: TreeNode) {
        return !node.isFolder;
    }

    setMode(mode: Mode, iconClickEvent) {
        if (this.isDisabled(iconClickEvent)) {
            return;
        }

        if (!this.isOneFolderSelected()) {
            this.alertService.error('bmsjHipsterApp.tree-table.messages.folder.select.one.folder');
            return;
        }

        const folder: PrimeNgTreeNode = this.selectedNodes[0];
        this.mode = mode;
        if (this.mode === Mode.Delete) {
            this.validateDeleteFolder(folder);
        } else {
            this.setName(folder);
        }
    }

    private isDisabled(iconClickEvent) {
        return iconClickEvent.target.classList.contains('disable-image');
    }

    private isOneFolderSelected(): boolean {
        return this.selectedNodes.length === 1;
    }

    isAddActionDisabled(): boolean {
        if (!this.isOneFolderSelected()) {
            this.disabledAddActionMessage = this.getFolderSelectionErrorMessage();
            return true;
        }

        const folder: PrimeNgTreeNode = this.selectedNodes[0];
        if (folder.data.id === this.CROP_LIST_FOLDER) {
            this.disabledAddActionMessage = this.translateService.instant('bmsjHipsterApp.tree-table.messages.disabled.crop.folder.selected');
            return true;
        }

        if (folder.leaf) {
            this.disabledAddActionMessage = this.translateService.instant('bmsjHipsterApp.tree-table.messages.disabled.not.folder.selected');
            return true;
        }

        this.disabledAddActionMessage = '';
        return false;
    }

    isRenameActionDisabled(): boolean {
        if (!this.isOneFolderSelected()) {
            this.disabledRenameActionMessage = this.getFolderSelectionErrorMessage();
            return true;
        }

        const folder: PrimeNgTreeNode = this.selectedNodes[0];
        if (this.isRootFolder(folder)) {
            this.disabledRenameActionMessage = this.translateService.instant('bmsjHipsterApp.tree-table.messages.disabled.root.folder.selected');
            return true;
        }

        if (folder.leaf) {
            this.disabledRenameActionMessage = this.translateService.instant('bmsjHipsterApp.tree-table.messages.disabled.not.folder.selected');
            return true;
        }

        this.disabledRenameActionMessage = '';
        return false;
    }

    isDeleteActionDisabled(): boolean {
        if (!this.isOneFolderSelected()) {
            this.disabledDeleteActionMessage = this.getFolderSelectionErrorMessage();
            return true;
        }

        const folder: PrimeNgTreeNode = this.selectedNodes[0];
        if (this.isRootFolder(folder)) {
            this.disabledDeleteActionMessage = this.translateService.instant('bmsjHipsterApp.tree-table.messages.disabled.root.folder.selected');
            return true;
        }

        if (folder.leaf) {
            this.disabledDeleteActionMessage = this.translateService.instant('bmsjHipsterApp.tree-table.messages.disabled.not.folder.selected');
            return true;
        }

        this.disabledDeleteActionMessage = '';
        return false;
    }

    private getFolderSelectionErrorMessage(): string {
        if (this.selectedNodes.length === 0) {
            return this.translateService.instant('bmsjHipsterApp.tree-table.messages.disabled.no.folder.selected');
        }

        return this.translateService.instant('bmsjHipsterApp.tree-table.messages.disabled.several.items.selected');
    }

    submitAddOrRenameFolder() {
        if (!this.isOneFolderSelected()) {
            return;
        }

        if (this.name.length > this.NAME_MAX_LENGTH) {
            this.alertService.error('bmsjHipsterApp.tree-table.messages.folder.name.too.long', { length: this.NAME_MAX_LENGTH })
            return;
        }

        const folder: PrimeNgTreeNode = this.selectedNodes[0];
        if (this.mode === Mode.Add) {
            const isParentCropList = this.isParentCropList(folder);
            this.service.create(this.name, folder.data.id, isParentCropList).subscribe((res) => {
                    this.mode = this.Modes.None;
                    this.expand(folder);
                    this.alertService.success('bmsjHipsterApp.tree-table.messages.folder.create.successfully');
                },
                (res: HttpErrorResponse) =>
                    this.alertService.error('bmsjHipsterApp.tree-table.messages.error', { param: res.error.errors[0].message }));
        } else if (this.mode === Mode.Rename) {
            this.service.rename(this.name, folder.data.id).subscribe(() => {
                    this.mode = this.Modes.None;
                    folder.data.name = this.name;
                    this.redrawNodes();
                    this.alertService.success('bmsjHipsterApp.tree-table.messages.folder.rename.successfully');
                },
                (res: HttpErrorResponse) =>
                    this.alertService.error('bmsjHipsterApp.tree-table.messages.error', { param: res.error.errors[0].message }));
        }
    }

    protected isParentCropList(node: PrimeNgTreeNode): boolean {
        if (node.parent) {
            return this.isParentCropList(node.parent);
        }
        return node.data.id === 'CROPLISTS';
    }

    protected validateDeleteFolder(folder: PrimeNgTreeNode) {
        if (folder.children && folder.children.length !== 0) {
            this.alertService.error('bmsjHipsterApp.tree-table.messages.folder.cannot.delete.has.children',
                { folder: folder.data.name });
            return;
        }
        this.confirmDeleteFolder(folder);
    }

    private isRootFolder(folder: PrimeNgTreeNode) {
        return folder.data.id === this.CROP_LIST_FOLDER || folder.data.id === this.PROGRAM_LIST_FOLDER;
    }

    private confirmDeleteFolder(folder: PrimeNgTreeNode) {
        let message = '';
        if (folder) {
            message = this.translateService.instant('bmsjHipsterApp.tree-table.messages.folder.delete.question',
                {id: folder.data.name});
        } else {
            return;
        }

        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.message = message;
        confirmModalRef.componentInstance.title = this.translateService.instant('bmsjHipsterApp.tree-table.action.folder.delete');

        confirmModalRef.result.then(() => {
            this.submitDeleteFolder(folder);
        }, () => confirmModalRef.dismiss());
    }

    private submitDeleteFolder(folder: PrimeNgTreeNode) {
        this.mode = this.Modes.None;
        this.service.delete(folder.data.id).subscribe(() => {
                this.expand(folder.parent);
                this.alertService.success('bmsjHipsterApp.tree-table.messages.folder.delete.successfully');
            },
            (res: HttpErrorResponse) =>
                this.alertService.error('bmsjHipsterApp.tree-table.messages.error', { param: res.error.errors[0].message })
        );
    }

    private setName(folder: PrimeNgTreeNode) {
        if (this.mode === Mode.Add) {
            this.name = '';
        } else if (this.mode === Mode.Rename) {
            this.name = folder.data.name;
        }
    }

}

// TODO: move to enum
export enum Mode {
    Add,
    Rename,
    Delete,
    None
}

// TODO reword usages of modal return, making explicit that result is entity agnostic
export interface TreeComponentResult {
    id: number;
    name: string;
}
