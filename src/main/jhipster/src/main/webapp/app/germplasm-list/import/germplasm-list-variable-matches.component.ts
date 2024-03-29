import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ParamContext } from '../../shared/service/param.context';
import { AlertService } from '../../shared/alert/alert.service';
import { ActivatedRoute } from '@angular/router';
import { JhiEventManager } from 'ng-jhipster';
import { toUpper } from '../../shared/util/to-upper';
import { GermplasmListService } from '../../shared/germplasm-list/service/germplasm-list.service';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { EntryDetailsImportContext } from '../../shared/ontology/entry-details-import.context';
import { EntryDetailsImportService } from '../../shared/ontology/service/entry-details-import.service';

@Component({
    selector: 'jhi-germplasm-list-variable-matches.component',
    templateUrl: 'germplasm-list-variable-matches.component.html'
})
export class GermplasmListVariableMatchesComponent implements OnInit {
    HEADERS = HEADERS;

    page = 0;
    pageSize = 10;

    isLoading: boolean;

    rows = [];

    constructor(
        private route: ActivatedRoute,
        private translateService: TranslateService,
        private modal: NgbActiveModal,
        private modalService: NgbModal,
        private paramContext: ParamContext,
        private alertService: AlertService,
        private germplasmListService: GermplasmListService,
        private eventManager: JhiEventManager,
        private context: EntryDetailsImportContext,
        private entryDetailsImportService: EntryDetailsImportService
    ) {
    }

    ngOnInit() {
        this.rows = [];
        this.rows = this.entryDetailsImportService
            .initializeVariableMatches();
    }

    back() {
        this.modal.close();
    }

    dismiss() {
        this.modal.dismiss();
    }

    next() {
        this.modal.close(this.context.variableMatchesResult);
    }

    save() {
        this.modal.close(this.context.variableMatchesResult);
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
    'ID' = 'ID',
    'NAME' = 'NAME',
    'DESCRIPTION' = 'DESCRIPTION'

}
