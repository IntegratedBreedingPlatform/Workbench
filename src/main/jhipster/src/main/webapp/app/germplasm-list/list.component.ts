import { Component, Input, OnInit } from '@angular/core';
import { GermplasmListService } from '../shared/germplasm-list/service/germplasm-list.service';
import { GermplasmListSearchResponse } from '../shared/germplasm-list/model/germplasm-list-search-response.model';
import { GermplasmListSearchRequest } from '../shared/germplasm-list/model/germplasm-list-search-request.model';
import { Subscription } from 'rxjs';
import { SearchResult } from '../shared/search-result.model';
import { GermplasmListDataSearchResponse } from '../shared/germplasm-list/model/germplasm-list-data-search-response.model';
import { GermplasmListDataSearchRequest } from '../shared/germplasm-list/model/germplasm-list-data-search-request.model';
import { ColumnFilterComponent } from '../shared/column-filter/column-filter.component';
import { finalize } from 'rxjs/internal/operators/finalize';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { SORT_PREDICATE_NONE } from '../germplasm-manager/germplasm-search-resolve-paging-params';
import { formatErrorList } from '../shared/alert/format-error-list';
import { ActivatedRoute, Router } from '@angular/router';
import { JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { AlertService } from '../shared/alert/alert.service';
import { GermplasmList } from '../shared/germplasm-list/model/germplasm-list.model';
import { GermplasmListSearchComponent } from './germplasm-list-search.component';
import { Principal } from '../shared';
import { ObservationVariable } from '../shared/model/observation-variable.model';

declare var $: any;

@Component({
    selector: 'jhi-list',
    templateUrl: './list.component.html'
})
export class ListComponent implements OnInit {

    @Input()
    listId: number;

    itemsPerPage = 20;

    user?: any;

    ColumnLabels = ColumnLabels;

    germplasmList: GermplasmList;
    header: ObservationVariable[];
    entries: GermplasmListDataSearchResponse[];
    searchRequest: GermplasmListDataSearchRequest;
    eventSubscriber: Subscription;
    resultSearch: SearchResult;

    currentSearch: string;
    totalItems: number;
    page: number;
    previousPage: number;
    predicate: any;
    reverse: any;

    isLoading: boolean;

    germplasmListFilters: any;

    constructor(private activatedRoute: ActivatedRoute,
                private jhiLanguageService: JhiLanguageService,
                private eventManager: JhiEventManager,
                private germplasmListService: GermplasmListService,
                private router: Router,
                private alertService: AlertService,
                private principal: Principal) {
        this.page = 1;
        this.predicate = '';
        this.currentSearch = '';
        this.predicate = ColumnLabels.ENTRY_NUMBER;
        this.reverse = 'asc';
        this.resultSearch = new SearchResult('');
        this.searchRequest = new GermplasmListDataSearchRequest();
    }

    async ngOnInit() {
        const identity = await this.principal.identity();
        this.user = identity;

        this.germplasmListService.getGermplasmListById(this.listId).subscribe(
            (res: HttpResponse<GermplasmList>) => this.germplasmList = res.body,
            (res: HttpErrorResponse) => this.onError(res)
        );

        this.filters = this.getInitialFilters();
        ColumnFilterComponent.reloadFilters(this.filters, this.request);

        this.registerColumnFiltersChanged();

        this.germplasmListService.getGermplasmListDataTableHeader(this.listId).subscribe(
            (res: HttpResponse<ObservationVariable[]>) => {
                this.header = res.body;
                this.loadAll(this.request);
            },
            (res: HttpErrorResponse) => this.onError(res));
    }

    get request() {
        return this.searchRequest;
    }

    set request(request: GermplasmListSearchRequest) {
        this.searchRequest = request;
    }

    get filters() {
        return this.germplasmListFilters;
    }

    set filters(filters) {
        this.germplasmListFilters = filters;
    }

    loadAll(request: GermplasmListDataSearchRequest) {
        this.isLoading = true;
        this.germplasmListService.searchListData(this.listId, request,
            {
                page: this.page - 1,
                size: this.itemsPerPage,
                sort: this.getSort()
            }
        ).pipe(finalize(() => {
            this.isLoading = false;
        })).subscribe(
            (res: HttpResponse<GermplasmListDataSearchResponse[]>) => this.onSuccess(res.body, res.headers),
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
            queryParamsHandling: 'merge',
            queryParams: {
                page: this.page,
                size: this.itemsPerPage,
                search: this.currentSearch,
                sort: this.getSort()
            },
            relativeTo: this.activatedRoute
        });
        this.loadAll(this.request);
    }

    onColumnsSelected(values: number[]) {
        console.log('value: ' + values);
    }

    getRowData(response: GermplasmListDataSearchResponse, column: ObservationVariable) {
        return response.data[column.name] === undefined ? response.data[column.termId] : response.data[column.name];
    }

    private getSort() {
        if (this.predicate === SORT_PREDICATE_NONE) {
            return '';
        }
        return [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
    }

    private clearSort() {
        this.predicate = SORT_PREDICATE_NONE;
        this.reverse = '';
        $('.fa-sort').removeClass('fa-sort-up fa-sort-down');
    }

    onClearSort($event) {
        $event.preventDefault();
        this.clearSort();
        this.transition();
    }

    trackId(index: number, item: GermplasmListSearchResponse) {
        return item.listId;
    }

    registerColumnFiltersChanged() {
        this.eventSubscriber = this.eventManager.subscribe(this.listId + 'ColumnFiltersChanged', (event) => {
            this.resetTable();
        });
    }

    resetTable() {
        this.page = 1;
        this.previousPage = 1;
        this.loadAll(this.request);
    }

    selectList($event, list: GermplasmListSearchResponse) {
        $event.preventDefault();

        this.router.navigate(['/germplasm-list/list'], {
            queryParams: {
                listId: list.listId,
                listName: list.listName
            }
        });
    }

    toggleListStatus() {
        this.germplasmListService.toggleGermplasmListStatus(this.listId).subscribe(
            (res: boolean) => this.onToggleListStatusSuccess(res),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    private getInitialFilters() {
        return [];
    }

    private onSuccess(data: GermplasmListDataSearchResponse[], headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.entries = data;
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
    }

    private onToggleListStatusSuccess(locked: boolean) {
        this.germplasmList.locked = locked;
        this.eventManager.broadcast({ name: GermplasmListSearchComponent.COLUMN_FILTER_EVENT_NAME, content: '' });
    }

}

export enum ColumnLabels {
    'ENTRY_NUMBER' = 'ENTRY_NUMBER'
}
