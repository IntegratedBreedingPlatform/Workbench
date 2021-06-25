import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute, Router } from '@angular/router';
import { PopupService } from '../../../shared/modal/popup.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { TranslateService } from '@ngx-translate/core';
import { finalize } from 'rxjs/operators';
import { formatErrorList } from '../../../shared/alert/format-error-list';
import { JhiAlertService } from 'ng-jhipster';
import { GermplasmAuditService } from '../germplasm-audit.service';
import { GermplasmAttributeAudit } from './germplasm-attribute-audit.model';
import { GermplasmAttributeContext } from '../../../entities/germplasm/attribute/germplasm-attribute.context';
import { getEventDate, getEventUser } from '../germplasm-audit-utils';

@Component({
    selector: 'jhi-germplasm-attribute-audit',
    templateUrl: './germplasm-attribute-audit.component.html',
    styleUrls: [
        '../germplasm-audit.scss'
    ]
})
export class GermplasmAttributeAuditComponent implements OnInit, OnDestroy {

    private readonly itemsPerPage: number = 10;

    gid: number;
    attributeId: number;
    title: string;

    page: number;
    previousPage: number;
    totalItems: number;
    queryCount: number;
    isLoading: boolean;

    auditChanges: GermplasmAttributeAudit[];

    getEventDate = getEventDate;
    getEventUser = getEventUser;

    constructor(public activeModal: NgbActiveModal,
                private germplasmChangesService: GermplasmAuditService,
                private translateService: TranslateService,
                private germplasmAttributeContext: GermplasmAttributeContext,
                private jhiAlertService: JhiAlertService,
                private activatedRoute: ActivatedRoute,
                private router: Router) {
        this.page = 1;

        const entity = this.germplasmAttributeContext.attributeType.toLowerCase();
        this.title = this.translateService.instant('audit.title',
            { entity: this.translateService.instant('audit.entities.' + entity), entityValue: this.germplasmAttributeContext.attribute.value });
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
        this.router.navigate(['germplasm/:gid/attribute/:attributeId/audit-dialog'], {
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
        this.germplasmChangesService.getAttributesChanges(this.gid, this.attributeId, {
            page: this.page - 1,
            size: this.itemsPerPage
        }).pipe(finalize(() => {
            this.isLoading = false;
        })).subscribe(
            (res: HttpResponse<GermplasmAttributeAudit[]>) => this.onSuccess(res.body, res.headers),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    private onSuccess(data: GermplasmAttributeAudit[], headers) {
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

    ngOnDestroy(): void {
        this.germplasmAttributeContext.attributeType = null;
        this.germplasmAttributeContext.attribute = null;
    }
}

@Component({
    selector: 'jhi-germplasm-attribute-audit-popup',
    template: ``
})
export class GermplasmAttributeAuditPopupComponent implements OnInit {

    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }

    ngOnInit(): void {
        const modal = this.popupService.open(GermplasmAttributeAuditComponent as Component, { windowClass: 'modal-large', backdrop: 'static' });
        modal.then((modalRef) => {
            modalRef.componentInstance.gid = Number(this.route.snapshot.paramMap.get('gid'));
            modalRef.componentInstance.attributeId = Number(this.route.snapshot.paramMap.get('attributeId'));
        });

    }

}
