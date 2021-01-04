import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { formatErrorList } from '../../../shared/alert/format-error-list';
import { JhiAlertService, JhiLanguageService } from 'ng-jhipster';
import { Lot } from '../../../shared/inventory/model/lot.model';
import { finalize } from 'rxjs/operators';
import { Transaction } from '../../../shared/inventory/model/transaction.model';
import { TransactionService } from '../../../shared/inventory/service/transaction.service';

@Component({
    selector: 'jhi-transaction',
    templateUrl: './transaction.component.html',
})
export class TransactionComponent implements OnInit {

    private readonly itemsPerPage: number = 10;

    transactions: Transaction[];

    private gid: number;

    private lotId: number;

    private page: number;
    private previousPage: number;
    private totalItems: number;
    private queryCount: number;
    private predicate: string;
    private reverse: string;

    private isLoading: boolean;

    constructor(private activatedRoute: ActivatedRoute,
                private transactionService: TransactionService,
                private jhiAlertService: JhiAlertService,
                private jhiLanguageService: JhiLanguageService,
                private router: Router
    ) {
        const queryParams = this.activatedRoute.snapshot.queryParams;
        this.gid = queryParams.gid;
        this.lotId = queryParams.lotId;
        this.page = 1;
        this.predicate = 'transactionId';
        this.reverse = 'asc';
    }

    ngOnInit(): void {
        this.loadAll();
    }

    private loadAll() {
        this.isLoading = true;
        this.transactionService.getTransactionsByGermplasmId(this.gid, this.lotId, {
            lotStatus: 'ACTIVE',
            page: this.page - 1,
            size: this.itemsPerPage,
            sort: this.sort()
        }).pipe(finalize(() => {
            this.isLoading = false;
        })).subscribe(
            (res: HttpResponse<Transaction[]>) => this.onSuccess(res.body, res.headers),
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
        if (this.predicate !== 'transactionId') {
            result.push('transactionId');
        }
        return result;
    }

    private onSuccess(data: Transaction[], headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.queryCount = this.totalItems;
        this.transactions = data;
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
