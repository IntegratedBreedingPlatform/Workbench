import {Component, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {ColumnFilterComponent, FilterType} from '../shared/column-filter/column-filter.component';
import {MatchType} from '../shared/column-filter/column-filter-text-with-match-options-component';
import {ActivatedRoute, Router} from '@angular/router';
import {JhiEventManager, JhiLanguageService} from 'ng-jhipster';
import {AlertService} from '../shared/alert/alert.service';
import {finalize} from 'rxjs/internal/operators/finalize';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {formatErrorList} from '../shared/alert/format-error-list';
import {SearchResult} from '../shared/search-result.model';
import {ParamContext} from '../shared/service/param.context';
import {CrossPlanSearchResponse} from "../shared/cross-plan-design/model/cross-plan-search-response.model";
import {CrossPlanService} from "../shared/cross-plan-design/service/cross-plan.service";
import {CrossPlanSearchRequest} from "../shared/cross-plan-design/model/cross-plan-search-request.model";
import {SORT_PREDICATE_NONE} from "../germplasm-manager/germplasm-search-resolve-paging-params";

declare var $: any;

@Component({
    selector: 'jhi-cross-plan-search',
    templateUrl: './cross-plan-search.component.html'
})
export class CrossPlanSearchComponent implements OnInit {
    static readonly COLUMN_FILTER_EVENT_NAME = 'searchColumnFiltersChanged';

    COLUMN_FILTER_EVENT_NAME = CrossPlanSearchComponent.COLUMN_FILTER_EVENT_NAME;

    itemsPerPage = 20;

    ColumnLabels = ColumnLabels;

    crossPlans: CrossPlanSearchResponse[];
    searchRequest: CrossPlanSearchRequest;
    eventSubscriber: Subscription;
    resultSearch: SearchResult;

    currentSearch: string;
    totalItems: number;
    page: number;
    previousPage: number;
    predicate: any;
    reverse: boolean;

    isLoading: boolean;

    crossPlanFilters: any;

    constructor(private activatedRoute: ActivatedRoute,
                private jhiLanguageService: JhiLanguageService,
                private eventManager: JhiEventManager,
                private crossPlanService: CrossPlanService,
                private router: Router,
                private alertService: AlertService,
                private paramContext: ParamContext
    ) {
        this.page = 1;
        this.totalItems = 0;
        this.currentSearch = '';
        this.predicate = ColumnLabels.CREATION_DATE;
        this.reverse = false;
        this.resultSearch = new SearchResult('');
        this.searchRequest = new CrossPlanSearchRequest();
    }

    ngOnInit() {
        this.filters = this.getInitialFilters();
        ColumnFilterComponent.reloadFilters(this.filters, this.request);
        this.registerColumnFiltersChanged();
        this.registerFilterBy();
        this.loadAll(this.request);
    }

    get request() {
        return this.searchRequest;
    }

    set request(request: CrossPlanSearchRequest) {
        this.searchRequest = request;
    }

    get filters() {
        return this.crossPlanFilters;
    }

    set filters(filters) {
        this.crossPlanFilters = filters;
    }

    loadAll(request: CrossPlanSearchRequest) {
        this.isLoading = true;
        this.crossPlanService.searchCrossPlan(request,
            {
                page: this.page - 1,
                size: this.itemsPerPage,
                sort: this.getSort()
            }
        ).pipe(finalize(() => {
            this.isLoading = false;
        })).subscribe(
            (res: HttpResponse<CrossPlanSearchResponse[]>) => this.onSuccess(res.body, res.headers),
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
        this.reverse = false;
        $('.fa-sort').removeClass('fa-sort-up fa-sort-down');
    }

    onClearSort($event) {
        $event.preventDefault();
        this.clearSort();
        this.transition();
    }

    trackId(index: number, item: CrossPlanSearchResponse) {
        return item.id;
    }

    registerColumnFiltersChanged() {
        this.eventSubscriber = this.eventManager.subscribe(CrossPlanSearchComponent.COLUMN_FILTER_EVENT_NAME, (event) => {
            this.resetTable();
        });
    }

    registerFilterBy() {
        this.eventSubscriber = this.eventManager.subscribe('crossPlanNameFilter', (event) => {
            this.resetFilters();
            const crossPlanNameFilter = this.filters.filter((filter) => filter.type === FilterType.TEXT_WITH_MATCH_OPTIONS);
            crossPlanNameFilter[0].matchType = MatchType.EXACTMATCH;
            this.request.crossPlanNameFilter = event.content;
            ColumnFilterComponent.reloadFilters(this.filters, this.request);
            this.request.crossPlanNameFilter = {
                type: MatchType.EXACTMATCH,
                value: event.content
            }
            this.resetTable();
        });
    }

    private resetFilters() {
        this.filters = this.getInitialFilters();
        this.request = new CrossPlanSearchRequest();
        this.resultSearch = new SearchResult('');
    }

    resetTable() {
        this.page = 1;
        this.previousPage = 1;
        this.loadAll(this.request);
    }

    selectList($event, crossPlan: CrossPlanSearchResponse) {
        $event.preventDefault();
        this.router.navigate([`/cross-plan/${crossPlan.id}`], {queryParams: {
                id: crossPlan.id,
                name: crossPlan.name
            }
        });
    }

    private getInitialFilters() {
        return [
            {
                key: 'crossPlanNameFilter', name: 'Cross Plan Name', placeholder: 'Search Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS,
                matchType: MatchType.STARTSWITH, default: true
            },
            { key: 'parentFolderName', name: 'Parent Folder', placeholder: 'Contains Text', type: FilterType.TEXT },
            { key: 'description', name: 'Description', placeholder: 'Contains Text', type: FilterType.TEXT },
            { key: 'ownerName', name: 'Created By', placeholder: 'Contains Text', type: FilterType.TEXT },
            { key: 'notes', name: 'Notes', placeholder: 'Contains Text', type: FilterType.TEXT },
            {
                key: 'crossPlanDate', name: 'Date', type: FilterType.DATE,
                fromKey: 'crossPlanStartDateFrom',
                toKey: 'crossPlanStartDateTo',
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

    private onSuccess(data: CrossPlanSearchResponse[], headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.crossPlans = data;
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
    }

    crossPlanDesign() {
        /*
         * FIXME workaround for history.back() with base-href
         *  Find solution for IBP-3534 / IBP-4177 that doesn't involve base-href
         *  or 'inventory-manager' string
         */
        window.history.pushState({}, '', window.location.hash);

        window.location.href = '/ibpworkbench/controller/jhipster#cross-plan-design'
            + '?cropName=' + this.paramContext.cropName
            + '&programUUID=' + this.paramContext.programUUID
            + '&selectedProjectId=' + this.paramContext.selectedProjectId
            + '&loggedInUserId=' + this.paramContext.loggedInUserId;
    }
}

export enum ColumnLabels {
    'CROSS_PLAN_NAME' = 'NAME',
    'PARENT_FOLDER' = 'PARENT_FOLDER_NAME',
    'DESCRIPTION' = 'DESCRIPTION',
    'CREATED_BY' = 'CREATED_BY',
    'LIST_TYPE' = 'LIST_TYPE',
    'NOTES' = 'NOTES',
    'CREATION_DATE' = 'CREATION_DATE'
}
