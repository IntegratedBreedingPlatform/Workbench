import { Component, OnInit, ViewChild } from '@angular/core';
import { Germplasm } from '../entities/germplasm/germplasm.model';
import { GermplasmSearchRequest } from '../entities/germplasm/germplasm-search-request.model';
import { ActivatedRoute, Router } from '@angular/router';
import { ColumnFilterComponent, FilterType } from '../shared/column-filter/column-filter.component';
import { GermplasmService } from '../shared/germplasm/service/germplasm.service';
import { finalize } from 'rxjs/internal/operators/finalize';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { JhiAlertService, JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { NgbModal, NgbPopover } from '@ng-bootstrap/ng-bootstrap';
import { Subscription } from 'rxjs';
import { GermplasmTreeTableComponent } from '../shared/tree/germplasm/germplasm-tree-table.component';
import { StudyTreeComponent } from '../shared/tree/study/study-tree.component';
import { MatchType } from '../shared/column-filter/column-filter-text-with-match-options-component';
import { PedigreeType } from '../shared/column-filter/column-filter-pedigree-options-component';
import { SORT_PREDICATE_NONE } from './germplasm-search-resolve-paging-params';
import { PopupService } from '../shared/modal/popup.service';

declare var $: any;

@Component({
    selector: 'jhi-germplasm-search',
    templateUrl: './germplasm-search.component.html'
})
export class GermplasmSearchComponent implements OnInit {

    @ViewChild('colVisPopOver') public colVisPopOver: NgbPopover;
    eventSubscriber: Subscription;
    germplasmList: Germplasm[];
    error: any;
    currentSearch: string;
    routeData: any;
    links: any;
    totalItems: any;
    queryCount: any;
    itemsPerPage: any = 20;
    page: any;
    predicate: any;
    previousPage: any;
    reverse: any;

    isLoading: boolean;

    germplasmSearchRequest = new GermplasmSearchRequest();
    germplasmFilters: any;
    germplasmHiddenColumns = {};
    resultSearch: any = {};

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

    selectedItems: any[] = [];
    allItemsPerPages = false;

    private static getInitialFilters() {
        return [
            {
                key: 'nameFilter', name: 'Name', placeholder: 'Search Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS,
                matchType: MatchType.STARTSWITH,
                transform(req) {
                    ColumnFilterComponent.transformTextWithMatchOptionsFilter(this, req);
                },
                options: Promise.resolve([{
                    id: MatchType.STARTSWITH, name: 'Starts with'
                }, {
                    id: MatchType.EXACTMATCH, name: 'Exact Match'
                }, {
                    id: MatchType.CONTAINS, name: 'Contains'
                }])
            },
            { key: 'germplasmUUID', name: 'Germplasm UID', placeholder: 'Match Text', type: FilterType.TEXT },
            { key: 'gid', name: 'GID', placeholder: 'Match Text', type: FilterType.TEXT },
            { key: 'groupId', name: 'MGID', placeholder: 'Match Text', type: FilterType.TEXT },
            { key: 'sampleUID', name: 'Sample ID', placeholder: 'Match Text', type: FilterType.TEXT },
            {
                key: 'germplasmListIds', name: 'Germplasm List', type: FilterType.MODAL,
                open(modal, request) {
                    return new Promise((resolve) => {
                        modal.open(GermplasmTreeTableComponent as Component, { size: 'lg', backdrop: 'static' })
                            .result.then((germplasmList) => {
                            if (germplasmList) {
                                this.value = germplasmList.map((list) => list.name);
                                request[this.key] = germplasmList.map((list) => list.id);
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
                key: 'plantingStudyIds', name: 'Study of use', type: FilterType.MODAL,
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
                key: 'harvestingStudyIds', name: 'Study of origin', type: FilterType.MODAL,
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
                key: 'harvestDate', name: 'Harvest Date', type: FilterType.DATE,
                fromKey: 'harvestDateFrom',
                toKey: 'harvestDateTo',
                transform(req) {
                    ColumnFilterComponent.transformDateFilter(this, req, this.fromKey, this.toKey);
                },
                reset(req) {
                    ColumnFilterComponent.resetDateFilter(this, req, this.fromKey, this.toKey);
                },
                reload(req) {
                    this.from = req[this.fromKey];
                    this.to = req[this.toKey];
                }
            },
            { key: 'femaleParentName', name: 'Cross-Female Parent Name', placeholder: 'Contains Text', type: FilterType.TEXT },
            { key: 'maleParentName', name: 'Cross-Male Parent Name', placeholder: 'Contains Text', type: FilterType.TEXT },
            { key: 'groupSourceName', name: 'Group Source Name', placeholder: 'Contains Text', type: FilterType.TEXT },
            { key: 'immediateSourceName', name: 'Immediate Source Name', placeholder: 'Contains Text', type: FilterType.TEXT },
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
            }
        ];
    }

    constructor(private activatedRoute: ActivatedRoute,
                private jhiLanguageService: JhiLanguageService,
                private eventManager: JhiEventManager,
                private germplasmService: GermplasmService,
                private router: Router,
                private jhiAlertService: JhiAlertService,
                private modal: NgbModal,
                private popupService: PopupService) {

        this.predicate = '';
        this.routeData = this.activatedRoute.data.subscribe((data) => {
            this.page = data.pagingParams.page;
            this.previousPage = data.pagingParams.page;
            this.reverse = data.pagingParams.ascending;
            this.predicate = data.pagingParams.predicate;
        });
        this.currentSearch = this.activatedRoute.snapshot && this.activatedRoute.snapshot.params['search'] ?
            this.activatedRoute.snapshot.params['search'] : '';

        if (!this.filters) {
            this.filters = GermplasmSearchComponent.getInitialFilters();
            ColumnFilterComponent.reloadFilters(this.filters, this.request);
        }
    }

    loadAll(request: GermplasmSearchRequest) {
        this.isLoading = true;
        this.germplasmService.searchGermplasm(request,
            this.addSortParam({
                page: this.page - 1,
                size: this.itemsPerPage
            })
        ).pipe(finalize(() => {
            this.isLoading = false;
        })).subscribe(
            (res: HttpResponse<Germplasm[]>) => this.onSuccess(res.body, res.headers),
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
                this.addSortParam({
                    page: this.page,
                    size: this.itemsPerPage,
                    search: this.currentSearch,
                }), relativeTo: this.activatedRoute
        });
        this.loadAll(this.request);
    }

    ngOnInit() {
        this.registerChangeInGermplasm();
        this.registerClearSort();
        this.request.addedColumnsPropertyIds = [];
        this.loadAll(this.request);
        this.hiddenColumns['groupId'] = true;
        this.hiddenColumns['unit'] = true;
        this.hiddenColumns['germplasmDate'] = true;
        this.hiddenColumns['methodCode'] = true;
        this.hiddenColumns['methodNumber'] = true;
        this.hiddenColumns['methodGroup'] = true;
        this.hiddenColumns['germplasmPeferredName'] = true;
        this.hiddenColumns['germplasmPeferredId'] = true;
        this.hiddenColumns['groupSourceGID'] = true;
        this.hiddenColumns['groupSourcePreferredName'] = true;
        this.hiddenColumns['immediateSourceGID'] = true;
        this.hiddenColumns['immediateSourcePreferredName'] = true;
        this.hiddenColumns['femaleParentGID'] = true;
        this.hiddenColumns['femaleParentPreferredName'] = true;
        this.hiddenColumns['maleParentGID'] = true;
        this.hiddenColumns['maleParentPreferredName'] = true;
    }

    addSortParam(params) {
        let sort = this.predicate && this.predicate !== SORT_PREDICATE_NONE ? {
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

    clearSort() {
        this.predicate = SORT_PREDICATE_NONE;
        this.reverse = '';
        $('.fa-sort').removeClass('fa-sort-up fa-sort-down')
        this.transition();
    }


    onClearSort($event) {
        $event.preventDefault();
        this.clearSort();
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
            if (event && event.content && event.content.filterBy) {
                this.filterBy(event.content.filterBy);
            }
            this.resetTable();
        });
    }

    registerClearSort() {
        this.eventSubscriber = this.eventManager.subscribe('clearSort', (event) => {
            this.clearSort();
        });
    }

    resetTable() {
        this.page = 1;
        this.previousPage = 1;
        this.allItemsPerPages = false;
        this.selectedItems = [];
        this.loadAll(this.request);
    }

    toggleAdditionalColumn(isVisible: boolean, columnPropertyId: string) {
        this.colVisPopOver.close();
        if (isVisible) {
            this.request.addedColumnsPropertyIds.push(columnPropertyId);
        } else {
            this.request.addedColumnsPropertyIds = this.request.addedColumnsPropertyIds.filter((e) => e !== columnPropertyId);
        }
        this.resetTable();
    }

    private onSuccess(data, headers) {
        // filtering or sorting will limit the response
        if (headers.get('X-Filtered-Count') < 5000 || this.predicate !== SORT_PREDICATE_NONE) {
            this.totalItems = headers.get('X-Filtered-Count');
        } else {
            this.totalItems = headers.get('X-Total-Count');
        }
        this.queryCount = this.totalItems;
        // this.page = pagingParams.page;
        this.germplasmList = data;
    }

    private onError(response: HttpErrorResponse) {
        if (response.error && response.error.errors) {
            this.jhiAlertService.error('error.custom', {
                param: response.error.errors.map((err) => err.message).join('<br/>')
            }, null);
        } else {
            this.jhiAlertService.error(response.message, null, null);
        }
    }

    isSelected(germplasm: Germplasm) {
        return germplasm && this.selectedItems.length > 0 && this.selectedItems.find((item) => item === germplasm.gid);
    }

    onSelectAllPage() {
        const allPageSelected = this.isAllPageSelected();
        const lotCurrentPage = this.germplasmList.map((germplasm) => germplasm.gid);
        if (allPageSelected) {
            this.selectedItems = this.selectedItems.filter((item) =>
                lotCurrentPage.indexOf(item) === -1);
        } else {
            this.selectedItems = lotCurrentPage.filter((item) =>
                this.selectedItems.indexOf(item) === -1
            ).concat(this.selectedItems);
        }
    }

    onSelectAllPages(selectAllItems) {
        this.allItemsPerPages = !selectAllItems;
        if (this.allItemsPerPages) {
            this.selectedItems = [];
        }
    }

    toggleSelect(germplasm: Germplasm) {
        if (this.selectedItems.length > 0 && this.selectedItems.find((item) => item === germplasm.gid)) {
            this.selectedItems = this.selectedItems.filter((item) => item !== germplasm.gid);
        } else {
            this.selectedItems.push(germplasm.gid);
        }
    }

    isAllPageSelected() {
        return this.germplasmList.length > 0 && !this.germplasmList.some((germplasm) => this.selectedItems.indexOf(germplasm.gid) === -1);
    }

    /**
     * Going to the last page of a large table is expensive.
     */
    isFullPaginationEnabled() {
        return this.totalItems < 1000000;
    }

    private validateSelection() {
        if (this.germplasmList.length === 0 || (!this.allItemsPerPages && this.selectedItems.length === 0)) {
            this.jhiAlertService.error('error.custom', {
                param: 'Please select at least one germplasm'
            }, null);
            return false;
        }
        return true;
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
        this.filters = GermplasmSearchComponent.getInitialFilters();
        this.request = new GermplasmSearchRequest();
    }

}
