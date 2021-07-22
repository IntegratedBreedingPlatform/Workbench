import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { ColumnFilterComponent, FilterType } from '../shared/column-filter/column-filter.component';
import { MatchType } from '../shared/column-filter/column-filter-text-with-match-options-component';
import { ActivatedRoute, Router } from '@angular/router';
import { JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { AlertService } from '../shared/alert/alert.service';
import { finalize } from 'rxjs/internal/operators/finalize';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { SORT_PREDICATE_NONE } from '../germplasm-manager/germplasm-search-resolve-paging-params';
import { formatErrorList } from '../shared/alert/format-error-list';
import { GermplasmListService } from '../shared/germplasm-list/service/germplasm-list.service';
import { GermplasmListSearchRequest } from '../shared/germplasm-list/model/germplasm-list-search-request.model';
import { GermplasmListSearchResponse } from '../shared/germplasm-list/model/germplasm-list-search-response.model';
import { SearchResult } from '../shared/search-result.model';
import { ListType } from '../shared/list-builder/model/list-type.model';
import { ColumnFilterRadioButtonOption } from '../shared/column-filter/column-filter-radio-component';
import { Select2OptionData } from 'ng-select2/lib/ng-select2.interface';

declare var $: any;

@Component({
    selector: 'jhi-germplasm-list-search',
    templateUrl: './germplasm-list-search.component.html'
})
export class GermplasmListSearchComponent implements OnInit {

    itemsPerPage = 20;

    ColumnLabels = ColumnLabels;

    germplasmLists: GermplasmListSearchResponse[];
    searchRequest: GermplasmListSearchRequest;
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
                private alertService: AlertService
    ) {
        this.page = 1;
        this.predicate = '';
        this.currentSearch = '';
        this.predicate = ColumnLabels.LIST_NAME;
        this.reverse = 'asc';
        this.resultSearch = new SearchResult('');
        this.searchRequest = new GermplasmListSearchRequest();
    }

    ngOnInit() {
        this.filters = this.getInitialFilters();
        ColumnFilterComponent.reloadFilters(this.filters, this.request);

        this.registerColumnFiltersChanged();
        this.loadAll(this.request);
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

    loadAll(request: GermplasmListSearchRequest) {
        this.isLoading = true;
        this.germplasmListService.searchList(request,
            {
                page: this.page - 1,
                size: this.itemsPerPage,
                sort: this.getSort()
            }
        ).pipe(finalize(() => {
            this.isLoading = false;
        })).subscribe(
            (res: HttpResponse<GermplasmListSearchResponse[]>) => this.onSuccess(res.body, res.headers),
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
        this.eventSubscriber = this.eventManager.subscribe('columnFiltersChanged', (event) => {
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

        this.router.navigate(['/germplasm-list'], {queryParams: {
                listId: list.listId,
                listName: list.listName
            }
        });
    }

    private getInitialFilters() {
        return [
            {
                key: 'listNameFilter', name: 'List Name', placeholder: 'Search Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS,
                matchType: MatchType.STARTSWITH, default: true
            },
            { key: 'parentFolderName', name: 'Parent Folder', placeholder: 'Contains Text', type: FilterType.TEXT },
            { key: 'description', name: 'Description', placeholder: 'Contains Text', type: FilterType.TEXT },
            { key: 'ownerName', name: 'List Owner', placeholder: 'Contains Text', type: FilterType.TEXT },
            {
                key: 'listTypes', name: 'List type', type: FilterType.DROPDOWN, values: this.getListTypesOptions(), multipleSelect: true,
                transform(req) {
                    ColumnFilterComponent.transformDropdownFilter(this, req);
                },
                reset(req) {
                    ColumnFilterComponent.resetDropdownFilter(this, req);
                },
            },
            { key: 'locked', name: 'Status', type: FilterType.RADIOBUTTON, options: this.getStatusFilterOptions() },
            { key: 'notes', name: 'Notes', placeholder: 'Contains Text', type: FilterType.TEXT },
            {
                key: 'listDate', name: 'List Date', type: FilterType.DATE,
                fromKey: 'listDateFrom',
                toKey: 'listDateTo',
                transform(req) {
                    ColumnFilterComponent.transformDateFilter(this, req, this.fromKey, this.toKey);
                },
                reset(req) {
                    ColumnFilterComponent.resetRangeFilter(this, req, this.fromKey, this.toKey);
                },
                reload(req) {
                    this.from = req[this.fromKey];
                    this.to = req[this.toKey];
                }
            },
        ];
    }

    private getListTypesOptions(): Promise<Select2OptionData[]> {
        return this.germplasmListService.getListTypes().toPromise().then((listTypes: ListType[]) => {
            return listTypes.filter((listType: ListType) => (listType.code !== 'FOLDER'))
                .map((listType: ListType) => {
                    return { id: listType.code, text: listType.name + ' (' + listType.code + ')' }
                });
        });
    }

    private getStatusFilterOptions(): Promise<ColumnFilterRadioButtonOption[]> {
        return new Promise<ColumnFilterRadioButtonOption[]>((resolve, reject) => {
            resolve([new ColumnFilterRadioButtonOption(true, 'LOCKED'),
                new ColumnFilterRadioButtonOption(false, 'UNLOCKED')]);
        });
    }

    private onSuccess(data: GermplasmListSearchResponse[], headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.germplasmLists = data;
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
    }

}

export enum ColumnLabels {
    'LIST_NAME' = 'LIST_NAME',
    'PARENT_FOLDER' = 'PARENT_FOLDER_NAME',
    'DESCRIPTION' = 'DESCRIPTION',
    'LIST_OWNER' = 'LIST_OWNER',
    'LIST_TYPE' = 'LIST_TYPE',
    'NUMBER_OF_ENTRIES' = 'NUMBER_OF_ENTRIES',
    'STATUS' = 'STATUS',
    'NOTES' = 'NOTES',
    'LIST_DATE' = 'LIST_DATE'
}
