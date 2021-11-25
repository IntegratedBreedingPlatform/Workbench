import { Component } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { ParamContext } from '../service/param.context';
import { finalize } from 'rxjs/operators';
import { ListCreationComponent } from './list-creation.component';
import { NgbActiveModal, NgbCalendar, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TreeService } from '../tree/tree.service';
import { JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { AlertService } from '../alert/alert.service';
import { TreeDragDropService } from 'primeng/api';
import { GermplasmManagerContext } from '../../germplasm-manager/germplasm-manager.context';
import { Principal } from '..';
import { ListModel } from '../list-builder/model/list.model';
import { SampleTreeService } from '../../entities/sample/tree-table';
import { ListService } from './service/list.service';
import { SampleListService } from './service/sample-list.service';

@Component({
    selector: 'jhi-sample-list-creation',
    templateUrl: './list-creation.component.html',
    providers: [
        { provide: TreeService, useClass: SampleTreeService },
        { provide: ListService, useClass: SampleListService }
    ]
})
export class SampleListCreationComponent extends ListCreationComponent {

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
            parentFolderId: this.selectedNodes[0].data.id
        });
        listModel.entries = this.entries;
        this._isLoading = true;
        this.listService.save(listModel)
            .pipe(finalize(() => {
                this._isLoading = false;
            })).subscribe(
            (res) => this.onSaveSuccess(),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    get isLoading() {
        return this._isLoading;
    }

}
