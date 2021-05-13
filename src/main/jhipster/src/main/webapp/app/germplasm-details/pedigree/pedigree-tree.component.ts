import { Component, Input, OnInit } from '@angular/core';
import { TreeNode as PrimeNgTreeNode } from 'primeng/components/common/treenode';
import { GermplasmTreeNode } from '../../shared/germplasm/model/germplasm-tree-node.model';
import { GermplasmPedigreeService } from '../../shared/germplasm/service/germplasm.pedigree.service';
import { GermplasmDetailsUrlService } from '../../shared/germplasm/service/germplasm-details.url.service';

@Component({
    selector: 'jhi-pedigree-tree',
    templateUrl: './pedigree-tree.component.html'
})
export class PedigreeTreeComponent implements OnInit {

    @Input() gid: number;
    includeDerivativeLines = false;
    numberOfGenerations: number;
    public nodes: PrimeNgTreeNode[] = [];

    constructor(public germplasmPedigreeService: GermplasmPedigreeService,
                public germplasmDetailsUrlService: GermplasmDetailsUrlService) {
    }

    ngOnInit(): void {
        this.loadTree();
    }

    loadTree() {
        this.nodes = [];
        this.germplasmPedigreeService.getGermplasmTree(this.gid, 2, this.includeDerivativeLines)
            .subscribe((germplasmTreeNode: GermplasmTreeNode) => {
                this.numberOfGenerations = germplasmTreeNode.numberOfGenerations;
                this.addNode(germplasmTreeNode);
                this.addChildren(this.nodes[0], germplasmTreeNode);
                this.redrawNodes();
            });
    }

    addNode(node: GermplasmTreeNode) {
        return this.nodes.push(this.toPrimeNgNode(node));
    }

    onNodeExpand(event) {
        if (event.node) {
            this.expand(event.node);
        }
    }

    expand(parent) {
        if (parent.leaf) {
            return;
        }
        this.germplasmPedigreeService.getGermplasmTree(parent.data.id, 2, this.includeDerivativeLines)
            .subscribe((germplasmTreeNode: GermplasmTreeNode) => {
                this.addChildren(parent, germplasmTreeNode);
                this.redrawNodes();
            });
    }

    addChildren(parent: PrimeNgTreeNode, germplasmTreeNode: GermplasmTreeNode) {
        parent.children = [];
        const germplasmTreeNodeChildren = this.getChildren(germplasmTreeNode);
        if (germplasmTreeNodeChildren.length > 0) {
            parent.expanded = true;
            // Recursively add "grand" children nodes as well
            germplasmTreeNodeChildren.forEach((germplasmTreeNodeChild) => {
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

    getChildren(germplasmTreeNode: GermplasmTreeNode): GermplasmTreeNode[] {
        let children = [];
        if (germplasmTreeNode.femaleParentNode) {
            children.push(germplasmTreeNode.femaleParentNode);
        }
        if (germplasmTreeNode.maleParentNode) {
            children.push(germplasmTreeNode.maleParentNode);
        }
        if (germplasmTreeNode.otherProgenitors && germplasmTreeNode.otherProgenitors.length > 0) {
            children = children.concat(germplasmTreeNode.otherProgenitors);
        }
        return children;
    }

    hasChildren(germplasmTreeNode: GermplasmTreeNode): boolean {
        return germplasmTreeNode.femaleParentNode != null || germplasmTreeNode.maleParentNode != null
            || (germplasmTreeNode.otherProgenitors && germplasmTreeNode.otherProgenitors.length > 0);
    }

    private toPrimeNgNode(node: GermplasmTreeNode, parent?: PrimeNgTreeNode): PrimeNgTreeNode {
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
