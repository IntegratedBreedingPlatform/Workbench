import { OnInit } from '@angular/core';
import { TreeNode } from './tree-node.model';
import { TreeNode as PrimeNgTreeNode } from 'primeng/api';
import { TreeService } from './tree.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

declare var $: any;

export class TreeComponent implements OnInit {

    public nodes: PrimeNgTreeNode[] = [];
    selectedNodes: PrimeNgTreeNode[];

    constructor(public service: TreeService,
                public activeModal: NgbActiveModal) {
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

    selectLists() {
        const selected = this.selectedNodes.filter((node: PrimeNgTreeNode) => node.leaf)
            .map((node: PrimeNgTreeNode) => {
                return {
                    id: node.data.id,
                    name: node.data.name
                };
            });
        this.activeModal.close(selected);
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
}
