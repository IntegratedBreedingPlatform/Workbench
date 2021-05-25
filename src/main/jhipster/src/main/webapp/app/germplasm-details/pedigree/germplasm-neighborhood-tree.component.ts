import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { GermplasmPedigreeService } from '../../shared/germplasm/service/germplasm.pedigree.service';
import { TreeNode as PrimeNgTreeNode } from 'primeng/components/common/treenode';
import { GermplasmNeighborhoodTreeNode } from '../../shared/germplasm/model/germplasm-neighborhood-tree-node.model';
import { GermplasmDetailsUrlService } from '../../shared/germplasm/service/germplasm-details.url.service';

@Component({
    selector: 'jhi-germplasm-neighborhood-tree',
    templateUrl: './germplasm-neighborhood-tree.component.html',
    styleUrls: ['./germplasm-neighborhood-tree.component.css'],
    encapsulation: ViewEncapsulation.None
})
export class GermplasmNeighborhoodTreeComponent implements OnInit {

    @Input() gid: number;
    @Input() type: string;
    public nodes: PrimeNgTreeNode[] = [];
    numberOfStepsBackward = 2;
    numberOfStepsForward =  3;
    isLoading = false;

    constructor(public germplasmPedigreeService: GermplasmPedigreeService,
                public germplasmDetailsUrlService: GermplasmDetailsUrlService) {
    }

    numberOfStepsChanged() {
        this.loadTree();
    }

    ngOnInit(): void {
        this.loadTree();
    }

    loadTree() {
        this.isLoading = true;
        this.nodes = [];
        if (this.type === 'derivative') {
            this.germplasmPedigreeService.getDerivativeNeighborhood(this.gid, this.numberOfStepsBackward, this.numberOfStepsForward)
                .subscribe((germplasmNeighborhoodTreeNode: GermplasmNeighborhoodTreeNode) => {
                    if (germplasmNeighborhoodTreeNode) {
                        this.addNode(germplasmNeighborhoodTreeNode);
                        this.addChildren(this.nodes[0], germplasmNeighborhoodTreeNode);
                        this.redrawNodes();
                    }
                    this.isLoading = false;
                });
        } else {
            this.germplasmPedigreeService.getMaintenanceNeighborhood(this.gid, this.numberOfStepsBackward, this.numberOfStepsForward)
                .subscribe((germplasmNeighborhoodTreeNode: GermplasmNeighborhoodTreeNode) => {
                    if (germplasmNeighborhoodTreeNode) {
                        this.addNode(germplasmNeighborhoodTreeNode);
                        this.addChildren(this.nodes[0], germplasmNeighborhoodTreeNode);
                        this.redrawNodes();
                    }
                    this.isLoading = false;
                });
        }

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
