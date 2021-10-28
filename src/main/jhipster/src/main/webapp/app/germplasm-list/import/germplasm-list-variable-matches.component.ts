import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ParamContext } from '../../shared/service/param.context';
import { AlertService } from '../../shared/alert/alert.service';
import { ActivatedRoute } from '@angular/router';
import { JhiEventManager } from 'ng-jhipster';
import { GermplasmListImportContext } from './germplasm-list-import.context';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';
import { GermplasmListImportUpdateComponent } from './germplasm-list-import-update.component';
import { toUpper } from '../../shared/util/to-upper';
import { GermplasmListService } from '../../shared/germplasm-list/service/germplasm-list.service';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { ListComponent } from '../list.component';

@Component({
    selector: 'jhi-germplasm-list-variable-matches.component',
    templateUrl: 'germplasm-list-variable-matches.component.html'
})
export class GermplasmListVariableMatchesComponent implements OnInit {

    listId: number;

    HEADERS = HEADERS;

    page = 0;
    pageSize = 10;

    isLoading: boolean;
    isSaving: boolean;
    rows = [];
    variables: { [key: string]: any; } = {};

    constructor(
        private route: ActivatedRoute,
        private translateService: TranslateService,
        private modal: NgbActiveModal,
        private modalService: NgbModal,
        private paramContext: ParamContext,
        private alertService: AlertService,
        private germplasmListService: GermplasmListService,
        private eventManager: JhiEventManager,
        private context: GermplasmListImportContext
    ) {
    }

    ngOnInit() {
        this.listId = Number(this.route.snapshot.queryParamMap.get('listId'));
        this.rows = [];
        this.context.newVariables.forEach((variable) => {
            const variableName = variable.alias ? variable.alias : variable.name;
            const entryDetail = {
                id: variable.id, name: variableName,
                description: variable.description,
                exitsInlist: false
            };
            this.variables[toUpper(variableName)] = entryDetail;
            this.rows.push(entryDetail);
        });

        this.context.variablesOfTheList.forEach((variable) => {
            const variableName = variable.alias ? variable.alias : variable.name;
            const entryDetail = {
                id: variable.id,
                name: variableName, description: variable.description,
                exitsInlist: true
            };
            this.variables[toUpper(variableName)] = entryDetail;
            this.rows.push(entryDetail);
        });

        this.context.unknownVariableNames.forEach((variableName) => {
            const entryDetail = {
                id: null,
                name: variableName,
                description: '',
                exitsInlist: false
            };
            this.rows.push(entryDetail);
        });
    }

    back() {
        this.modal.close();
        this.modalService.open(GermplasmListImportUpdateComponent as Component, { size: 'lg', backdrop: 'static' });
    }

    dismiss() {
        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component, { backdrop: 'static' });
        confirmModalRef.componentInstance.message = this.translateService.instant('germplasm-list.import.cancel.confirm');
        confirmModalRef.result.then(() => this.modal.dismiss());
    }

    save() {

    }

}

export enum HEADERS {
    'ENTRY_NO' = 'ENTRY_NO',
    'ID' = 'ID',
    'NAME' = 'NAME',
    'DESCRIPTION' = 'DESCRIPTION'

}
