import { Component, OnInit } from '@angular/core';
import { SearchResult } from '../shared/search-result.model';
import { StudySearchResponse } from '../shared/study/model/study-search-response.model';
import { StudySearchRequest } from '../shared/study/model/study-search-request.model';
import { finalize } from 'rxjs/internal/operators/finalize';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { StudyService } from '../shared/study/service/study.service';
import { JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { ActivatedRoute, Router } from '@angular/router';
import { SORT_PREDICATE_NONE } from '../germplasm-manager/germplasm-search-resolve-paging-params';
import { formatErrorList } from '../shared/alert/format-error-list';
import { AlertService } from '../shared/alert/alert.service';
import { ColumnFilterComponent, FilterType } from '../shared/column-filter/column-filter.component';
import { MatchType } from '../shared/column-filter/column-filter-text-with-match-options-component';
import { ColumnFilterRadioButtonOption } from '../shared/column-filter/column-filter-radio-component';
import { Subscription } from 'rxjs';
import { UrlService } from '../shared/service/url.service';
import { VariableTypeEnum } from '../shared/ontology/variable-type.enum';
import { MANAGE_STUDIES_PERMISSIONS } from '../shared/auth/permissions';
import { Principal } from '../shared';

declare var $: any;

@Component({
    selector: 'jhi-study-search',
    templateUrl: './study-search.component.html',
    styleUrls: ['./study-search.component.scss']
})
export class StudySearchComponent implements OnInit {

    static readonly COLUMN_FILTER_EVENT_NAME = 'searchColumnFiltersChanged';

    STUDIES_EDITION_PERMISSIONS = [
        ...MANAGE_STUDIES_PERMISSIONS,
        'MS_MANAGE_OBSERVATION_UNITS',
        'MS_WITHDRAW_INVENTORY',
        'MS_CREATE_PENDING_WITHDRAWALS',
        'MS_CREATE_CONFIRMED_WITHDRAWALS',
        'MS_CANCEL_PENDING_TRANSACTIONS',
        'MS_MANAGE_FILES',
        'MS_CREATE_LOTS'
    ];

    itemsPerPage = 20;

    COLUMN_FILTER_EVENT_NAME = StudySearchComponent.COLUMN_FILTER_EVENT_NAME;
    ColumnLabels = ColumnLabels;

    studies: StudySearchResponse[];
    searchRequest: StudySearchRequest;
    eventSubscriber: Subscription;
    resultSearch: SearchResult;

    currentSearch: string;
    totalItems: number;
    page: number;
    previousPage: number;
    predicate: any;
    reverse: boolean;

    isLoading: boolean;

    studyFilters: any;

    user?: any;

    constructor(private jhiLanguageService: JhiLanguageService,
                private studyService: StudyService,
                private router: Router,
                private activatedRoute: ActivatedRoute,
                private alertService: AlertService,
                private eventManager: JhiEventManager,
                private urlService: UrlService,
                private principal: Principal) {
        this.page = 1;
        this.totalItems = 0;
        this.currentSearch = '';
        this.predicate = ColumnLabels.START_DATE;
        this.reverse = false;
        this.resultSearch = new SearchResult('');
        this.searchRequest = new StudySearchRequest();
    }

    async ngOnInit() {
        this.user = await this.principal.identity();

        this.filters = this.getInitialFilters();
        this.registerColumnFiltersChanged();
        this.loadAll(this.request);
    }

    get request() {
        return this.searchRequest;
    }

    set request(request: StudySearchRequest) {
        this.searchRequest = request;
    }

    get filters() {
        return this.studyFilters;
    }

    set filters(filters) {
        this.studyFilters = filters;
    }

    loadAll(request: StudySearchRequest) {
        this.isLoading = true;
        this.studyService.searchStudies(request,
            {
                page: this.page - 1,
                size: this.itemsPerPage,
                sort: this.getSort()
            }
        ).pipe(finalize(() => {
            this.isLoading = false;
        })).subscribe(
            (res: HttpResponse<StudySearchResponse[]>) => this.onSuccess(res.body, res.headers),
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

    trackId(index: number, item: StudySearchResponse) {
        return item.studyId;
    }

    resetTable() {
        this.page = 1;
        this.previousPage = 1;
        this.loadAll(this.request);
    }

    openViewSummary($event, study: StudySearchResponse) {
        $event.preventDefault();
        this.router.navigate([`/study-manager/study/${study.studyId}`], {
            queryParams: {
                studyId: study.studyId,
                studyName: study.studyName
            }
        });
    }

    openStudy(study: StudySearchResponse) {
        if (study.locked && !this.user.authorities.includes('ADMIN') && this.user.id !== study.ownerId) {
            this.alertService.error('study.manager.errors.study-locked', { ownerName: study.ownerName });
            return;
        }

        this.urlService.openStudy(study.studyId, study.studyName);
    }

    onClearSort($event) {
        $event.preventDefault();
        this.clearSort();
        this.transition();
    }

    private registerColumnFiltersChanged() {
        this.eventSubscriber = this.eventManager.subscribe(StudySearchComponent.COLUMN_FILTER_EVENT_NAME, (event) => {
            this.resetTable();
        });
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

    private onSuccess(data: StudySearchResponse[], headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.studies = data;
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
    }

    private getInitialFilters() {
        return [
            {
                key: 'studyNameFilter', name: 'Study Name', placeholder: 'Search Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS,
                matchType: MatchType.STARTSWITH, default: true
            },
            {
                key: 'studyTypeIds', name: 'Study type', type: FilterType.CHECKLIST,
                value: undefined,
                options: this.getStudyTypeOptions()
            },
            { key: 'locked', name: 'Locked', type: FilterType.RADIOBUTTON, options: this.getStatusFilterOptions() },
            { key: 'ownerName', name: 'Created By', placeholder: 'Contains Text', type: FilterType.TEXT },
            {
                key: 'studyDate', name: 'Date', type: FilterType.DATE,
                fromKey: 'studyStartDateFrom',
                toKey: 'studyStartDateTo',
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
            { key: 'parentFolderName', name: 'Parent Folder', placeholder: 'Contains Text', type: FilterType.TEXT },
            { key: 'objective', name: 'Objective', placeholder: 'Contains Text', type: FilterType.TEXT },
            {
                key: 'studySettings', name: 'Study Settings', type: FilterType.VARIABLES, placeholder: 'Search study settings...',
                description: 'Search for study settings that you want to filter', variableTypeIds: [VariableTypeEnum.STUDY_DETAIL.toString()], variables: [],
                transform(req) {
                    ColumnFilterComponent.transformVariablesFilter(this, req);
                },
                reset(req) {
                    ColumnFilterComponent.resetVariablesFilter(this, req);
                },
            },
            {
                key: 'environmentDetails', name: 'Environment Details', type: FilterType.VARIABLES, placeholder: 'Search environment details...',
                description: 'Search for environment details that you want to filter', variableTypeIds: [VariableTypeEnum.ENVIRONMENT_DETAIL.toString()], variables: [],
                transform(req) {
                    ColumnFilterComponent.transformVariablesFilter(this, req);
                },
                reset(req) {
                    ColumnFilterComponent.resetVariablesFilter(this, req);
                },
            },
            {
                key: 'environmentConditions', name: 'Environment Conditions', type: FilterType.VARIABLES, placeholder: 'Search environment conditions...',
                description: 'Search for environment conditions that you want to filter', variableTypeIds: [VariableTypeEnum.ENVIRONMENT_CONDITION.toString()], variables: [],
                transform(req) {
                    ColumnFilterComponent.transformVariablesFilter(this, req);
                },
                reset(req) {
                    ColumnFilterComponent.resetVariablesFilter(this, req);
                },
            }
        ];
    }

    private getStudyTypeOptions(): Promise<any> {
        return new Promise((resolve) => {
            resolve([{ id: 1, name: 'Nursery' },
                { id: 6, name: 'Trial' }]);
        });
    }

    private getStatusFilterOptions(): Promise<ColumnFilterRadioButtonOption[]> {
        return new Promise<ColumnFilterRadioButtonOption[]>((resolve, reject) => {
            resolve([new ColumnFilterRadioButtonOption(true, 'Yes'),
                new ColumnFilterRadioButtonOption(false, 'No')]);
        });
    }

}

export enum ColumnLabels {
    'STUDY_NAME' = 'STUDY_NAME',
    'STUDY_TYPE_NAME' = 'STUDY_TYPE_NAME',
    'LOCKED' = 'LOCKED',
    'STUDY_OWNER_NAME' = 'STUDY_OWNER_NAME',
    'START_DATE' = 'START_DATE',
    'PARENT_FOLDER_NAME' = 'PARENT_FOLDER_NAME',
    'OBJECTIVE' = 'OBJECTIVE'
}
