import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { TreeNode } from './tree-node.model';
import { TreeNode as PrimeNgTreeNode } from 'primeng/api';
import { ModalAnimation } from '../../../shared/animations/modal.animation';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { SampleTreeService } from './sample-tree.service';

declare var authToken: string
    , selectedProjectId: string
    , loggedInUserId: string;

declare var deleteConfirmation: string;
declare var $: any;

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
    crop: string;
    private paramSubscription: Subscription;

    public mode: Mode = Mode.None;
    public Modes = Mode;

    public nodes: PrimeNgTreeNode[] = [];
    public selected: PrimeNgTreeNode;
    public name: string; // rename or add item

    private draggedNode: PrimeNgTreeNode;

    // TODO improve globals
    public deleteConfirmation = deleteConfirmation;

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
    constructor(public service: SampleTreeService,
                private activatedRoute: ActivatedRoute,
                private router: Router) {
        this.paramSubscription = this.activatedRoute.params.subscribe((params) => {
            this.crop = params['crop'];
        });
    }

    ngOnInit(): void {
        this.service.getInitTree(AUTH_PARAMS).subscribe((res: HttpResponse<TreeNode[]>) => {
            res.body.forEach((node) => this.addNode(node));
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
        this.draggedNode = null;
    }

    onDrop(event, node: PrimeNgTreeNode) {
        if (this.draggedNode) {
            if (!node.children) {
                node.children = [];
            }
            node.children.push(this.draggedNode);
            TreeTableComponent.removeParent(this.draggedNode);
            this.draggedNode.parent = node;
            this.repaintRows();
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

    private expand(parent) {
        this.service
            .expand(parent.data.id, AUTH_PARAMS)
            .subscribe((res2: HttpResponse<TreeNode[]>) => {
                parent.children = [];
                res2.body.forEach((node) => {
                    parent.children.push(this.toPrimeNgNode(node, parent))
                });
                this.repaintRows();
            }, (res2: HttpErrorResponse) => this.onError(res2.message));
    }

    private addNode(node: TreeNode) {
        return this.nodes.push(this.toPrimeNgNode(node));
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
        $('#listTreeModal').modal('hide');
    }

    openList() {
        return this.router.navigate(['/' + this.crop + '/sample-browse'], {
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

    // XXX TreeTable still don't support odd/even
    // It'll be implemented in new TreeTable https://github.com/primefaces/primeng/issues/4813
    repaintRows() {
        setTimeout(() => {
            $('.ui-treetable-row.ui-treetable-row-selectable:odd').css('background-color', '#e3e3e3');
            $('.ui-treetable-row.ui-treetable-row-selectable:even').css('background-color', '#f0eeee');
        });
    }

}

export enum Mode {
    Add,
    Rename,
    Delete,
    None
}
