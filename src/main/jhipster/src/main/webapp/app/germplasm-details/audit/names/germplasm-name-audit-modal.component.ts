import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute, Router } from '@angular/router';
import { PopupService } from '../../../shared/modal/popup.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { TranslateService } from '@ngx-translate/core';
import { GermplasmNameContext } from '../../../entities/germplasm/name/germplasm-name.context';
import { finalize } from 'rxjs/operators';
import { formatErrorList } from '../../../shared/alert/format-error-list';
import { JhiAlertService } from 'ng-jhipster';
import { GermplasmAuditService } from '../germplasm-audit.service';
import { GermplasmNameAudit } from './germplasm-name-audit.model';
import { getEventDate, getEventUser } from '../germplasm-audit-utils';

@Component({
    selector: 'jhi-germplasm-name-audit-modal',
    templateUrl: './germplasm-name-audit-modal.component.html',
    styleUrls: [
        '../germplasm-audit-modal.scss'
    ]
})
export class GermplasmNameAuditModalComponent implements OnInit {

    private readonly itemsPerPage: number = 10;

    gid: number;
    nameId: number;
    title: string;

    page: number;
    previousPage: number;
    totalItems: number;
    queryCount: number;
    isLoading: boolean;

    germplasmNameAuditChanges: GermplasmNameAudit[];

    getEventDate = getEventDate;
    getEventUser = getEventUser;

    constructor(public activeModal: NgbActiveModal,
                private germplasmChangesService: GermplasmAuditService,
                private translateService: TranslateService,
                private germplasmNameContext: GermplasmNameContext,
                private jhiAlertService: JhiAlertService,
                private activatedRoute: ActivatedRoute,
                private router: Router) {
        this.page = 1;

        this.title = this.translateService.instant('audit.title',
            { entity: this.translateService.instant('audit.entities.name'), entityValue: this.germplasmNameContext.germplasmName.name });
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
        this.router.navigate(['germplasm/:gid/name/:nameId/audit-dialog'], {
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

    dismiss() {
        this.activeModal.dismiss('cancel');
    }

    private loadAll() {
        this.isLoading = true;
        this.germplasmChangesService.getNamesChanges(this.gid, this.nameId, {
            page: this.page - 1,
            size: this.itemsPerPage
        }).pipe(finalize(() => {
            this.isLoading = false;
        })).subscribe(
            (res: HttpResponse<GermplasmNameAudit[]>) => this.onSuccess(res.body, res.headers),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    private onSuccess(data: GermplasmNameAudit[], headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.queryCount = this.totalItems;
        this.germplasmNameAuditChanges = data;
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

@Component({
    selector: 'jhi-germplasm-name-audit-popup',
    template: ``
})
export class GermplasmNameAuditPopupComponent implements OnInit {

    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }

    ngOnInit(): void {
        const modal = this.popupService.open(GermplasmNameAuditModalComponent as Component, { windowClass: 'modal-large', backdrop: 'static' });
        modal.then((modalRef) => {
            modalRef.componentInstance.gid = Number(this.route.snapshot.paramMap.get('gid'));
            modalRef.componentInstance.nameId = Number(this.route.snapshot.paramMap.get('nameId'));
        });

    }

}
