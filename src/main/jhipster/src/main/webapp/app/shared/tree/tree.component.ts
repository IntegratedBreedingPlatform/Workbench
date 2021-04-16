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

        this.service.init()
            .subscribe((nodes: TreeNode[]) => {
                nodes.forEach((node) => this.addNode(node));
                this.nodes.forEach( (rootNode)  => {
                    const node = nodes.find((c) => rootNode.data.id === c.key);
                    if (node && node.children) {
                        this.addChildren(rootNode, node.children)
                    }
                });

                this.redrawNodes();
            });

        // FIXME tableStyleClass not working on primeng treetable 6?
        $('.ui-treetable-table').addClass('table table-striped table-bordered table-curved');
    }

    addNode(node: TreeNode) {
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

    expand(parent) {
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
            children.forEach((node) => {
                const child = this.toPrimeNgNode(node, parent);
                parent.children.push(child);
                // Recursively add "grand" children nodes as well
                if (node.children) {
                    this.addChildren(child, node.children)
                }
            });
            parent.expanded = true;
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
}
