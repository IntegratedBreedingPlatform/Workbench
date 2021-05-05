import { Component, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { SERVER_API_URL } from '../../app.constants';
import { ParamContext } from '../service/param.context';
import { map, finalize } from 'rxjs/operators';
import { GermplasmList, GermplasmListEntry } from '../model/germplasm-list';
import { SearchComposite } from '../model/search-composite';
import { GermplasmSearchRequest } from '../../entities/germplasm/germplasm-search-request.model';
import { ListType } from '../list-builder/model/list-type.model';
import { ListBuilderService } from './service/list-builder.service';
import { ListCreationComponent } from './list-creation.component';
import { ColumnLabels } from '../../germplasm-manager/germplasm-search.component';
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
import { HttpErrorResponse } from '@angular/common/http';
import { ListService } from './service/list.service';
import { GermplasmListService } from './service/germplasm-list.service';

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
            name: this.model.name,
            date: `${this.selectedDate.year}-${this.selectedDate.month}-${this.selectedDate.day}`,
            type: this.model.type,
            description: this.model.description,
            notes: this.model.notes,
            parentFolderId: this.selectedNode.data.id
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
