import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute, Router } from '@angular/router';
import { PopupService } from '../../../shared/modal/popup.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { RevisionType } from '../revision-type';
import { TranslateService } from '@ngx-translate/core';
import { finalize } from 'rxjs/operators';
import { formatErrorList } from '../../../shared/alert/format-error-list';
import { JhiAlertService } from 'ng-jhipster';
import { GermplasmAuditService } from '../germplasm-audit.service';
import { GermplasmAttributeAudit } from './germplasm-attribute-audit.model';
import { GermplasmAttributeContext } from '../../../entities/germplasm/attribute/germplasm-attribute.context';
import { parseDate } from '../../../shared/util/date-utils';

@Component({
    selector: 'jhi-germplasm-attribute-audit-modal',
    templateUrl: './germplasm-attribute-audit-modal.component.html',
    styleUrls: [
        '../germplasm-audit-modal.scss'
    ]
})
export class GermplasmAttributeAuditModalComponent implements OnInit {

    private readonly itemsPerPage: number = 10;

    gid: number;
    attributeId: number;
    title: string;

    page: number;
    previousPage: number;
    totalItems: number;
    queryCount: number;
    isLoading: boolean;

    germplasmAttributeAuditChanges: GermplasmAttributeAudit[];

    constructor(public activeModal: NgbActiveModal,
                private germplasmChangesService: GermplasmAuditService,
                private translateService: TranslateService,
                private germplasmAttributesContext: GermplasmAttributeContext,
                private jhiAlertService: JhiAlertService,
                private activatedRoute: ActivatedRoute,
                private router: Router) {
        this.page = 1;

        this.title = this.translateService.instant('audit.title',
            { entity: this.translateService.instant('audit.entities.attribute'), entityValue: this.germplasmAttributesContext.attribute.value });
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

    getEventDate(germplasmAttributeAudit: GermplasmAttributeAudit): string {
        const date: number = (germplasmAttributeAudit.revisionType ===  RevisionType.CREATION) ? germplasmAttributeAudit.createdDate : germplasmAttributeAudit.modifiedDate;
        return parseDate(date);
    }

    getUser(germplasmAttributeAudit: GermplasmAttributeAudit): string {
        return (germplasmAttributeAudit.revisionType ===  RevisionType.CREATION) ? germplasmAttributeAudit.createdBy : germplasmAttributeAudit.modifiedBy;
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
        this.germplasmAttributeAuditChanges = data;
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
    selector: 'jhi-germplasm-attribute-audit-popup',
    template: ``
})
export class GermplasmAttributeAuditPopupComponent implements OnInit {

    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }

    ngOnInit(): void {
        const modal = this.popupService.open(GermplasmAttributeAuditModalComponent as Component, { windowClass: 'modal-large', backdrop: 'static' });
        modal.then((modalRef) => {
            modalRef.componentInstance.gid = Number(this.route.snapshot.paramMap.get('gid'));
            modalRef.componentInstance.attributeId = Number(this.route.snapshot.paramMap.get('attributeId'));
        });

    }

}
