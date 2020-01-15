import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { TreeNode } from './tree-node.model';
import { TreeNode as PrimeNgTreeNode } from 'primeng/api';
import { ModalAnimation } from '../../../shared/animations/modal.animation';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { SampleTreeService } from './sample-tree.service';
import { JhiAlertService } from 'ng-jhipster';

declare var authToken: string
    , selectedProjectId: string
    , loggedInUserId: string;

declare var $: any;
declare const cropName: string;
declare const currentProgramId: string;

const AUTH_PARAMS = {
    authToken,
    selectedProjectId,
    loggedInUserId
};

@Component({
    selector: 'jhi-tree-table',
    animations: [ModalAnimation],
    templateUrl: './tree-table.component.html'
})
export class TreeTableComponent implements OnInit {

    private paramSubscription: Subscription;

    public mode: Mode = Mode.None;
    public Modes = Mode;

    public nodes: PrimeNgTreeNode[] = [];
    public selected: PrimeNgTreeNode;
    public name: string; // rename or add item

    private draggedNode: PrimeNgTreeNode;

    private static removeParent(node: PrimeNgTreeNode) {
        if (!node || !node.parent || !node.parent.children) {
            return;
        }
        const indexOf = node.parent.children.indexOf(node, 0);
        if (indexOf > -1) {
            node.parent.children.splice(indexOf, 1);
        }
    }

    // TODO make generic interface: TreeService
    constructor(private alertService: JhiAlertService,
                private service: SampleTreeService,
                private activatedRoute: ActivatedRoute,
                private router: Router) {
        this.paramSubscription = this.activatedRoute.queryParams.subscribe((params) => {
            service.setCrop(cropName);
            service.setProgram(currentProgramId);
        });
    }

    ngOnInit(): void {
        this.loadTree();
    }

    private loadTree() {
        this.service.getInitTree(AUTH_PARAMS).subscribe((res: HttpResponse<TreeNode[]>) => {
            res.body.forEach((node) => this.addNode(node));
            this.redrawNodes();
            this.nodes.forEach((parent) => {
                this.expand(parent);
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
            this.service.move(this.draggedNode.data.id, node.data.id).subscribe((res) => {
                    if (!node.children) {
                        node.children = [];
                    }
                    node.children.push(this.draggedNode);
                    TreeTableComponent.removeParent(this.draggedNode);
                    this.draggedNode.parent = node;
                    this.redrawNodes();
                    this.draggedNode = null;
                },
                (res: HttpErrorResponse) =>
                    this.alertService.error('bmsjHipsterApp.tree-table.error', { param: res.error.errors[0].message }));
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
        if (this.isRootFolder() && this.mode === Mode.Rename || this.mode === Mode.Delete) {
            this.mode = Mode.None;
        }
        this.setName();
        if (!node.expanded) {
            node.expanded = true;
            this.expand(node);
        }
    }

    isRootFolder() {
        return this.selected.data.id === 'CROPLISTS' || this.selected.data.id === 'LISTS';
    }

    setMode(mode: Mode, iconClickEvent) {
        if (this.isDisabled(iconClickEvent)) {
            return;
        }
        this.mode = mode;
        this.setName();
    }

    private setName() {
        if (this.mode === Mode.Add) {
            this.name = '';
        } else if (this.mode === Mode.Rename) {
            this.name = this.selected.data.name;
        }
    }

    isDisabled(iconClickEvent) {
        return iconClickEvent.target.classList.contains('disable-image');
    }

    private expand(parent, selectedId?: any) {
        this.service
            .expand(parent.data.id, AUTH_PARAMS)
            .subscribe((res2: HttpResponse<TreeNode[]>) => {
                parent.children = [];
                res2.body.forEach((node) => {
                    const primeNgTreeNode = this.toPrimeNgNode(node, parent);
                    parent.children.push(primeNgTreeNode);
                    if (selectedId === primeNgTreeNode.data.id) {
                        this.selected = primeNgTreeNode;
                        this.selected.expanded = true;
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
                numOfEntries: node.numOfEntries || ''
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
        $('#listTreeModal').modal('hide');
    }

    openList() {
        return this.router.navigate(['/sample-manager'], {
            queryParams: {
                listId: this.selected.data.id
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

    submitDeleteFolderInTreeTable() {
        this.mode = this.Modes.None;
        this.service.delete(this.selected.data.id).subscribe(() => {
                this.expand(this.selected.parent);
                this.alertService.success('bmsjHipsterApp.tree-table.folder.delete.successfully');
            },
            (res: HttpErrorResponse) =>
                this.alertService.error('bmsjHipsterApp.tree-table.error', { param: res.error.errors[0].message })
        );
    }

    submitAddOrRenameFolderInTreeTable() {
        if (this.mode === Mode.Add) {
            this.service.create(this.name, this.selected.data.id).subscribe((res) => {
                    this.mode = this.Modes.None;
                    this.expand(this.selected, res.id);
                    this.alertService.success('bmsjHipsterApp.tree-table.folder.create.successfully');
                },
                (res: HttpErrorResponse) =>
                    this.alertService.error('bmsjHipsterApp.tree-table.error', { param: res.error.errors[0].message }));
        } else if (this.mode === Mode.Rename) {
            this.service.rename(this.name, this.selected.data.id).subscribe(() => {
                    this.mode = this.Modes.None;
                    this.selected.data.name = this.name;
                    this.redrawNodes();
                    this.alertService.success('bmsjHipsterApp.tree-table.folder.rename.successfully');
                },
                (res: HttpErrorResponse) =>
                    this.alertService.error('bmsjHipsterApp.tree-table.error', { param: res.error.errors[0].message }));
        }
    }
}

export enum Mode {
    Add,
    Rename,
    Delete,
    None
}
