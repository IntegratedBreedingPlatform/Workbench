import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute, Router } from '@angular/router';
import { PopupService } from '../../shared/modal/popup.service';
import { GermplasmChangesService } from './germplasm-changes.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { GermplasmNameChange } from './germplasm-name-changes.model';
import { RevisionType } from './revision-type';
import { TranslateService } from '@ngx-translate/core';
import { GermplasmNameContext } from '../../entities/germplasm/name/germplasm-name.context';
import { finalize } from 'rxjs/operators';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { JhiAlertService } from 'ng-jhipster';

@Component({
    selector: 'jhi-germplasm-name-changes-modal',
    templateUrl: './germplasm-name-changes-modal.component.html'
})
export class GermplasmNameChangesModalComponent implements OnInit {

    private readonly itemsPerPage: number = 10;

    gid: number;
    nameId: number;
    title: string;

    page: number;
    previousPage: number;
    totalItems: number;
    queryCount: number;
    isLoading: boolean;

    germplasmNameChanges: GermplasmNameChange[];

    constructor(public activeModal: NgbActiveModal,
                private germplasmChangesService: GermplasmChangesService,
                private translateService: TranslateService,
                private germplasmNameContext: GermplasmNameContext,
                private jhiAlertService: JhiAlertService,
                private activatedRoute: ActivatedRoute,
                private router: Router) {
        this.page = 1;

        this.title = this.translateService.instant('basic-details.audit-changes.title',
            { entity: 'Name', entityValue: this.germplasmNameContext.germplasmName.name });
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

    dismiss() {
        this.activeModal.dismiss('cancel');
    }

    getEventDate(germplasmNameChange: GermplasmNameChange): string {
        if (germplasmNameChange.revisionType ===  RevisionType.CREATION) {
            return germplasmNameChange.createdDate;
        }
        return germplasmNameChange.modifiedDate;
    }

    getUser(germplasmNameChange: GermplasmNameChange): string {
        if (germplasmNameChange.revisionType ===  RevisionType.CREATION) {
            return germplasmNameChange.createdBy;
        }
        return germplasmNameChange.modifiedBy;
    }

    private loadAll() {
        this.isLoading = true;
        this.germplasmChangesService.getNamesChanges(this.gid, this.nameId, {
            page: this.page - 1,
            size: this.itemsPerPage
        }).pipe(finalize(() => {
            this.isLoading = false;
        })).subscribe(
            (res: HttpResponse<GermplasmNameChange[]>) => this.onSuccess(res.body, res.headers),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    private onSuccess(data: GermplasmNameChange[], headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.queryCount = this.totalItems;
        this.germplasmNameChanges = data;
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
    selector: 'jhi-germplasm-name-changes-popup',
    template: ``
})
export class GermplasmNameChangesPopupComponent implements OnInit {

    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }

    ngOnInit(): void {
        const modal = this.popupService.open(GermplasmNameChangesModalComponent as Component, { windowClass: 'modal-large', backdrop: 'static' });
        modal.then((modalRef) => {
            modalRef.componentInstance.gid = Number(this.route.snapshot.paramMap.get('gid'));
            modalRef.componentInstance.nameId = Number(this.route.snapshot.paramMap.get('nameId'));
        });

    }

}
