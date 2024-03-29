import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { ParamContext } from '../service/param.context';
import { finalize } from 'rxjs/operators';
import { ListCreationComponent } from './list-creation.component';
import { NgbActiveModal, NgbCalendar, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TreeService } from '../tree/tree.service';
import { GermplasmTreeService } from '../tree/germplasm/germplasm-tree.service';
import { JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { AlertService } from '../alert/alert.service';
import { TreeDragDropService } from 'primeng/api';
import { GermplasmManagerContext } from '../../germplasm-manager/germplasm-manager.context';
import { Principal } from '..';
import { ListService } from './service/list.service';
import { GermplasmListService } from '../germplasm-list/service/germplasm-list.service';
import { Router } from '@angular/router';
import { GermplasmListModel } from '../germplasm-list/model/germplasm-list.model';
import { DateHelperService } from '../service/date.helper.service';
import { GermplasmListManagerContext } from '../../germplasm-list/germplasm-list-manager.context';

@Component({
    selector: 'jhi-germplasm-list-clone',
    templateUrl: './list-creation.component.html',
    providers: [
        { provide: TreeService, useClass: GermplasmTreeService },
        { provide: ListService, useClass: GermplasmListService }
    ]
})
export class GermplasmListCloneComponent extends ListCreationComponent implements OnInit {

    _isLoading: boolean;

    constructor(
        public modal: NgbActiveModal,
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
        public principal: Principal,
        public http: HttpClient,
        public eventManager: JhiEventManager,
        public germplasmListService: GermplasmListService,
        private router: Router,
        private dateHelperService: DateHelperService,
        private germplasmListManagerContext: GermplasmListManagerContext
    ) {
        super(
            modal,
            jhiLanguageService,
            translateService,
            alertService,
            paramContext,
            treeService,
            treeDragDropService,
            listService,
            germplasmManagerContext,
            calendar,
            modalService,
            principal,
        );
    }

    ngOnInit() {
        this.listService.getById(this.germplasmListManagerContext.activeGermplasmListId).subscribe((listModel) => {
            this.model = listModel;
            this.model.listName = null;
            this.creationDate =  this.dateHelperService.convertFormattedDateStringToNgbDate(listModel.creationDate, 'yyyy-mm-dd');
        });

        super.ngOnInit();
    }

    save() {
        const germplasmList = <GermplasmListModel>({
            listName: this.model.listName,
            creationDate: `${this.creationDate.year}-${this.creationDate.month}-${this.creationDate.day}`,
            description: this.model.description,
            listType: this.model.listType,
            notes: this.model.notes,
            parentFolderId: this.selectedNodes[0].data.id
        });

        this._isLoading = true;
        const persistPromise = this.persistTreeState();
        persistPromise.then(() => {
            this.germplasmListService.cloneGermplasmList(this.germplasmListManagerContext.activeGermplasmListId, germplasmList)
                .pipe(finalize(() => {
                    this._isLoading = false;
                })).subscribe(
                (res) => this.onCloneSuccess(res),
                (res: HttpErrorResponse) => this.onError(res)
            );
        });
    }

    get isLoading() {
        return this._isLoading;
    }

    async onCloneSuccess(listModel: GermplasmListModel) {
        await this.router.navigate([`/germplasm-list/list/${listModel.listId}`], {
            queryParams: {
                listId: listModel.listId,
                listName: listModel.listName
            }
        });
        this.eventManager.broadcast({ name: 'clonedGermplasmList', content: '' });
        this.alertService.success('germplasm-list.list-data.clone-list.success');
        this.modal.close();
    }

}
