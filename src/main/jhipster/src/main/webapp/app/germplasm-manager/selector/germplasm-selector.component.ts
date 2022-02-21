import { Component, OnInit, ViewChild } from '@angular/core';
import { Germplasm } from '../../entities/germplasm/germplasm.model';
import { GermplasmSearchRequest } from '../../entities/germplasm/germplasm-search-request.model';
import { ActivatedRoute, Router } from '@angular/router';
import { ColumnFilterComponent, FilterType } from '../../shared/column-filter/column-filter.component';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { finalize } from 'rxjs/internal/operators/finalize';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { NgbActiveModal, NgbModal, NgbPopover } from '@ng-bootstrap/ng-bootstrap';
import { Subscription } from 'rxjs';
import { GermplasmTreeTableComponent } from '../../shared/tree/germplasm/germplasm-tree-table.component';
import { StudyTreeComponent } from '../../shared/tree/study/study-tree.component';
import { MatchType } from '../../shared/column-filter/column-filter-text-with-match-options-component';
import { PedigreeType } from '../../shared/column-filter/column-filter-pedigree-options-component';
import { SORT_PREDICATE_NONE } from '.././germplasm-search-resolve-paging-params';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';
import { TranslateService } from '@ngx-translate/core';
import { ParamContext } from '../../shared/service/param.context';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { AlertService } from '../../shared/alert/alert.service';
import { ColumnLabels } from '../germplasm-search.component';
import { GermplasmDetailsUrlService } from '../../shared/germplasm/service/germplasm-details.url.service';
import { SearchResult } from '../../shared/search-result.model';

declare var $: any;

@Component({
    selector: 'jhi-germplasm-selector',
    templateUrl: './germplasm-selector.component.html'
})
export class GermplasmSelectorComponent implements OnInit {

    ColumnLabels = ColumnLabels;

    @ViewChild('colVisPopOver') public colVisPopOver: NgbPopover;
    eventSubscriber: Subscription;
    germplasmList: Germplasm[];
    error: any;
    currentSearch: string;
    routeData: any;
    links: any;
    filteredItems: any;
    itemsPerPage: any = 10;
    page: any = 1;
    predicate: any;
    previousPage: any;
    reverse: any;
    resultSearch: SearchResult;

    isLoading: boolean;
    selectMultiple: any = false;

    germplasmSearchRequest = new GermplasmSearchRequest();
    germplasmFilters: any;
    germplasmHiddenColumns = {};

    selectedItems: any[] = [];
    isSelectAllPages = false;

    private static getInitialFilters() {
        return [
            {
                key: 'nameFilter', name: 'Name', placeholder: 'Search Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS,
                matchType: MatchType.STARTSWITH, default: true
            },
            { key: 'germplasmUUID', name: 'Germplasm UID', placeholder: 'Match Text', type: FilterType.TEXT },
            { key: 'gids', name: 'GID', type: FilterType.LIST, default: true },
            {
                key: 'gidRange', name: 'GID Range', type: FilterType.NUMBER_RANGE,
                fromKey: 'gidFrom',
                toKey: 'gidTo',
                transform(req) {
                    ColumnFilterComponent.transformNumberRangeFilter(this, req, this.fromKey, this.toKey);
                },
                reset(req) {
                    ColumnFilterComponent.resetRangeFilter(this, req, this.fromKey, this.toKey);
                },
                reload(req) {
                    this.from = req[this.fromKey];
                    this.to = req[this.toKey];
                }
            },
            { key: 'groupId', name: 'Group ID', placeholder: 'Match Text', type: FilterType.TEXT },
            { key: 'sampleUID', name: 'Sample ID', placeholder: 'Match Text', type: FilterType.TEXT },
            {
                key: 'germplasmListIds', name: 'Germplasm List', type: FilterType.MODAL,
                open(modal, request) {
                    return new Promise((resolve) => {
                        modal.open(GermplasmTreeTableComponent as Component, { size: 'lg', backdrop: 'static' })
                            .result.then((germplasmLists) => {
                            if (germplasmLists && germplasmLists.length > 0) {
                                this.value = germplasmLists.map((list) => list.name);
                                request[this.key] = germplasmLists.map((list) => list.id);
                            }
                            resolve();
                        }, () => {
                        });
                    });
                }
            },
            { key: 'stockId', name: 'Stock ID', placeholder: 'Starts with', type: FilterType.TEXT },
            { key: 'locationOfOrigin', name: 'Location of Origin', placeholder: 'Contains Text', type: FilterType.TEXT },
            { key: 'locationOfUse', name: 'Location of Use', placeholder: 'Contains Text', type: FilterType.TEXT },
            {
                key: 'studyOfUseIds', name: 'Study of use', type: FilterType.MODAL,
                open(modal, request) {
                    return new Promise((resolve) => {
                        modal.open(StudyTreeComponent as Component, { windowClass: 'modal-extra-small', backdrop: 'static' })
                            .result.then((studyIds) => {
                            if (studyIds) {
                                this.value = studyIds.map((list) => list.name);
                                request[this.key] = studyIds.map((list) => list.id);
                            }
                            resolve();
                        }, () => {
                        });
                    });
                }
            },
            {
                key: 'studyOfOriginIds', name: 'Study of origin', type: FilterType.MODAL,
                open(modal, request) {
                    return new Promise((resolve) => {
                        modal.open(StudyTreeComponent as Component, { windowClass: 'modal-extra-small', backdrop: 'static' })
                            .result.then((studyIds) => {
                            if (studyIds) {
                                this.value = studyIds.map((list) => list.name);
                                request[this.key] = studyIds.map((list) => list.id);
                            }
                            resolve();
                        }, () => {
                        });
                    });
                }
            },
            {
                key: 'plantingStudyIds', name: 'Study of lot use', type: FilterType.MODAL,
                open(modal, request) {
                    return new Promise((resolve) => {
                        modal.open(StudyTreeComponent as Component, { windowClass: 'modal-extra-small', backdrop: 'static' })
                            .result.then((studyIds) => {
                            if (studyIds) {
                                this.value = studyIds.map((list) => list.name);
                                request[this.key] = studyIds.map((list) => list.id);
                            }
                            resolve();
                        }, () => {
                        });
                    });
                }
            },
            {
                key: 'harvestingStudyIds', name: 'Study of lot origin', type: FilterType.MODAL,
                open(modal, request) {
                    return new Promise((resolve) => {
                        modal.open(StudyTreeComponent as Component, { windowClass: 'modal-extra-small', backdrop: 'static' })
                            .result.then((studyIds) => {
                            if (studyIds) {
                                this.value = studyIds.map((list) => list.name);
                                request[this.key] = studyIds.map((list) => list.id);
                            }
                            resolve();
                        }, () => {
                        });
                    });
                }
            },
            { key: 'reference', name: 'Reference', placeholder: 'Contains Text', type: FilterType.TEXT },
            { key: 'breedingMethodName', name: 'Breeding Method Name', placeholder: 'Contains Text', type: FilterType.TEXT },
            {
                key: 'germplasmDate', name: 'Germplasm Date', type: FilterType.DATE,
                fromKey: 'germplasmDateFrom',
                toKey: 'germplasmDateTo',
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
            {
                key: 'femaleParentName', name: 'Cross-Female Parent Name', placeholder: 'Search Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS,
                matchType: MatchType.STARTSWITH
            },
            {
                key: 'maleParentName', name: 'Cross-Male Parent Name', placeholder: 'Search Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS,
                matchType: MatchType.STARTSWITH
            },
            {
                key: 'groupSourceName', name: 'Group Source Name', placeholder: 'Contains Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS,
                matchType: MatchType.STARTSWITH
            },
            {
                key: 'immediateSourceName', name: 'Immediate Source Name', placeholder: 'Contains Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS,
                matchType: MatchType.STARTSWITH
            },
            { key: 'withInventoryOnly', name: 'With Inventory Only', type: FilterType.BOOLEAN, value: true },
            { key: 'withRawObservationsOnly', name: 'With Observations Only', type: FilterType.BOOLEAN, value: true },
            { key: 'withSampleOnly', name: 'With Sample Only', type: FilterType.BOOLEAN, value: true },
            { key: 'withAnalyzedDataOnly', name: 'With Analyzed Data Only', type: FilterType.BOOLEAN, value: true },
            { key: 'inProgramListOnly', name: 'In Program List Only', type: FilterType.BOOLEAN, value: true },
            { key: 'includeGroupMembers', name: 'Include Group Members', type: FilterType.BOOLEAN, value: true },
            {
                key: 'includePedigree', name: 'Include Pedigree', type: FilterType.PEDIGREE_OPTIONS,
                pedigreeType: PedigreeType.GENERATIVE,
                value: 1, // Generation Level
                transform(req) {
                    ColumnFilterComponent.transformPedigreeOptionsFilter(this, req);
                },
                options: Promise.resolve([{
                    id: PedigreeType.GENERATIVE, name: 'Generative'
                }, {
                    id: PedigreeType.DERIVATIVE, name: 'Derivative and Maintenance'
                }, {
                    id: PedigreeType.BOTH, name: 'Both'
                }])
            },
            {
                key: 'attributes', name: 'Attributes', type: FilterType.ATTRIBUTES, attributes: [],
                transform(req) {
                    ColumnFilterComponent.transformAttributesFilter(this, req);
                },
                reset(req) {
                    ColumnFilterComponent.resetAttributesFilter(this, req);
                },
            },
            {
                key: 'nameTypes', name: 'Name Types', type: FilterType.NAME_TYPES, nameTypes: [],
                transform(req) {
                    ColumnFilterComponent.transformNameTypesFilter(this, req);
                },
                reset(req) {
                    ColumnFilterComponent.resetNameTypesFilter(this, req);
                },
            },
            {
                key: 'externalReferenceSource', name: 'External Reference Source', placeholder: 'Search Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS,
                matchType: MatchType.STARTSWITH
            },
            {
                key: 'externalReferenceId', name: 'External Reference ID', placeholder: 'Search Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS,
                matchType: MatchType.STARTSWITH
            }
        ];
    }

    get request() {
        return this.germplasmSearchRequest;
    }

    set request(request) {
        this.germplasmSearchRequest = request;
    }

    get filters() {
        return this.germplasmFilters;
    }

    set filters(filters) {
        this.germplasmFilters = filters;
    }

    get hiddenColumns() {
        return this.germplasmHiddenColumns;
    }

    set hiddenColumns(hiddenColumns) {
        this.germplasmHiddenColumns = hiddenColumns;
    }

    constructor(private activatedRoute: ActivatedRoute,
                private jhiLanguageService: JhiLanguageService,
                private eventManager: JhiEventManager,
                private germplasmService: GermplasmService,
                private router: Router,
                private alertService: AlertService,
                private modal: NgbModal,
                private activeModal: NgbActiveModal,
                private translateService: TranslateService,
                private paramContext: ParamContext,
                public germplasmDetailsUrlService: GermplasmDetailsUrlService) {

        this.predicate = '';
        this.routeData = this.activatedRoute.data.subscribe((data) => {
            this.page = data.pagingParams.page;
            this.previousPage = data.pagingParams.page;
            this.reverse = data.pagingParams.ascending;
            this.predicate = data.pagingParams.predicate;
        });
        this.paramContext.readParams();
        const queryParams = this.activatedRoute.snapshot.queryParams;
        const selectMultipleParamString = queryParams.selectMultiple;
        this.selectMultiple = selectMultipleParamString === undefined || (selectMultipleParamString.toString().trim().toLowerCase() === 'true');
        this.currentSearch = this.activatedRoute.snapshot && this.activatedRoute.snapshot.params['search'] ?
            this.activatedRoute.snapshot.params['search'] : '';

        if (!this.filters) {
            this.filters = GermplasmSelectorComponent.getInitialFilters();
            ColumnFilterComponent.reloadFilters(this.filters, this.request);
        }
        this.resultSearch = new SearchResult('');

    }

    search(request: GermplasmSearchRequest): Promise<string> {
        return new Promise((resolve, reject) => {
            if (!this.resultSearch.searchResultDbId) {
                this.germplasmService.search(request).subscribe((response) => {
                    this.resultSearch.searchResultDbId = response;
                    resolve(this.resultSearch.searchResultDbId);
                }, (error) => reject(error));
                this.page = 1;
            } else {
                resolve(this.resultSearch.searchResultDbId);
            }
        });
    }

    loadAll(request: GermplasmSearchRequest, successCallback?: () => void) {
        this.isLoading = true;
        this.search(request).then((searchId) => {
            this.germplasmService.getSearchResults(
                this.addSortParam({
                    searchRequestId: searchId,
                    page: this.page - 1,
                    size: this.itemsPerPage
                })
            ).pipe(finalize(() => {
                this.isLoading = false;
            })).subscribe(
                (res: HttpResponse<Germplasm[]>) => this.onSuccess(res.body, res.headers, successCallback),
                (res: HttpErrorResponse) => this.onError(res)
            );
        }, (error) => this.onError(error));
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
                this.addSortParam({
                    page: this.page,
                    size: this.itemsPerPage,
                    search: this.currentSearch,
                }), relativeTo: this.activatedRoute
        });
        this.loadAll(this.request);
    }

    ngOnInit() {
        this.loadAll(this.request);
        this.registerChangeInGermplasm();
        this.request.addedColumnsPropertyIds = [];
        this.hiddenColumns[ColumnLabels['GROUP ID']] = true;
        this.hiddenColumns[ColumnLabels['GERMPLASM DATE']] = true;
        this.hiddenColumns[ColumnLabels['METHOD ABBREV']] = true;
        this.hiddenColumns[ColumnLabels['METHOD NUMBER']] = true;
        this.hiddenColumns[ColumnLabels['METHOD GROUP']] = true;
        this.hiddenColumns[ColumnLabels['PREFERRED NAME']] = true;
        this.hiddenColumns[ColumnLabels['PREFERRED ID']] = true;
        this.hiddenColumns[ColumnLabels['GROUP SOURCE GID']] = true;
        this.hiddenColumns[ColumnLabels['GROUP SOURCE']] = true;
        this.hiddenColumns[ColumnLabels['IMMEDIATE SOURCE GID']] = true;
        this.hiddenColumns[ColumnLabels['IMMEDIATE SOURCE']] = true;
        this.hiddenColumns[ColumnLabels['FGID']] = true;
        this.hiddenColumns[ColumnLabels['CROSS-FEMALE PREFERRED NAME']] = true;
        this.hiddenColumns[ColumnLabels['MGID']] = true;
        this.hiddenColumns[ColumnLabels['CROSS-MALE PREFERRED NAME']] = true;
    }

    addSortParam(params) {
        const sort = this.predicate && this.predicate !== SORT_PREDICATE_NONE ? {
            sort: [this.getSort()]
        } : {};
        return Object.assign(params, sort);
    }

    getSort() {
        if (!this.predicate) {
            return '';
        }
        return [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
    }

    preSortCheck() {
        const isAddedColumn = this.request.addedColumnsPropertyIds.some((col) => col === this.predicate);
        const isHidden = this.hiddenColumns[this.predicate];
        if (isHidden || !ColumnLabels[this.predicate] && !isAddedColumn) {
            this.clearSort();
        }
    }

    clearSort() {
        this.predicate = SORT_PREDICATE_NONE;
        this.reverse = '';
        $('.fa-sort').removeClass('fa-sort-up fa-sort-down');
    }

    onClearSort($event) {
        $event.preventDefault();
        this.clearSort();
        this.transition();
    }

    /**
     * Sorting limits the response size, so it's important to reset
     */
    sort() {
        this.page = 1;
        this.transition();
    }

    trackId(index: number, item: Germplasm) {
        return item.gid;
    }

    registerChangeInGermplasm() {
        this.eventSubscriber = this.eventManager.subscribe('columnFiltersChanged', (event) => {

            this.preSortCheck();

            if (this.isExpensiveFilter()) {
                const confirmModalRef = this.modal.open(ModalConfirmComponent as Component);
                confirmModalRef.componentInstance.title = this.translateService.instant('search-germplasm.performance-warning.title');
                let message = this.translateService.instant('search-germplasm.performance-warning.text');
                message += this.getExpensiveFilterWarningList();
                confirmModalRef.componentInstance.message = message;
                confirmModalRef.result.then(() => {
                    this.resetTable();
                }, () => confirmModalRef.dismiss());

            } else {
                this.resetTable();
            }

        });
    }

    isExpensiveFilter() {
        return this.request && this.hasNameContainsFilters();
    }

    getExpensiveFilterWarningList() {
        let list = '';
        if (this.hasNameContainsFilters()) {
            list += '<li>name contains</li>';
        }
        return list;
    }

    private hasNameContainsFilters() {
        return this.request.nameFilter && this.request.nameFilter.type === MatchType.CONTAINS
            || this.request.femaleParentName && this.request.femaleParentName.type === MatchType.CONTAINS
            || this.request.maleParentName && this.request.maleParentName.type === MatchType.CONTAINS
            || this.request.groupSourceName && this.request.groupSourceName.type === MatchType.CONTAINS
            || this.request.immediateSourceName && this.request.immediateSourceName.type === MatchType.CONTAINS;
    }

    resetTable() {
        this.page = 1;
        this.previousPage = 1;
        this.isSelectAllPages = false;
        this.selectedItems = [];
        this.loadAll(this.request);
    }

    toggleAdditionalColumn(isVisible: boolean, columnPropertyId: string) {
        this.resultSearch.searchResultDbId = '';
        this.colVisPopOver.close();
        if (isVisible) {
            this.request.addedColumnsPropertyIds.push(columnPropertyId);
        } else {
            this.request.addedColumnsPropertyIds = this.request.addedColumnsPropertyIds.filter((e) => e !== columnPropertyId);
        }
        this.preSortCheck();
        this.resetTable();
    }

    private onSuccess(data, headers, callback) {
        this.filteredItems = headers.get('X-Filtered-Count');
        this.germplasmList = data;
        if (callback && typeof callback === 'function') {
            callback();
        }
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
    }

    isSelected(germplasm: Germplasm) {
        return germplasm && this.selectedItems.length > 0 && this.selectedItems.find((item) => item === germplasm.gid);
    }

    onSelectPage() {
        const isPageSelected = this.isPageSelected();
        const pageGids = this.germplasmList.map((germplasm) => germplasm.gid);
        if (isPageSelected) {
            this.selectedItems = this.selectedItems.filter((item) =>
                pageGids.indexOf(item) === -1);
        } else {
            this.selectedItems = pageGids.filter((item) =>
                this.selectedItems.indexOf(item) === -1
            ).concat(this.selectedItems);
        }
    }

    onSelectAllPages(isSelectAllPages) {
        this.isSelectAllPages = !isSelectAllPages;
        this.selectedItems = [];
    }

    toggleSelect(germplasm: Germplasm) {
        if (this.isSelectAllPages) {
            return;
        }
        if (this.selectedItems.length > 0 && this.selectedItems.find((item) => item === germplasm.gid)) {
            this.selectedItems = this.selectedItems.filter((item) => item !== germplasm.gid);
        } else if (!this.selectMultiple) {
            this.selectedItems = [germplasm.gid];
        } else {
            this.selectedItems.push(germplasm.gid);
        }
    }

    isPageSelected() {
        return this.germplasmList.length > 0 && !this.germplasmList.some((germplasm) => this.selectedItems.indexOf(germplasm.gid) === -1);
    }

    /**
     * Filter by different fields.
     * - Some filters may have a custom filtering logic
     * - Will clear all existing filters
     * @param filterBy map with key value filters
     */
    private filterBy(filterBy: { [p: string]: any }) {
        if (!filterBy) {
            return;
        }
        const entries = Object.entries(filterBy);
        if (entries.length === 0) {
            return;
        }
        this.resetFilters();
    }

    private resetFilters() {
        this.filters = GermplasmSelectorComponent.getInitialFilters();
        this.request = new GermplasmSearchRequest();
        this.resultSearch = new SearchResult('');
    }

    selectGermplasm() {
        if (this.isSelectAllPages) {
            this.handleSelectAllPages();
        } else {
            this.handleGidSelection();
        }

    }

    handleSelectAllPages() {
        this.itemsPerPage = this.filteredItems;
        this.page = 1;

        this.loadAll(this.request, () => {
            this.selectedItems = this.germplasmList.map((germplasm) => germplasm.gid);
            this.handleGidSelection();
        });
    }

    handleGidSelection() {
        // Handle selection when this page is loaded outside Angular.
        if ((<any>window.parent).onGidsSelected) {
            (<any>window.parent).onGidsSelected(this.selectedItems);
        }
        if ((<any>window.parent)) {
            (<any>window.parent).postMessage({ name: 'selector-changed', 'value': this.selectedItems }, '*');
        }
    }

    cancel() {
        // Handle closing of modal when this page is loaded outside of Angular.
        if ((<any>window.parent).closeModal) {
            (<any>window.parent).closeModal();
        }
        if ((<any>window.parent)) {
            (<any>window.parent).postMessage({ name: 'cancel', 'value': '' }, '*');
        }
    }

}
