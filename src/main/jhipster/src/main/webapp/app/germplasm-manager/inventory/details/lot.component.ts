import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LotService } from '../../../shared/inventory/service/lot.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { formatErrorList } from '../../../shared/alert/format-error-list';
import { JhiAlertService, JhiLanguageService } from 'ng-jhipster';
import { Lot } from '../../../shared/inventory/model/lot.model';
import { finalize } from 'rxjs/operators';

@Component({
    selector: 'jhi-lot',
    templateUrl: './lot.component.html',
})
export class LotComponent implements OnInit {

    private readonly itemsPerPage: number = 10;

    lots: Lot[];

    private gid: number;
    private page: number;
    private previousPage: number;
    private totalItems: number;
    private queryCount: number;
    private predicate: string;
    private reverse: boolean;

    private isLoading: boolean;

    constructor(private activatedRoute: ActivatedRoute,
                private lotService: LotService,
                private jhiAlertService: JhiAlertService,
                private jhiLanguageService: JhiLanguageService,
                private router: Router,
    ) {
        const queryParams = this.activatedRoute.snapshot.queryParams;
        this.gid = queryParams.gid;
        this.page = 1;
        this.predicate = 'lotId';
        this.reverse = false;
    }

    ngOnInit(): void {
        this.loadAll();
    }

    private loadAll() {
        this.isLoading = true;
        this.lotService.getLotsByGId(this.gid, {
            status: 'ACTIVE',
            page: this.page - 1,
            size: this.itemsPerPage,
            sort: this.sort()
        }).pipe(finalize(() => {
            this.isLoading = false;
        })).subscribe(
            (res: HttpResponse<Lot[]>) => this.onSuccess(res.body, res.headers),
            (res: HttpErrorResponse) => this.onError(res)
        );
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
                    size: this.itemsPerPage,
                    sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc')
                },
            relativeTo: this.activatedRoute,
            queryParamsHandling: 'merge'
        });
        this.loadAll();
    }

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'lotId') {
            result.push('lotId');
        }
        return result;
    }

    private onSuccess(data: Lot[], headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.queryCount = this.totalItems;
        this.lots = data;
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.jhiAlertService.addAlert({ msg: 'error.custom', type: 'danger', toast: false, params: { param: msg } }, null);
        } else {
            this.jhiAlertService.addAlert({ msg: 'error.general', type: 'danger', toast: false }, null);
        }
    }

    private trackId(index: number, item: Lot) {
        return item.lotId;
    }

}
