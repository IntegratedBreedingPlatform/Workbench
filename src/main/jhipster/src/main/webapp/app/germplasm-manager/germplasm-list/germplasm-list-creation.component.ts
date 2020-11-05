import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../shared/modal/popup.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { JhiAlertService, JhiLanguageService } from 'ng-jhipster';
import { TreeService } from '../../shared/tree/tree.service';
import { TreeNode } from '../../shared/tree';
import { TreeNode as PrimeNgTreeNode } from 'primeng/components/common/treenode';
import { GermplasmTreeTableService } from '../../shared/tree/germplasm/germplasm-tree-table.service';
import { ParamContext } from '../../shared/service/param.context';
import { GermplasmList, GermplasmListEntry } from '../../shared/model/germplasm-list';
import { GermplasmListType } from './germplasm-list-type.model';
import { GermplasmListService } from './germplasm-list.service';
import { GermplasmManagerContext } from '../germplasm-manager.context';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { HttpErrorResponse } from '@angular/common/http';
import { NgbCalendar } from '@ng-bootstrap/ng-bootstrap';
import { NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { finalize } from 'rxjs/internal/operators/finalize';

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
    germplasmListTypes: GermplasmListType[];

    model = new GermplasmList();
    selectedDate: NgbDate;

    isLoading: boolean;

    constructor(private modal: NgbActiveModal,
                private jhiLanguageService: JhiLanguageService,
                private translateService: TranslateService,
                private jhiAlertService: JhiAlertService,
                private paramContext: ParamContext,
                public treeService: TreeService,
                public germplasmListService: GermplasmListService,
                private germplasmManagerContext: GermplasmManagerContext,
                private calendar: NgbCalendar) {
        if (!this.paramContext.cropName) {
            this.paramContext.readParams();
        }
        this.selectedDate = calendar.getToday();
    }

    ngOnInit(): void {
        this.treeService.expand('')
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
            this.model.type = 'LST';
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

    isFormValid(f) {
        return f.form.valid && this.selectedNode && !this.isLoading;
    }

    save() {
        const germplasmList = <GermplasmList>({
            name: this.model.name,
            date: `${this.selectedDate.year}-${this.selectedDate.month}-${this.selectedDate.day}`,
            type: this.model.type,
            description: this.model.description,
            notes: this.model.notes,
            parentFolderId: this.selectedNode.data.id,
            searchComposite: this.germplasmManagerContext.searchComposite
        });
        this.isLoading = true;
        this.germplasmListService.save(germplasmList)
            .pipe(finalize(() => {
                this.isLoading = false;
            })).subscribe(
            (res: GermplasmList) => this.onSaveSuccess(res),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    private onSaveSuccess(res: GermplasmList) {
        this.jhiAlertService.success('germplasm-list-creation.success');
        this.modal.close();
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.jhiAlertService.error('error.custom', { param: msg });
        } else {
            this.jhiAlertService.error('error.general', null, null);
        }
    }

    dismiss() {
        this.modal.dismiss();
    }

    private expand(parent) {
        if (parent.leaf) {
            return;
        }
        this.treeService.expand(parent.data.id)
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
