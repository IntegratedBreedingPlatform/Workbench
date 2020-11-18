import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ParamContext } from '../../../shared/service/param.context';
import { LotService } from '../../../shared/inventory/service/lot.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { formatErrorList } from '../../../shared/alert/format-error-list';
import { JhiAlertService, JhiLanguageService } from 'ng-jhipster';
import { Lot } from '../../../shared/inventory/model/lot.model';
import { ITEMS_PER_PAGE } from '../../../shared';
import { finalize } from 'rxjs/operators';

@Component({
    selector: 'jhi-lot',
    templateUrl: './lot.component.html',
})
export class LotComponent implements OnInit {

    private gid: number;
    private lots: Lot[];
    page: number;
    previousPage: number;
    itemsPerPage: number;
    private totalItems: number;
    private queryCount: number;
    predicate: any;
    reverse: any;
    private isLoading: boolean;

    constructor(private activatedRoute: ActivatedRoute,
                private paramContext: ParamContext,
                private lotService: LotService,
                private jhiAlertService: JhiAlertService,
                private jhiLanguageService: JhiLanguageService,
    ) {
        this.itemsPerPage = ITEMS_PER_PAGE;
        this.page = 1;

        this.paramContext.readParams();
        const queryParams = this.activatedRoute.snapshot.queryParams;
        this.gid = queryParams.gid;
    }

    ngOnInit(): void {
        this.loadLots();
    }

    private loadLots() {
        this.lotService.getLotsByGId(this.gid, {
            page: this.page - 1,
            size: this.itemsPerPage,
            // sort: this.sort()
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
        // this.router.navigate(['./'], {
        //     queryParams:
        //         {
        //             page: this.page,
        //             size: this.itemsPerPage,
        //             search: this.currentSearch,
        //             sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc')
        //         }, relativeTo: this.activatedRoute
        // });
        // this.loadAll(this.request);
    }

    private onSuccess(data, headers) {
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
