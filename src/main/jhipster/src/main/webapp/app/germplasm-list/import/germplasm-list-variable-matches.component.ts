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
import { GermplasmListImportComponent } from './germplasm-list-import.component';
import { GermplasmListImportReviewComponent } from './germplasm-list-import-review.component';

@Component({
    selector: 'jhi-germplasm-list-variable-matches.component',
    templateUrl: 'germplasm-list-variable-matches.component.html'
})
export class GermplasmListVariableMatchesComponent implements OnInit {
    HEADERS = HEADERS;

    page = 0;
    pageSize = 10;

    isLoading: boolean;
    isSaving: boolean;

    rows = [];
    variableMatchesResult: any = {};

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
        this.rows = [];
        this.context.newVariables.forEach((variable) => {
            const variableName = variable.alias ? variable.alias : variable.name;
            const row = {
                id: variable.id, name: variableName,
                description: variable.description,
                existsInlist: false
            };

            if (variable.alias) {
                this.variableMatchesResult[toUpper(variable.alias)] = variable.id;
            }
            this.variableMatchesResult[toUpper(variable.name)] = variable.id;
            this.rows.push(row);
        });

        this.context.variablesOfTheList.forEach((variable) => {
            const variableName = variable.alias ? variable.alias : variable.name;
            const row = {
                id: variable.id,
                name: variableName, description: variable.description,
                existsInlist: true
            };

            if (variable.alias) {
                this.variableMatchesResult[toUpper(variable.alias)] = variable.id;
            }
            this.variableMatchesResult[toUpper(variable.name)] = variable.id;
            this.rows.push(row);
        });

        this.context.unknownVariableNames.forEach((variableName) => {
            const row = {
                id: null,
                name: variableName,
                description: '',
                existsInlist: false
            };
            this.rows.push(row);
        });
    }

    back() {
        this.modal.close();
    }

    dismiss() {
        this.modal.dismiss();
    }

    next() {
        this.modal.close(this.variableMatchesResult);
    }

    save() {
        this.modal.close(this.variableMatchesResult);
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general', null, null);
        }
    }

}

export enum HEADERS {
    'ENTRY_NO' = 'ENTRY_NO',
    'ID' = 'ID',
    'NAME' = 'NAME',
    'DESCRIPTION' = 'DESCRIPTION'

}
