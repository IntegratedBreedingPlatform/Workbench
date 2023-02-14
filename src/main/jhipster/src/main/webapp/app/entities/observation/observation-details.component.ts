import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute, Router } from '@angular/router';
import { JhiAlertService, JhiLanguageService } from 'ng-jhipster';
import { VariableTypeEnum } from '../../shared/ontology/variable-type.enum';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { finalize } from 'rxjs/internal/operators/finalize';
import { DatasetService } from '../../shared/dataset/service/dataset.service';
import { PhenotypeAudit } from '../../shared/model/phenotype-audit.model';
import { ObservationVariable } from '../../shared/model/observation-variable.model';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { DateFormatEnum, formatDateToUTC } from '../../shared/util/date-utils';

@Component({
    selector: 'jhi-observation-details',
    templateUrl: './observation-details.component.html'
})
export class ObservationDetailsComponent implements OnInit {

    observationUnitId: string;
    cropName: number;
    programUUID: number;
    studyId: number;
    datasetId: number;
    editable = false;

    selectedVariable: number;
    studyVariables = [];
    VARIABLE_TYPE_IDS;

    isLoading: boolean;
    totalItems: number;
    queryCount: number;
    page: number;
    previousPage: number;

    private readonly itemsPerPage: number = 10;

    phenotypeAudits: PhenotypeAudit[];

    constructor(private route: ActivatedRoute,
                public activeModal: NgbActiveModal,
                private jhiAlertService: JhiAlertService,
                public datasetService: DatasetService,
                private jhiLanguageService: JhiLanguageService,
                private router: Router) {
    }

    ngOnInit(): void {
        this.page = 1;
        this.selectedVariable = 0;

        this.observationUnitId = this.route.snapshot.params['observationUnitId'];
        this.studyId = this.route.snapshot.queryParams['studyId'];
        this.datasetId = this.route.snapshot.queryParams['datasetId'];

        (<any>window).onCloseModal = this.clear;

        this.VARIABLE_TYPE_IDS = [VariableTypeEnum.TRAIT, VariableTypeEnum.SELECTION_METHOD, VariableTypeEnum.ENVIRONMENT_CONDITION];

        this.datasetService.getObservationSetColumns(this.studyId, this.datasetId)
            .subscribe(
                (resp: HttpResponse<ObservationVariable[]>) => {
                    this.studyVariables = resp.body.filter((variable: ObservationVariable) => !variable.factor)
                }
            );
    }

    loadAll() {
        this.isLoading = true;
        console.log(this.selectedVariable);

        const pagination: any = {
            page: this.page - 1,
            size: this.itemsPerPage
        }
        this.datasetService.getPhenotypeAuditRecords(this.studyId, this.datasetId, this.observationUnitId, this.selectedVariable, pagination)
            .pipe(finalize(() => {
                this.isLoading = false;
            }))
            .subscribe(
                (res: HttpResponse<PhenotypeAudit[]>) => this.onSuccess(res.body, res.headers),
                (res: HttpErrorResponse) => this.onError(res)
            );
    }

    private onSuccess(data: PhenotypeAudit[], headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.queryCount = this.totalItems;
        this.phenotypeAudits = data;
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.jhiAlertService.addAlert({ msg: 'error.custom', type: 'danger', toast: false, params: { param: msg } }, null);
        } else {
            this.jhiAlertService.addAlert({ msg: 'error.general', type: 'danger', toast: false }, null);
        }
    }

    resetTable() {
        this.page = 1;
        this.previousPage = 1;
        this.loadAll();
    }

    clear() {
        this.activeModal.dismiss('cancel');
        if ((<any>window.parent).closeModal) {
            (<any>window.parent).closeModal();
        }
    }

    loadPage(page: number) {
        if (page !== this.previousPage) {
            this.previousPage = page;
            this.transition();
        }
    }

    transition() {
        this.router.navigate(['./'], {
            queryParams:
                {
                    page: this.page,
                    size: this.itemsPerPage
                },
            relativeTo: this.route,
            queryParamsHandling: 'merge'
        });
        this.loadAll();
    }

    formatDate(date: string): string {
        return formatDateToUTC(date, DateFormatEnum.ISO_8601_AND_TIME) + ' UTC';
    }

}

