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
import { GermplasmBasicDetailsAudit } from './germplasm-basic-details-audit.model';
import { GermplasmService } from '../../../shared/germplasm/service/germplasm.service';
import { GermplasmMerged } from '../../../shared/germplasm/model/merged-germplasm.model';

@Component({
    selector: 'jhi-germplasm-basic-details-audit',
    templateUrl: './basic-details-audit.component.html',
    styleUrls: [
        '../germplasm-audit.scss'
    ]
})
export class BasicDetailsAuditComponent implements OnInit {

    private readonly itemsPerPage: number = 10;

    gid: number;

    page: number;
    previousPage: number;
    totalItems: number;
    queryCount: number;
    isLoading: boolean;

    auditChanges: GermplasmBasicDetailsAudit[];

    getEventDate = getEventDate;
    getEventUser = getEventUser;

    germplasmMerged: GermplasmMerged[] = [];

    constructor(public activeModal: NgbActiveModal,
                private  germplasmAuditService: GermplasmAuditService,
                private  germplasmService: GermplasmService,
                private translateService: TranslateService,
                private jhiAlertService: JhiAlertService,
                private activatedRoute: ActivatedRoute,
                private router: Router) {
        this.gid = Number(this.activatedRoute.snapshot.queryParams.gid);
        this.page = 1;
    }

    ngOnInit(): void {
        this.loadAll();
        this.loadGermplasmMerged();
    }

    loadPage(page: number) {
        if (page !== this.previousPage) {
            this.previousPage = page;
            this.transition();
        }
    }

    loadGermplasmMerged() {
        this.germplasmService.getGermplasmMerged(this.gid).toPromise().then((germplasmMerged) => {
            this.germplasmMerged = germplasmMerged;
        });
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

    private loadAll() {
        this.isLoading = true;
        this. germplasmAuditService.getBasicDetailsChanges(this.gid, {
            page: this.page - 1,
            size: this.itemsPerPage
        }).pipe(finalize(() => {
            this.isLoading = false;
        })).subscribe(
            (res: HttpResponse<GermplasmBasicDetailsAudit[]>) => this.onSuccess(res.body, res.headers),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    private onSuccess(data: GermplasmBasicDetailsAudit[], headers) {
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
