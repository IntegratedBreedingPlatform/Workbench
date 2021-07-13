import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { TranslateService } from '@ngx-translate/core';
import { finalize } from 'rxjs/operators';
import { formatErrorList } from '../../../shared/alert/format-error-list';
import { JhiAlertService } from 'ng-jhipster';
import { GermplasmAuditService } from '../germplasm-audit.service';
import { getEventDate, getEventUser } from '../germplasm-audit-utils';
import { GermplasmProgenitorDetailsAudit } from './germplasm-progenitor-details-audit.model';
import { GermplasmDetailsUrlService } from '../../../shared/germplasm/service/germplasm-details.url.service';
import { BreedingMethodTypeEnum } from '../../../shared/breeding-method/model/breeding-method-type.model';

@Component({
    selector: 'jhi-germplasm-progenitors-details-audit',
    templateUrl: './details-audit.component.html',
    styleUrls: [
        '../germplasm-audit.scss'
    ]
})
export class DetailsAuditComponent implements OnInit {

    private readonly itemsPerPage: number = 10;

    gid: number;

    page: number;
    previousPage: number;
    totalItems: number;
    queryCount: number;
    isLoading: boolean;

    auditChanges: GermplasmProgenitorDetailsAudit[];

    getEventDate = getEventDate;
    getEventUser = getEventUser;

    constructor(public activeModal: NgbActiveModal,
                private  germplasmAuditService: GermplasmAuditService,
                private translateService: TranslateService,
                private jhiAlertService: JhiAlertService,
                private activatedRoute: ActivatedRoute,
                private router: Router,
                private germplasmDetailsUrlService: GermplasmDetailsUrlService) {
        this.gid = Number(this.activatedRoute.snapshot.queryParams.gid);
        this.page = 1;
    }

    ngOnInit(): void {
        this.loadAll();
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
            relativeTo: this.activatedRoute,
            queryParamsHandling: 'merge'
        });
        this.loadAll();
    }

    isGenerative(change: GermplasmProgenitorDetailsAudit): boolean {
        return change.breedingMethodType === BreedingMethodTypeEnum.GENERATIVE;
    }

    private loadAll() {
        this.isLoading = true;
        this. germplasmAuditService.getProgenitorDetailsChanges(this.gid, {
            page: this.page - 1,
            size: this.itemsPerPage
        }).pipe(finalize(() => {
            this.isLoading = false;
        })).subscribe(
            (res: HttpResponse<GermplasmProgenitorDetailsAudit[]>) => this.onSuccess(res.body, res.headers),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    private onSuccess(data: GermplasmProgenitorDetailsAudit[], headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.queryCount = this.totalItems;
        this.auditChanges = data;
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.jhiAlertService.addAlert({ msg: 'error.custom', type: 'danger', toast: false, params: { param: msg } }, null);
        } else {
            this.jhiAlertService.addAlert({ msg: 'error.general', type: 'danger', toast: false }, null);
        }
    }
}
