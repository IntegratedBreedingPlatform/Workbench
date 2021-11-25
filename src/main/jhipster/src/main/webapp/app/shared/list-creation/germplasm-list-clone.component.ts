import { Component } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { ParamContext } from '../service/param.context';
import { finalize } from 'rxjs/operators';
import { ListCreationComponent } from './list-creation.component';
import { NgbActiveModal, NgbCalendar, NgbDate, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TreeService } from '../tree/tree.service';
import { GermplasmTreeService } from '../tree/germplasm/germplasm-tree.service';
import { JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { AlertService } from '../alert/alert.service';
import { TreeDragDropService } from 'primeng/api';
import { GermplasmManagerContext } from '../../germplasm-manager/germplasm-manager.context';
import { Principal } from '..';
import { ListModel } from '../list-builder/model/list.model';
import { ListService } from './service/list.service';
import { GermplasmListService } from '../germplasm-list/service/germplasm-list.service';
import { GermplasmListMetadataRequest } from '../germplasm-list/model/germplasm-list-metadata-request.model';

@Component({
    selector: 'jhi-germplasm-list-clone',
    templateUrl: './list-creation.component.html',
    providers: [
        { provide: TreeService, useClass: GermplasmTreeService },
        { provide: ListService, useClass: GermplasmListService }
    ]
})
export class GermplasmListCloneComponent extends ListCreationComponent {

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
        public germplasmListService: GermplasmListService
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
        const germplasmList = this.germplasmManagerContext.sourceGermplasmList;
        this.model.type = germplasmList.type;
        this.model.notes = germplasmList.notes;
        this.model.description = germplasmList.description;
        const dateArray = germplasmList.creationDate.split('-');
        this.selectedDate = new NgbDate(parseInt(dateArray[0], 10), parseInt(dateArray[1], 10), parseInt(dateArray[2], 10));

        super.ngOnInit();
    }

    save() {
        const germplasmListMetadata = <GermplasmListMetadataRequest>({
            name: this.model.name,
            description: this.model.description,
            type: this.model.type,
            date: `${this.selectedDate.year}-${this.selectedDate.month}-${this.selectedDate.day}`,
            notes: this.model.notes,
            parentFolderId: this.selectedNode.data.id
        });

        this._isLoading = true;
        const persistPromise = this.persistTreeState();
        persistPromise.then(() => {
            this.germplasmListService.cloneGermplasmList(this.germplasmManagerContext.sourceGermplasmList.listId, germplasmListMetadata)
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

    onCloneSuccess(listModel: ListModel) {
        this.eventManager.broadcast({ name: 'clonedGermplasmList', content: listModel });
        this.modal.close();
    }

}
