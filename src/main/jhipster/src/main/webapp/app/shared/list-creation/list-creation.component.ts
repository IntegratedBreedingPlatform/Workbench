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
import { GERMPLASM_LIST_CREATED } from '../../app.events';

declare var $: any;

export abstract class ListCreationComponent extends TreeComponent implements OnInit {

    listTypes: ListType[];

    model = new ListModel();
    creationDate: NgbDate;

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
        super(true, 'single', treeService, modal, alertService, translateService, modalService);
        if (!this.paramContext.cropName) {
            this.paramContext.readParams();
        }
        this.creationDate = calendar.getToday();
    }

    abstract save();
    abstract get isLoading();

    ngOnInit(): void {
        this.principal.identity().then((account) => {
            this.loggedUserId = account.userId;
        });

        super.ngOnInit();

        this.listService.getListTypes().subscribe((listTypes) => this.listTypes = listTypes);
        if (!this.model.listType) {
            this.listService.getListType().subscribe((listType) => this.model.listType = listType);
        }
    }

    onNodeDrop(event, source: PrimeNgTreeNode, target: PrimeNgTreeNode) {
        this.onDragStart(event, source);
        this.onDrop(event, target);
    }

    isFormValid(f) {
        return f.form.valid && this.selectedNodes.length === 1 && !this.isLoading;
    }

    onSaveSuccess() {
        if ((<any>window.parent)) {
            (<any>window.parent).postMessage({ name: GERMPLASM_LIST_CREATED }, '*');
        }

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
}
