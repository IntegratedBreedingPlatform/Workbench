import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../shared/modal/popup.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
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
import { TreeDragDropService, TreeNodeDragEvent } from 'primeng/api';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';
import { Tree, UITreeNode } from 'primeng/primeng';

declare var $: any;

@Component({
    encapsulation: ViewEncapsulation.None,
    selector: 'jhi-germplasm-list-creation',
    templateUrl: './germplasm-list-creation.component.html',
    // TODO migrate IBP-4093
    styleUrls: ['../../../content/css/global-bs4.scss'],
    providers: [
        { provide: TreeService, useClass: GermplasmTreeTableService },
        { provide: TreeDragDropService, useClass: TreeDragDropService },
        { provide: GermplasmListService, useClass: GermplasmListService },
    ]
})
export class GermplasmListCreationComponent implements OnInit {

    @ViewChild(Tree) tree: Tree;

    public nodes: PrimeNgTreeNode[] = [];
    selectedNode: PrimeNgTreeNode;
    germplasmListTypes: GermplasmListType[];

    model = new GermplasmList();
    selectedDate: NgbDate;

    public mode: Mode = Mode.None;
    public Modes = Mode;
    public name: string; // rename or add item

    constructor(private modal: NgbActiveModal,
                private jhiLanguageService: JhiLanguageService,
                private translateService: TranslateService,
                private jhiAlertService: JhiAlertService,
                private paramContext: ParamContext,
                public treeService: TreeService,
                public treeDragDropService: TreeDragDropService,
                public germplasmListService: GermplasmListService,
                private germplasmManagerContext: GermplasmManagerContext,
                private calendar: NgbCalendar,
                private modalService: NgbModal) {
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

        // UITreeNode.prototype.onDropNodeDragOver = function (event) {
        //     console.log("onDropNodeDragOver");
        //     return;
        // };
        //
        // Tree.prototype.allowDrop = (dragNode: any, dropNode: any, dragNodeScope: any): boolean => {
        //     console.log('allowDrop');
        //
        //     return false;
        // }

        // this.treeDragDropService.dragStop$.subscribe((event: TreeNodeDragEvent) => {
        //     if (1 == 1) {
        //         // event.stopPropagation();
        //         // event.preventDefault();
        //     }
        //
        //     console.log('event' + event);
        //     return false;
        // });
    }

    private addNode(node: TreeNode) {
        return this.nodes.push(this.toPrimeNgNode(node));
    }

    onDrop(event, source: PrimeNgTreeNode, target: PrimeNgTreeNode) {
        console.log('onDrop');
        // event.accept();
        // event.originalEvent.stopPropagation();
        // event.originalEvent.preventDefault();
        // return;

        if (source.children && source.children.length !== 0) {
            this.jhiAlertService.error('bmsjHipsterApp.tree-table.messages.folder.cannot.move.has.children',
                { folder: source.data.name });
            return false;
        }
        // } else if (target.data.id === 'CROPLISTS' && !source.leaf) {
        //     this.jhiAlertService.error('bmsjHipsterApp.tree-table.messages.folder.move.to.crop.list.not.allowed');
        //     return;
        // } else if (target.leaf) {
        //     this.jhiAlertService.error('bmsjHipsterApp.tree-table.messages.folder.move.not.allowed');
        //     return;
        // }
        // this.treeService.move(source.data.id, target.data.id).subscribe((res) => {},
        //     (res: HttpErrorResponse) =>
        //         this.jhiAlertService.error('bmsjHipsterApp.tree-table.messages.error', { param: res.error.errors[0].message }));
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
        return f.form.valid && this.selectedNode;
    }

    isRootFolder() {
        return this.selectedNode.data.id === 'CROPLISTS' || this.selectedNode.data.id === 'LISTS';
    }

    setMode(mode: Mode, iconClickEvent) {
        if (this.isDisabled(iconClickEvent)) {
            return;
        }
        this.mode = mode;
        if (this.mode === Mode.Delete) {
            this.confirmDeleteFolderInTreeTable();
        } else {
            this.setName();
        }
    }

    setName() {
        if (this.mode === Mode.Add) {
            this.name = '';
        } else if (this.mode === Mode.Rename) {
            this.name = this.selectedNode.data.name;
        }
    }

    confirmDeleteFolderInTreeTable() {
        let message = '';
        if (this.selectedNode) {
            message = this.translateService.instant('bmsjHipsterApp.tree-table.messages.folder.delete.question',
                    {id: this.selectedNode.data.name});
        } else {
            return;
        }

        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.message = message;
        confirmModalRef.componentInstance.title = this.translateService.instant('bmsjHipsterApp.tree-table.action.folder.delete');

        confirmModalRef.result.then(() => {
            this.submitDeleteFolderInTreeTable();
        }, () => confirmModalRef.dismiss());
    }

    isDisabled(iconClickEvent) {
        return iconClickEvent.target.classList.contains('disable-image');
    }

    save() {
        const germplasmList = <GermplasmList>({
            name: this.model.name,
            date: `${this.selectedDate.year}-${this.selectedDate.month}-${this.selectedDate.day}`,
            type: this.model.type,
            description: this.model.description,
            notes: this.model.notes,
            parentFolderId: this.selectedNode.data.id,
            entries: this.germplasmManagerContext.itemIds.map((itemId) => {
                return <GermplasmListEntry>({ gid: itemId });
            })
        });
        this.germplasmListService.save(germplasmList).subscribe(
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

    private expand(parent, selectedId?: any) {
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
        console.log('toPrimeNgNode');
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
            selectable: node.isFolder,
            leaf: !node.isFolder,
            parent,
        };
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

    submitDeleteFolderInTreeTable() {
        this.mode = this.Modes.None;
        this.treeService.delete(this.selectedNode.data.id).subscribe(() => {
                this.expand(this.selectedNode.parent);
                this.jhiAlertService.success('bmsjHipsterApp.tree-table.messages.folder.delete.successfully');
            },
            (res: HttpErrorResponse) =>
                this.jhiAlertService.error('bmsjHipsterApp.tree-table.messages.error', { param: res.error.errors[0].message })
        );
    }

    submitAddOrRenameFolderInTreeTable() {
        if (this.mode === Mode.Add) {
            this.treeService.create(this.name, this.selectedNode.data.id).subscribe((res) => {
                    this.mode = this.Modes.None;
                    this.expand(this.selectedNode, res.id);
                    this.jhiAlertService.success('bmsjHipsterApp.tree-table.messages.folder.create.successfully');
                },
                (res: HttpErrorResponse) =>
                    this.jhiAlertService.error('bmsjHipsterApp.tree-table.messages.error', { param: res.error.errors[0].message }));
        } else if (this.mode === Mode.Rename) {
            this.treeService.rename(this.name, this.selectedNode.data.id).subscribe(() => {
                    this.mode = this.Modes.None;
                    this.selectedNode.data.name = this.name;
                    this.redrawNodes();
                    this.jhiAlertService.success('bmsjHipsterApp.tree-table.messages.folder.rename.successfully');
                },
                (res: HttpErrorResponse) =>
                    this.jhiAlertService.error('bmsjHipsterApp.tree-table.messages.error', { param: res.error.errors[0].message }));
        }
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

enum Mode {
    Add,
    Rename,
    Delete,
    None
}
