import { Component, Input, OnInit } from '@angular/core';
import { GermplasmListService } from '../shared/germplasm-list/service/germplasm-list.service';
import { GermplasmListSearchResponse } from '../shared/germplasm-list/model/germplasm-list-search-response.model';
import { Subscription } from 'rxjs';
import { SearchResult } from '../shared/search-result.model';
import { GermplasmListDataSearchResponse } from '../shared/germplasm-list/model/germplasm-list-data-search-response.model';
import { FilterType } from '../shared/column-filter/column-filter.component';
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
import { GermplasmListObservationVariable } from '../shared/germplasm-list/model/germplasm-list-observation-variable.model';
import { GermplasmListColumnCategory } from '../shared/germplasm-list/model/germplasm-list-column-category.type';
import { GermplasmListColumnModel } from './list-columns.component';
import { GermplasmListDataUpdateViewRequest } from '../shared/germplasm-list/model/germplasm-list-data-update-view-request.model';
import { MatchType } from '../shared/column-filter/column-filter-text-with-match-options-component';

declare var $: any;

@Component({
    selector: 'jhi-list',
    templateUrl: './list.component.html'
})
export class ListComponent implements OnInit {

    static readonly GERMPLASMLIST_VIEW_CHANGED_EVENT_SUFFIX = 'GermplasmListViewChanged';

    readonly STATIC_FILTERS = {
        ENTRY_NO: {
            key: 'entryNumbers', type: FilterType.LIST
        },
        GID: {
            key: 'gids', type: FilterType.LIST
        },
        GUID: {
            key: 'germplasmUUID', placeholder: 'Match Text', type: FilterType.TEXT
        },
        DESIGNATION: {
            key: 'designationFilter', placeholder: 'Search Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS,
            matchType: MatchType.STARTSWITH
        },
        IMMEDIATE_SOURCE_NAME: {
            key: 'immediateSourceName', placeholder: 'Contains Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS, matchType: MatchType.STARTSWITH
        },
        GROUP_SOURCE_NAME: {
            key: 'groupSourceName', placeholder: 'Contains Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS, matchType: MatchType.STARTSWITH
        },
        FEMALE_PARENT_NAME: {
            key: 'femaleParentName', placeholder: 'Search Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS, matchType: MatchType.STARTSWITH
        },
        MALE_PARENT_NAME: {
            key: 'maleParentName', placeholder: 'Search Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS, matchType: MatchType.STARTSWITH
        },
        BREEDING_METHOD_PREFERRED_NAME: {
            key: 'breedingMethodName', placeholder: 'Contains Text', type: FilterType.TEXT
        },
        BREEDING_METHOD_ABBREVIATION: {
            key: 'breedingMethodAbbreviation', placeholder: 'Contains Text', type: FilterType.TEXT
        },
        BREEDING_METHOD_GROUP: {
            key: 'breedingMethodGroup', placeholder: 'Contains Text', type: FilterType.TEXT
        },
        LOCATION_NAME: {
            key: 'locationName', placeholder: 'Contains Text', type: FilterType.TEXT
        },
        LOCATION_ABBREVIATION: {
            key: 'locationAbbreviation', placeholder: 'Contains Text', type: FilterType.TEXT
        },
        GERMPLASM_DATE: {
            key: 'germplasmDate', type: FilterType.DATE,
            fromKey: 'germplasmDateFrom',
            toKey: 'germplasmDateTo'
        },
        GERMPLASM_REFERENCE : {
            key: 'reference', placeholder: 'Contains Text', type: FilterType.TEXT
        }
    };

    @Input()
    listId: number;

    itemsPerPage = 20;

    user?: any;

    GermplasmListColumnCategory = GermplasmListColumnCategory;

    germplasmList: GermplasmList;
    header: GermplasmListObservationVariable[];
    entries: GermplasmListDataSearchResponse[];
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
        this.totalItems = 0;
        this.predicate = '';
        this.currentSearch = '';
        this.predicate = ColumnAlias.ENTRY_NUMBER;
        this.reverse = 'asc';
        this.resultSearch = new SearchResult('');
    }

    async ngOnInit() {
        const identity = await this.principal.identity();
        this.user = identity;

        this.germplasmListService.getGermplasmListById(this.listId).subscribe(
            (res: HttpResponse<GermplasmList>) => this.germplasmList = res.body,
            (res: HttpErrorResponse) => this.onError(res)
        );

        this.filters = this.getInitialFilters();
        this.registerColumnFiltersChanged();
        this.registerGermplasmListViewChanged();
        this.refreshTable();
    }

    private refreshTable() {
        this.germplasmListService.getGermplasmListDataTableHeader(this.listId).subscribe(
            (res: HttpResponse<GermplasmListObservationVariable[]>) => {
                this.header = res.body;
                this.loadAll();
            },
            (res: HttpErrorResponse) => this.onError(res));
    }

    get filters() {
        return this.germplasmListFilters;
    }

    set filters(filters) {
        this.germplasmListFilters = filters;
    }

    loadAll() {
        this.isLoading = true;

        const request = this.mapFiltersToRequest();
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
        this.loadAll();
    }

    onColumnsSelected(columns: GermplasmListColumnModel[]) {
        const request = this.mapSelectedColumnsToUpdateViewRequest(columns);
        this.germplasmListService.saveGermplasmListDataView(this.listId, request).subscribe(
            (res: HttpResponse<any>) => this.refreshTable(),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    // TODO: remove it?
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

    registerGermplasmListViewChanged() {
        this.eventSubscriber = this.eventManager.subscribe(this.listId + ListComponent.GERMPLASMLIST_VIEW_CHANGED_EVENT_SUFFIX, (event) => {
            this.resetTable();
        });
    }

    resetTable() {
        this.page = 1;
        this.previousPage = 1;
        this.loadAll();
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

    getHeaderDisplayName(column: GermplasmListObservationVariable) {
        if (column.columnCategory === GermplasmListColumnCategory.STATIC) {
            return column.name;
        }

        return column.alias ? column.alias : column.name;
    }

    isColumnFilterable(columnAlias: string): boolean {
        return this.STATIC_FILTERS[columnAlias];
    }

    applyFilters() {
        this.loadAll();
    }

    resetFilters() {
        this.loadAll();
    }

    private getInitialFilters() {
        return this.STATIC_FILTERS;
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

    // TODO: remove it?
    private getSort() {
        if (this.predicate === SORT_PREDICATE_NONE) {
            return '';
        }
        return [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
    }

    // TODO: remove it?
    private clearSort() {
        this.predicate = SORT_PREDICATE_NONE;
        this.reverse = '';
        $('.fa-sort').removeClass('fa-sort-up fa-sort-down');
    }

    private mapSelectedColumnsToUpdateViewRequest(selectedColumns: GermplasmListColumnModel[]): GermplasmListDataUpdateViewRequest[] {
        return selectedColumns.map((column: GermplasmListColumnModel) =>
            new GermplasmListDataUpdateViewRequest(column.id, column.category, column.typeId));
    }

    private mapFiltersToRequest() {
        const request = {};
        for (const filterKey of Object.keys(this.filters)) {
            const filter = this.filters[filterKey];
            if (filter.value || (filter.type ===  FilterType.DATE && (filter.from || filter.to))) {
                if (filter.type === FilterType.LIST) {
                    request[filter.key] = filter.value.split(',');
                } else if (filter.type === FilterType.TEXT_WITH_MATCH_OPTIONS) {
                    request[filter.key] = { type: filter.matchType, value: filter.value };
                } else if (filter.type === FilterType.DATE) {
                    if (filter.from) {
                        request[filter.fromKey] = `${filter.from.year}-${filter.from.month}-${filter.from.day}`
                    }
                    if (filter.to) {
                        request[filter.toKey] = `${filter.to.year}-${filter.to.month}-${filter.to.day}`
                    }
                } else {
                    request[filter.key] = filter.value;
                }
            }
        }
        return request;
    }

}

export enum ColumnAlias {
    'ENTRY_NUMBER' = 'ENTRY_NUMBER',
    'GID' = 'GID',
    'LOTS' = 'LOTS',
    'LOCATION_NAME' = 'LOCATION_NAME',
    'BREEDING_METHOD_PREFERRED_NAME' = 'BREEDING_METHOD_PREFERRED_NAME'
}
