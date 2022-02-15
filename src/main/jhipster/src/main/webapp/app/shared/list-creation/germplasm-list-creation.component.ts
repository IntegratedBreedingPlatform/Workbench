import { Component } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { ParamContext } from '../service/param.context';
import { finalize } from 'rxjs/operators';
import { ListCreationComponent } from './list-creation.component';
import { NgbActiveModal, NgbCalendar, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TreeService } from '../tree/tree.service';
import { GermplasmTreeService } from '../tree/germplasm/germplasm-tree.service';
import { JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { AlertService } from '../alert/alert.service';
import { TreeDragDropService } from 'primeng/api';
import { GermplasmManagerContext } from '../../germplasm-manager/germplasm-manager.context';
import { Principal } from '..';
import { ListModel } from '../list-builder/model/list.model';
import { ListService } from './service/list.service';
import { GermplasmListService } from '../germplasm-list/service/germplasm-list.service';

@Component({
    selector: 'jhi-germplasm-list-creation',
    templateUrl: './list-creation.component.html',
    providers: [
        { provide: TreeService, useClass: GermplasmTreeService },
        { provide: ListService, useClass: GermplasmListService }
    ]
})
export class GermplasmListCreationComponent extends ListCreationComponent {

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
        public context: ParamContext,
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

    save() {
        const listModel = <ListModel>({
            listName: this.model.listName,
            creationDate: `${this.creationDate.year}-${this.creationDate.month}-${this.creationDate.day}`,
            listType: this.model.listType,
            description: this.model.description,
            notes: this.model.notes,
            parentFolderId: this.selectedNodes[0].data.id
        });
        if (this.entries && this.entries.length) {
            listModel.entries = this.entries;
        } else {
            listModel.searchComposite = this.germplasmManagerContext.searchComposite;
        }
        this._isLoading = true;
        const persistPromise = this.persistTreeState();
        persistPromise.then(() => {
            this.listService.save(listModel)
                .pipe(finalize(() => {
                    this._isLoading = false;
                })).subscribe(
                (res) => this.onSaveSuccess(),
                (res: HttpErrorResponse) => this.onError(res)
            );
        });
    }

    get isLoading() {
        return this._isLoading;
    }

}
