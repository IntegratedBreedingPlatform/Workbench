import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ParamContext } from '../../../shared/service/param.context';
import { AlertService } from '../../../shared/alert/alert.service';
import { JhiEventManager } from 'ng-jhipster';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../../../shared/alert/format-error-list';
import { EntryDetailsImportContext } from '../../../shared/ontology/entry-details-import.context';
import { EntryDetailsImportService } from '../../../shared/ontology/service/entry-details-import.service';

@Component({
    selector: 'jhi-study-entry-variable-matches.component',
    templateUrl: 'study-entry-variable-matches.component.html'
})
export class StudyEntryVariableMatchesComponent implements OnInit {
    HEADERS = HEADERS;

    page = 0;
    pageSize = 10;

    isLoading: boolean;

    rows = [];

    constructor(
        private translateService: TranslateService,
        private modal: NgbActiveModal,
        private modalService: NgbModal,
        private paramContext: ParamContext,
        private alertService: AlertService,
        private eventManager: JhiEventManager,
        private context: EntryDetailsImportContext,
        private importEntryDetailsService: EntryDetailsImportService
    ) {
    }

    ngOnInit() {
        this.rows = [];
        this.rows = this.importEntryDetailsService
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
