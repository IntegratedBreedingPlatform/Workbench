import { OnInit } from '@angular/core';
import { TreeService } from '../tree/tree.service';
import { TreeDragDropService, TreeNode as PrimeNgTreeNode } from 'primeng/api';
import { NgbActiveModal, NgbCalendar, NgbDate, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { AlertService } from '../alert/alert.service';
import { ParamContext } from '../service/param.context';
import { GermplasmManagerContext } from '../../germplasm-manager/germplasm-manager.context';
import { Principal } from '../index';
import { Mode, TreeComponent, TreeNode } from '../tree';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../alert/format-error-list';
import { ListEntry, ListModel } from '../list-builder/model/list.model';
import { ListType } from '../list-builder/model/list-type.model';
import { ListService } from './service/list.service';

declare var $: any;

export abstract class ListCreationComponent extends TreeComponent implements OnInit {

    listTypes: ListType[];

    model = new ListModel();
    selectedDate: NgbDate;

    public FolderModes = Mode;

    private loggedUserId: number;

    // for import process
    entries: ListEntry[];

    constructor(public modal: NgbActiveModal,
                public jhiLanguageService: JhiLanguageService,
                public translateService: TranslateService,
                public alertService: AlertService,
                public paramContext: ParamContext,
                public treeService: TreeService,
                public treeDragDropService: TreeDragDropService,
                public listService: ListService,
                public germplasmManagerContext: GermplasmManagerContext,
                public calendar: NgbCalendar,
                public modalService: NgbModal,
                public principal: Principal) {
        super(true, treeService, modal, alertService, translateService, modalService);
        if (!this.paramContext.cropName) {
            this.paramContext.readParams();
        }
        this.selectedDate = calendar.getToday();
    }

    abstract save();
    abstract get isLoading();

    ngOnInit(): void {
        this.principal.identity().then((account) => {
            this.loggedUserId = account.userId;
        });

        super.ngOnInit();

        this.listService.getListTypes().subscribe((listTypes) => this.listTypes = listTypes);
        if (!this.model.type) {
            this.listService.getListType().subscribe((listType) => this.model.type = listType);
        }
    }

    onNodeDrop(event, source: PrimeNgTreeNode, target: PrimeNgTreeNode) {
        // Prevent to move source on same parent folder
        if (source.parent.data.id === target.data.id) {
            return;
        }

        // Prevent to move source if parent has a child with same name as the source
        if (event.dropNode.children && event.dropNode.children.find((node) => node.data.name === source.data.name)) {
            this.alertService.error('bmsjHipsterApp.tree-table.messages.folder.move.parent.duplicated.name');
            return;
        }

        if (source.children && source.children.length !== 0) {
            this.alertService.error('bmsjHipsterApp.tree-table.messages.folder.cannot.move.has.children',
                { folder: source.data.name });
            return;
        }

        if (target.leaf) {
            this.alertService.error('bmsjHipsterApp.tree-table.messages.folder.move.not.allowed');
            return;
        }

        event.accept();

        const isParentCropList = this.isParentCropList(target);
        this.treeService.move(source.data.id, target.data.id, isParentCropList).subscribe(
            (res) => {},
            (res: HttpErrorResponse) => {
                // TODO: FIX ME! Due to primeng7 does not support accepting the event within subscribe, we are handling the re-render of the component by calling the expand method.
                // Check issue reported: https://github.com/primefaces/primeng/issues/7386
                this.expand(source.parent);
                this.expand(target);
                this.alertService.error('bmsjHipsterApp.tree-table.messages.error', { param: res.error.errors[0].message })
            });
    }

    isFormValid(f) {
        return f.form.valid && this.selectedNodes.length === 1 && !this.isLoading;
    }

    onSaveSuccess() {
        this.alertService.success('germplasm-list-creation.success');
        this.modal.close();
    }

    onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general', null, null);
        }
    }

    isSelectable(node: TreeNode) {
        return node.isFolder;
    }

    set selectedNode(node: PrimeNgTreeNode) {
        this.selectedNodes[0] = node;
    }

    get selectedNode(): PrimeNgTreeNode {
        return this.selectedNodes[0];
    }

}
