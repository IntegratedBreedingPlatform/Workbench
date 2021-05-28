import { Component, OnInit } from '@angular/core';
import { TreeService } from '../tree/tree.service';
import { TreeDragDropService, TreeNode as PrimeNgTreeNode } from 'primeng/api';
import { NgbActiveModal, NgbCalendar, NgbDate, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { AlertService } from '../alert/alert.service';
import { ParamContext } from '../service/param.context';
import { GermplasmManagerContext } from '../../germplasm-manager/germplasm-manager.context';
import { Principal } from '../index';
import { TreeComponent, TreeNode } from '../tree';
import { HttpErrorResponse } from '@angular/common/http';
import { ModalConfirmComponent } from '../modal/modal-confirm.component';
import { formatErrorList } from '../alert/format-error-list';
import { ListEntry, ListModel } from '../list-builder/model/list.model';
import { ListType } from '../list-builder/model/list-type.model';
import { ListService } from './service/list.service';

declare var $: any;

export abstract class ListCreationComponent extends TreeComponent implements OnInit {

    readonly NAME_MAX_LENGTH: number = 50;

    selectedNode: PrimeNgTreeNode;
    listTypes: ListType[];

    model = new ListModel();
    selectedDate: NgbDate;

    public mode: FolderMode = FolderMode.None;
    public FolderModes = FolderMode;
    public name: string; // rename or add item

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
        super(treeService, modal);
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
        this.listService.getListType().subscribe((listType) => this.model.type = listType);
    }

    onDrop(event, source: PrimeNgTreeNode, target: PrimeNgTreeNode) {
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

        if (target.data.id === 'CROPLISTS' && !source.leaf) {
            this.alertService.error('bmsjHipsterApp.tree-table.messages.folder.move.program.to.crop.list.not.allowed');
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
        return f.form.valid && this.selectedNode && !this.isLoading;
    }

    isRootFolder() {
        return this.selectedNode.data.id === 'CROPLISTS' || this.selectedNode.data.id === 'LISTS';
    }

    setMode(mode: FolderMode, iconClickEvent) {
        if (this.isDisabled(iconClickEvent)) {
            return;
        }
        this.mode = mode;
        if (this.mode === FolderMode.Delete) {
            this.validateDeleteFolder();
        } else {
            this.setName();
        }
    }

    setName() {
        if (this.mode === FolderMode.Add) {
            this.name = '';
        } else if (this.mode === FolderMode.Rename) {
            this.name = this.selectedNode.data.name;
        }
    }

    validateDeleteFolder() {
        if (this.selectedNode.children && this.selectedNode.children.length !== 0) {
            this.alertService.error('bmsjHipsterApp.tree-table.messages.folder.cannot.delete.has.children',
                { folder: this.selectedNode.data.name });
            return;
        }

        if (this.loggedUserId && this.loggedUserId.toString() !== this.selectedNode.data.ownerId) {
            this.alertService.error('bmsjHipsterApp.tree-table.messages.folder.delete.not.owner');
            return;
        }

        this.confirmDeleteFolder();
    }

    confirmDeleteFolder() {
        let message = '';
        if (this.selectedNode) {
            message = this.translateService.instant('bmsjHipsterApp.tree-table.messages.folder.delete.question',
                { id: this.selectedNode.data.name });
        } else {
            return;
        }

        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.message = message;
        confirmModalRef.componentInstance.title = this.translateService.instant('bmsjHipsterApp.tree-table.action.folder.delete');

        confirmModalRef.result.then(() => {
            this.submitDeleteFolder();
        }, () => confirmModalRef.dismiss());
    }

    isDisabled(iconClickEvent) {
        return iconClickEvent.target.classList.contains('disable-image');
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

    submitDeleteFolder() {
        this.mode = this.FolderModes.None;
        this.treeService.delete(this.selectedNode.data.id).subscribe(() => {
                super.expand(this.selectedNode.parent);
                this.alertService.success('bmsjHipsterApp.tree-table.messages.folder.delete.successfully');
            },
            (res: HttpErrorResponse) =>
                this.alertService.error('bmsjHipsterApp.tree-table.messages.error', { param: res.error.errors[0].message })
        );
    }

    submitAddOrRenameFolder() {
        if (this.name.length > this.NAME_MAX_LENGTH) {
            this.alertService.error('bmsjHipsterApp.tree-table.messages.folder.name.too.long', { length: this.NAME_MAX_LENGTH })
            return;
        }

        if (this.mode === FolderMode.Add) {
            const isParentCropList = this.isParentCropList(this.selectedNode);
            this.treeService.create(this.name, this.selectedNode.data.id, isParentCropList).subscribe((res) => {
                    this.mode = this.FolderModes.None;
                    this.expand(this.selectedNode);
                    this.alertService.success('bmsjHipsterApp.tree-table.messages.folder.create.successfully');
                },
                (res: HttpErrorResponse) =>
                    this.alertService.error('bmsjHipsterApp.tree-table.messages.error', { param: res.error.errors[0].message }));
        }

        if (this.mode === FolderMode.Rename) {
            this.treeService.rename(this.name, this.selectedNode.data.id).subscribe(() => {
                    this.mode = this.FolderModes.None;
                    this.selectedNode.data.name = this.name;
                    this.redrawNodes();
                    this.alertService.success('bmsjHipsterApp.tree-table.messages.folder.rename.successfully');
                },
                (res: HttpErrorResponse) =>
                    this.alertService.error('bmsjHipsterApp.tree-table.messages.error', { param: res.error.errors[0].message }));
        }
    }

    isParentCropList(node: PrimeNgTreeNode): boolean {
        if (node.parent) {
            return this.isParentCropList(node.parent);
        }
        return node.data.id === 'CROPLISTS';
    }

    isSelectable(node: TreeNode) {
        return node.isFolder;
    }

}

enum FolderMode {
    Add,
    Rename,
    Delete,
    None
}
