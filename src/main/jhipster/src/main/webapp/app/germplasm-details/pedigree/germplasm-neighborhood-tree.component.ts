import { Input, OnInit } from '@angular/core';
import { TreeNode as PrimeNgTreeNode } from 'primeng/components/common/treenode';
import { GermplasmNeighborhoodTreeNode } from '../../shared/germplasm/model/germplasm-neighborhood-tree-node.model';

export abstract class GermplasmNeighborhoodTreeComponent implements OnInit {

    @Input() gid: number;
    public nodes: PrimeNgTreeNode[] = [];
    numberOfSteps = Array(10).fill(1).map((x, i) => i + 1);
    numberOfStepsBackward = 2;
    numberOfStepsForward = 3;
    isLoading = false;

    MAX_NAME_DISPLAY_SIZE = 30;

    constructor() {
    }

    numberOfStepsChanged() {
        this.loadTree();
    }

    ngOnInit(): void {
        this.loadTree();
    }

    abstract getGermplasmNeighborhoodTreeNode();

    loadTree() {
        this.isLoading = true;
        this.nodes = [];
        this.getGermplasmNeighborhoodTreeNode()
            .subscribe((germplasmNeighborhoodTreeNode: GermplasmNeighborhoodTreeNode) => {
                if (germplasmNeighborhoodTreeNode) {
                    this.addNode(germplasmNeighborhoodTreeNode);
                    this.addChildren(this.nodes[0], germplasmNeighborhoodTreeNode);
                    this.redrawNodes();
                }
                this.isLoading = false;
            });

    }

    addNode(node: GermplasmNeighborhoodTreeNode) {
        return this.nodes.push(this.toPrimeNgNode(node));
    }

    addChildren(parent: PrimeNgTreeNode, germplasmNeighborhoodTreeNode: GermplasmNeighborhoodTreeNode) {
        parent.children = [];
        if (germplasmNeighborhoodTreeNode.linkedNodes.length > 0) {
            parent.expanded = true;
            // Recursively add "grand" children nodes as well
            germplasmNeighborhoodTreeNode.linkedNodes.forEach((germplasmTreeNodeChild) => {
                const child = this.toPrimeNgNode(germplasmTreeNodeChild, parent);
                parent.children.push(child);
                this.addChildren(child, germplasmTreeNodeChild)
            });
        }
    }

    redrawNodes() {
        // see primefaces/primeng/issues/5966#issuecomment-402498667
        this.nodes = Object.assign([], this.nodes);
    }

    private toPrimeNgNode(node: GermplasmNeighborhoodTreeNode, parent?: PrimeNgTreeNode): PrimeNgTreeNode {
        return {
            label: node.preferredName,
            data: {
                id: node.gid,
                name: node.preferredName || ''
            },
            leaf: false,
            parent
        };
    }

}
