import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../shared/modal/popup.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { JhiLanguageService } from 'ng-jhipster';
import { TreeService } from '../../shared/tree/tree.service';
import { TreeNode } from '../../shared/tree';
import { TreeNode as PrimeNgTreeNode } from 'primeng/components/common/treenode';
import { GermplasmTreeTableService } from '../../shared/tree/germplasm/germplasm-tree-table.service';
import { ParamContext } from '../../shared/service/param.context';
import { map } from 'rxjs/operators';
import { GermplasmList } from '../../shared/model/germplasm-list';
import { GermplasmListType } from './germplasm-list-type.model';
import { GermplasmListService } from './germplasm-list.service';

declare var $: any;

@Component({
    encapsulation: ViewEncapsulation.None,
    selector: 'jhi-germplasm-list-creation',
    templateUrl: './germplasm-list-creation.component.html',
    // TODO migrate IBP-4093
    styleUrls: ['../../../content/css/global-bs4.scss'],
    providers: [
        { provide: TreeService, useClass: GermplasmTreeTableService },
        { provide: GermplasmListService, useClass: GermplasmListService },
    ]
})
export class GermplasmListCreationComponent implements OnInit {

    public nodes: PrimeNgTreeNode[] = [];
    selectedNode: PrimeNgTreeNode;
    model = new GermplasmList();
    germplasmListTypes: GermplasmListType[];

    constructor(private modal: NgbActiveModal,
                private jhiLanguageService: JhiLanguageService,
                private translateService: TranslateService,
                private paramContext: ParamContext,
                public service: TreeService,
                public germplasmListService: GermplasmListService) {
        if (!this.paramContext.cropName) {
            this.paramContext.readParams();
        }
    }

    ngOnInit(): void {
        this.service.expand('')
            // .pipe(map((res: TreeNode[]) => res.filter((node) => node.isFolder)))
            .subscribe((res: TreeNode[]) => {
                res.forEach((node) => this.addNode(node));
                this.redrawNodes();
                // FIXME tableStyleClass not working on primeng treetable 6?
                $('.ui-treetable-table').addClass('table table-striped table-bordered table-curved');
                this.nodes.forEach((parent) => {
                    this.expand(parent);
                    parent.expanded = true;
                });
            });

        this.germplasmListService.getGermplasmListTypes().toPromise().then((germplasmListTypes) => {
            this.germplasmListTypes = germplasmListTypes;
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

    save() {
        // const selected = this.selectedNodes.filter((node: PrimeNgTreeNode) => node.leaf)
        //     .map((node: PrimeNgTreeNode) => {
        //         return {
        //             id: node.data.id,
        //             name: node.data.name
        //         };
        //     });
        this.modal.close();
    }

    dismiss() {
        this.modal.dismiss();
    }

    private expand(parent) {
        if (parent.leaf) {
            return;
        }
        this.service.expand(parent.data.id)
            // .pipe(map((res: TreeNode[]) => res.filter((node) => node.isFolder)))
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
            // draggable: node.isFolder,
            // droppable: node.isFolder,
            selectable: node.isFolder,
            leaf: !node.isFolder,
            parent,
        };
    }
}

@Component({
    selector: 'jhi-germplasm-list-creation-popup',
    template: ''
})
export class GermplasmListCreationPopupComponent implements OnInit {
    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }

    ngOnInit(): void {
        const modal = this.popupService.open(GermplasmListCreationComponent as Component);
    }

}
