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
import { ModalConfirmComponent } from '../shared/modal/modal-confirm.component';
import { TranslateService } from '@ngx-translate/core';
import { formatErrorList } from '../shared/alert/format-error-list';
import { GermplasmManagerContext } from './germplasm-manager.context';
import { SearchComposite } from '../shared/model/search-composite';
import { IMPORT_GERMPLASM_UPDATES_PERMISSIONS } from '../shared/auth/permissions';
import { AlertService } from '../shared/alert/alert.service';

declare var $: any;

@Component({
    selector: 'jhi-germplasm-search',
    templateUrl: './germplasm-search.component.html'
})
export class GermplasmSearchComponent implements OnInit {

    IMPORT_GERMPLASM_UPDATES = [...IMPORT_GERMPLASM_UPDATES_PERMISSIONS];

    ColumnLabels = ColumnLabels;

    @ViewChild('colVisPopOver') public colVisPopOver: NgbPopover;
    @ViewChild(ColumnFilterComponent) public columnFilterComponent: ColumnFilterComponent;

    eventSubscriber: Subscription;
    germplasmList: Germplasm[];
    error: any;
    currentSearch: string;
    routeData: any;
    links: any;
    filteredItems: any;
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
    isSelectAll = false;

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
            }
        ];
    }

    constructor(private activatedRoute: ActivatedRoute,
                private jhiLanguageService: JhiLanguageService,
                private eventManager: JhiEventManager,
                private germplasmService: GermplasmService,
                private router: Router,
                private alertService: AlertService,
                private modal: NgbModal,
                private translateService: TranslateService,
                private popupService: PopupService,
                private germplasmManagerContext: GermplasmManagerContext) {

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
        this.registerColumnFiltersChaged();
        this.registerFilterBy();
        this.request.addedColumnsPropertyIds = [];
        this.loadAll(this.request);
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
        $('.fa-sort').removeClass('fa-sort-up fa-sort-down')
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

    registerColumnFiltersChaged() {
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

    registerFilterBy() {
        // E.g germplasm changed via import.
        this.eventSubscriber = this.eventManager.subscribe('filterByGid', (event) => {

            this.columnFilterComponent.clearFilters();

            // Get the existing gids filter
            const gidsFilter = this.filters.find((filter) => filter.key === 'gids');
            gidsFilter.value = event.content.join(',');

            // Manually add it to the filters and apply.
            this.columnFilterComponent.selectedFilter = gidsFilter.key;
            this.columnFilterComponent.AddFilter();
            this.columnFilterComponent.updateListFilter(gidsFilter);
        });
    }

    isExpensiveFilter() {
        return this.request && this.hasNameExpensiveFilters();
    }

    getExpensiveFilterWarningList() {
        let list = '';
        if (this.hasNameExpensiveFilters()) {
            list += '<li>name contains or ends with</li>'
        }
        return list;
    }

    private hasNameExpensiveFilters() {
        return this.request.nameFilter && (this.request.nameFilter.type === MatchType.CONTAINS || this.request.nameFilter.type === MatchType.ENDSWITH)
            || this.request.femaleParentName && (this.request.femaleParentName.type === MatchType.CONTAINS || this.request.femaleParentName.type === MatchType.ENDSWITH)
            || this.request.maleParentName && (this.request.maleParentName.type === MatchType.CONTAINS || this.request.maleParentName.type === MatchType.ENDSWITH)
            || this.request.groupSourceName && (this.request.groupSourceName.type === MatchType.CONTAINS || this.request.groupSourceName.type === MatchType.ENDSWITH)
            || this.request.immediateSourceName && (this.request.immediateSourceName.type === MatchType.CONTAINS || this.request.immediateSourceName.type === MatchType.ENDSWITH);
    }

    hasIncludedGids() {
        return this.request && (
            this.request.includePedigree
            || this.request.includeGroupMembers
        );
    }

    resetTable() {
        this.page = 1;
        this.previousPage = 1;
        this.isSelectAll = false;
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
        this.preSortCheck();
        this.resetTable();
    }

    private onSuccess(data, headers) {
        this.filteredItems = headers.get('X-Filtered-Count');
        this.germplasmList = data;
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

    onSelectAll(isSelectAll) {
        this.isSelectAll = !isSelectAll;
        if (this.isSelectAll) {
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

    isPageSelected() {
        return this.germplasmList.length > 0 && !this.germplasmList.some((germplasm) => this.selectedItems.indexOf(germplasm.gid) === -1);
    }

    private validateSelection() {
        if (this.germplasmList.length === 0 || (!this.isSelectAll && this.selectedItems.length === 0)) {
            this.alertService.error('error.custom', {
                param: 'Please select at least one germplasm'
            });
            return false;
        }
        return true;
    }

    openCreateList() {
        if (!this.validateSelection()) {
            return;
        }

        const searchComposite = new SearchComposite<GermplasmSearchRequest, number>();
        if (this.isSelectAll) {
            searchComposite.searchRequest = this.request;
        } else {
            searchComposite.itemIds = this.selectedItems;
        }
        this.germplasmManagerContext.searchComposite = searchComposite;

        this.router.navigate(['/', { outlets: { popup: 'germplasm-list-creation-dialog' }, }], {
            replaceUrl: true,
            queryParamsHandling: 'merge'
        });
    }

    openAddToList() {
        if (!this.validateSelection()) {
            return;
        }

        const searchComposite = new SearchComposite<GermplasmSearchRequest, number>();
        if (this.isSelectAll) {
            searchComposite.searchRequest = this.request;
        } else {
            searchComposite.itemIds = this.selectedItems;
        }
        this.germplasmManagerContext.searchComposite = searchComposite;

        this.router.navigate(['/', { outlets: { popup: 'germplasm-list-add-dialog' }, }], {
            replaceUrl: true,
            queryParamsHandling: 'merge'
        });
    }
}

export enum ColumnLabels {
    'GID' = 'GID',
    'GROUP ID' = 'GROUP ID',
    'NAMES' = 'NAMES',
    'AVAILABLE' = 'AVAILABLE',
    'LOT_UNITS' = 'LOT_UNITS',
    'LOTS' = 'LOTS',
    'CROSS' = 'CROSS',
    'PREFERRED ID' = 'PREFERRED ID',
    'PREFERRED NAME' = 'PREFERRED NAME',
    'GERMPLASM DATE' = 'GERMPLASM DATE',
    'LOCATIONS' = 'LOCATIONS',
    'METHOD NAME' = 'METHOD NAME',
    'METHOD ABBREV' = 'METHOD ABBREV',
    'METHOD NUMBER' = 'METHOD NUMBER',
    'METHOD GROUP' = 'METHOD GROUP',
    'FGID' = 'FGID',
    'CROSS-FEMALE PREFERRED NAME' = 'CROSS-FEMALE PREFERRED NAME',
    'MGID' = 'MGID',
    'CROSS-MALE PREFERRED NAME' = 'CROSS-MALE PREFERRED NAME',
    'GROUP SOURCE GID' = 'GROUP SOURCE GID',
    'GROUP SOURCE' = 'GROUP SOURCE',
    'IMMEDIATE SOURCE GID' = 'IMMEDIATE SOURCE GID',
    'IMMEDIATE SOURCE' = 'IMMEDIATE SOURCE',
}
