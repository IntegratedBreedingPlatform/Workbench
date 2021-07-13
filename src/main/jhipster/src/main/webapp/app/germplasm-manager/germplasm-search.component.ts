import { Component, OnInit, ViewChild } from '@angular/core';
import { Germplasm } from '../entities/germplasm/germplasm.model';
import { GermplasmSearchRequest } from '../entities/germplasm/germplasm-search-request.model';
import { ActivatedRoute, Router } from '@angular/router';
import { ColumnFilterComponent, FilterType } from '../shared/column-filter/column-filter.component';
import { GermplasmService } from '../shared/germplasm/service/germplasm.service';
import { finalize } from 'rxjs/internal/operators/finalize';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { NgbActiveModal, NgbModal, NgbPopover } from '@ng-bootstrap/ng-bootstrap';
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
import {
    IMPORT_GERMPLASM_PERMISSIONS, IMPORT_GERMPLASM_UPDATES_PERMISSIONS, GERMPLASM_LABEL_PRINTING_PERMISSIONS, DELETE_GERMPLASM_PERMISSIONS, GROUP_GERMPLASM_PERMISSIONS,
    UNGROUP_GERMPLASM_PERMISSIONS, CODE_GERMPLASM_PERMISSIONS
} from '../shared/auth/permissions';
import { AlertService } from '../shared/alert/alert.service';
import { ListBuilderContext } from '../shared/list-builder/list-builder.context';
import { ListEntry } from '../shared/list-builder/model/list.model';
import { KeySequenceRegisterDeletionDialogComponent } from './key-sequence-register/key-sequence-register-deletion-dialog.component';
import { GERMPLASM_LABEL_PRINTING_TYPE } from '../app.constants';
import { ParamContext } from '../shared/service/param.context';
import { SearchResult } from '../shared/search-result.model';
import { GermplasmGroupOptionsDialogComponent } from './grouping/germplasm-group-options-dialog-component';
import { GermplasmGroupingService } from '../shared/germplasm/service/germplasm-grouping.service';
import { GermplasmGroupingResultComponent } from './grouping/germplasm-grouping-result.component';
import { GermplasmCodingDialogComponent } from './coding/germplasm-coding-dialog.component';
import { GermplasmCodingResultDialogComponent } from './coding/germplasm-coding-result-dialog.component';
import { GermplasmCodeNameBatchResultModel } from '../shared/germplasm/model/germplasm-code-name-batch-result.model';

declare var $: any;

@Component({
    selector: 'jhi-germplasm-search',
    templateUrl: './germplasm-search.component.html'
})
export class GermplasmSearchComponent implements OnInit {

    IMPORT_GERMPLASM_PERMISSIONS = IMPORT_GERMPLASM_PERMISSIONS;
    IMPORT_GERMPLASM_UPDATES_PERMISSIONS = IMPORT_GERMPLASM_UPDATES_PERMISSIONS;
    GERMPLASM_LABEL_PRINTING_PERMISSIONS = GERMPLASM_LABEL_PRINTING_PERMISSIONS;
    DELETE_GERMPLASM_PERMISSIONS = DELETE_GERMPLASM_PERMISSIONS;
    GROUP_GERMPLASM_PERMISSIONS = GROUP_GERMPLASM_PERMISSIONS;
    UNGROUP_GERMPLASM_PERMISSIONS = UNGROUP_GERMPLASM_PERMISSIONS;
    CODE_GERMPLASM_PERMISSIONS = CODE_GERMPLASM_PERMISSIONS;

    ColumnLabels = ColumnLabels;

    @ViewChild('colVisPopOver') public colVisPopOver: NgbPopover;

    eventSubscriber: Subscription;
    germplasmDetailsEventSubscriber: Subscription;
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
    resultSearch: SearchResult;

    isLoading: boolean;

    germplasmSearchRequest = new GermplasmSearchRequest();
    germplasmFilters: any;
    germplasmHiddenColumns = {};

    // { <gid>: germplasm }
    selectedItems: { [key: number]: Germplasm } = {};
    isSelectAll = false;
    lastClickIndex: any;

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
                private germplasmGroupingService: GermplasmGroupingService,
                private router: Router,
                private alertService: AlertService,
                private translateService: TranslateService,
                private popupService: PopupService,
                private germplasmManagerContext: GermplasmManagerContext,
                private modalService: NgbModal,
                private activeModal: NgbActiveModal,
                public listBuilderContext: ListBuilderContext,
                private paramContext: ParamContext
    ) {

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
        this.listBuilderContext.pageSize = this.itemsPerPage;
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

    loadAll(request: GermplasmSearchRequest) {
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
                (res: HttpResponse<Germplasm[]>) => this.onSuccess(res.body, res.headers),
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
            queryParamsHandling: 'merge',
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
        this.registerGermplasmDetailsChanged();
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

    registerColumnFiltersChaged() {
        this.eventSubscriber = this.eventManager.subscribe('columnFiltersChanged', (event) => {

            this.preSortCheck();

            if (this.isExpensiveFilter()) {
                const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
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
            this.resetFilters();
            this.request.gids = event.content;
            ColumnFilterComponent.reloadFilters(this.filters, this.request);
            this.resetTable();
        });
    }

    registerGermplasmDetailsChanged() {
        // E.g germplasm changed via import.
        this.germplasmDetailsEventSubscriber = this.eventManager.subscribe('germplasmDetailsChanged', (event) => {
            // Reload the table when a germplasm is updated via germplasm details popup.
            this.transition();
        });
    }

    isExpensiveFilter() {
        return this.request && this.hasNameExpensiveFilters();
    }

    getExpensiveFilterWarningList() {
        let list = '';
        if (this.hasNameExpensiveFilters()) {
            list += '<li>name contains or ends with</li>';
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
        this.selectedItems = {};
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

    private resetFilters() {
        this.filters = GermplasmSearchComponent.getInitialFilters();
        this.request = new GermplasmSearchRequest();
        this.request.addedColumnsPropertyIds = [];
        this.resultSearch = new SearchResult('');
    }

    isSelected(germplasm: Germplasm) {
        return this.selectedItems[germplasm.gid];
    }

    onSelectPage() {
        if (this.isPageSelected()) {
            // remove all items
            this.germplasmList.forEach((g) => delete this.selectedItems[g.gid]);
        } else {
            // check remaining items
            this.germplasmList.forEach((g) => this.selectedItems[g.gid] = g);
        }
    }

    onSelectAll(isSelectAll) {
        this.isSelectAll = !isSelectAll;
        this.selectedItems = {};
    }

    toggleSelect($event, index, germplasm: Germplasm, checkbox = false) {
        if (this.isSelectAll) {
            return;
        }
        if (!$event.ctrlKey && !checkbox) {
            this.selectedItems = {};
        }
        let items;
        if ($event.shiftKey) {
            const max = Math.max(this.lastClickIndex, index) + 1,
                min = Math.min(this.lastClickIndex, index);
            items = this.germplasmList.slice(min, max);
        } else {
            items = [germplasm];
            this.lastClickIndex = index;
        }
        const isClickedItemSelected = this.selectedItems[germplasm.gid];
        for (const item of items) {
            if (isClickedItemSelected) {
                delete this.selectedItems[item.gid];
            } else {
                this.selectedItems[item.gid] = item;
            }
        }
    }

    isPageSelected() {
        return this.size(this.selectedItems) && this.germplasmList.every((g) => Boolean(this.selectedItems[g.gid]));
    }

    size(obj) {
        return Object.keys(obj).length;
    }

    private getSelectedItemIds() {
        return Object.keys(this.selectedItems).map((gid) => Number(gid));
    }

    private validateSelection() {
        if (this.germplasmList.length === 0 || (!this.isSelectAll && this.size(this.selectedItems) === 0)) {
            this.alertService.error('error.custom', {
                param: 'Please select at least one germplasm'
            });
            return false;
        }
        return true;
    }

    dragStart($event, dragged: Germplasm) {
        let selected;
        if (this.selectedItems[dragged.gid]) {
            // TODO sort as in table
            selected = Object.values(this.selectedItems).sort((a, b) => a.gid > b.gid ? 1 : -1);
        } else {
            selected = [dragged];
        }
        this.listBuilderContext.data = selected.map((germplasm: Germplasm) => {
            const row: ListEntry = new ListEntry();
            row[ColumnLabels.GID] = germplasm.gid;
            row[ColumnLabels.NAMES] = germplasm.names;
            row[ColumnLabels.AVAILABLE] = germplasm.availableBalance;
            // FIXME consolidate enum ColumnLabels with localization files
            //  Modify backend sorting mechanism if needed
            row['UNIT'] = germplasm.unit;
            row['LOTS'] = germplasm.lotCount;
            row[ColumnLabels.CROSS] = germplasm.pedigreeString;
            row['LOCATION'] = germplasm.locationName;
            row[ColumnLabels['METHOD NAME']] = germplasm.methodName;
            return row;
        });
    }

    dragEnd($event) {
    }

    toggleListBuilder() {
        this.listBuilderContext.visible = !this.listBuilderContext.visible;
    }

    openCreateList() {
        if (!this.validateSelection()) {
            return;
        }

        const searchComposite = new SearchComposite<GermplasmSearchRequest, number>();
        if (this.isSelectAll) {
            searchComposite.searchRequest = this.request;
        } else {
            searchComposite.itemIds = this.getSelectedItemIds();
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
            searchComposite.itemIds = this.getSelectedItemIds();
        }
        this.germplasmManagerContext.searchComposite = searchComposite;

        this.router.navigate(['/', { outlets: { popup: 'germplasm-list-add-dialog' }, }], {
            replaceUrl: true,
            queryParamsHandling: 'merge'
        });
    }

    exportDataAndLabels() {
        this.paramContext.resetQueryParams().then(() => {
            /*
             * FIXME workaround for history.back() with base-href
             *  Find solution for IBP-3534 / IBP-4177 that doesn't involve base-href
             *  or 'inventory-manager' string
             */
            window.history.pushState({}, '', window.location.hash);

            window.location.href = '/ibpworkbench/controller/jhipster#label-printing'
                + '?cropName=' + this.paramContext.cropName
                + '&programUUID=' + this.paramContext.programUUID
                + '&printingLabelType=' + GERMPLASM_LABEL_PRINTING_TYPE
                + '&searchRequestId=' + this.resultSearch.searchResultDbId;
        });
    }

    filterBySelectedRecords() {
        if (!this.validateSelection()) {
            return;
        }
        if (this.isSelectAll) {
            this.alertService.error('germplasm-filter-by-selected-records.filter-all-germplasm-not-supported');
            return;
        }
        this.eventManager.broadcast({ name: 'filterByGid', content: this.getSelectedItemIds() });
    }

    openGermplasmCoding() {
        if (!this.validateSelection()) {
            return;
        }
        if (this.isSelectAll) {
            this.alertService.error('germplasm-code.code-all-germplasm-not-supported');
            return;
        }
        if (this.size(this.selectedItems) > 500) {
            this.alertService.error('germplasm-code.too-many-selected-germplasm');
            return;
        }
        const germplasmCodingDialog = this.modalService.open(GermplasmCodingDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        germplasmCodingDialog.componentInstance.gids = this.getSelectedItemIds();
        germplasmCodingDialog.result.then((results) => {
            this.openGermplasmCodingResult(results);
            // Refresh the germplasm search table
            this.transition();
        });
    }

    openGermplasmCodingResult(results: GermplasmCodeNameBatchResultModel[]) {
        if (results) {
            const germplasmCodingResultDialog = this.modalService.open(GermplasmCodingResultDialogComponent as Component, { size: 'lg', backdrop: 'static' });
            germplasmCodingResultDialog.componentInstance.results = results;
        }
    }

    deleteGermplasm() {
        if (!this.validateSelection()) {
            return;
        }

        if (this.isSelectAll) {
            this.alertService.error('germplasm-delete.delete-all-germplasm-not-supported');
            return;
        }

        if (this.size(this.selectedItems) > 500) {
            this.alertService.error('germplasm-delete.too-many-selected-germplasm');
            return;
        }

        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.title = 'Delete Germplasm';
        confirmModalRef.componentInstance.message = 'Are you sure you want to delete the selected germplasm records from the database? '
            + 'The deletion will be permanent and can take a long time for germplasm included in lists - from which they will also be deleted.';
        confirmModalRef.result.then(() => {
            this.germplasmService.deleteGermplasm(this.getSelectedItemIds()).subscribe((response) => {
                if (response.germplasmWithErrors && response.germplasmWithErrors.length) {
                    this.alertService.warning('germplasm-delete.warning');
                    this.resetFilters();
                    // Show the germplasm that were not deleted because of validation
                    this.request.gids = response.germplasmWithErrors;
                    ColumnFilterComponent.reloadFilters(this.filters, this.request);
                } else {
                    this.alertService.success('germplasm-delete.success');
                }
                this.resetTable();

                // If there are deleted germplasm, show the Clear Prefix Key Cache Sequence Dialog
                if (response.deletedGermplasm && response.deletedGermplasm.length) {
                    this.openKeySequenceDeletionDialog(response.deletedGermplasm);
                }

            });
            this.activeModal.close();
        }, () => this.activeModal.dismiss());
    }

    openKeySequenceDeletionDialog(gids: number[]) {
        const confirmModalRef = this.modalService.open(KeySequenceRegisterDeletionDialogComponent as Component);
        confirmModalRef.componentInstance.gids = gids;
    }

    groupGermplasm() {
        if (!this.validateSelection()) {
            return;
        }
        if (this.isSelectAll) {
            this.alertService.error('germplasm-grouping.group-all-germplasm-not-supported');
            return;
        }
        if (this.size(this.selectedItems) > 500) {
            this.alertService.error('germplasm-grouping.too-many-selected-germplasm');
            return;
        }
        const groupGermplasmModal = this.modalService.open(GermplasmGroupOptionsDialogComponent as Component);
        groupGermplasmModal.componentInstance.gids = this.getSelectedItemIds();
        groupGermplasmModal.result.then(((germplasmGroupList) => {
            this.openGermplasmGroupingResult(germplasmGroupList)
        }));
    }

    openGermplasmGroupingResult(results) {
        if (results) {
            const germplasmGroupingResultComponent = this.modalService.open(GermplasmGroupingResultComponent as Component, { size: 'lg', backdrop: 'static' });
            germplasmGroupingResultComponent.componentInstance.results = results;
            germplasmGroupingResultComponent.result.then(() => {
                    this.hiddenColumns[ColumnLabels['GROUP ID']] = false;
                    this.transition();
                }
            );
        }
    }

    ungroupGermplasm() {
        if (!this.validateSelection()) {
            return;
        }

        if (this.isSelectAll) {
            this.alertService.error('germplasm-grouping.ungroup-all-germplasm-not-supported');
            return;
        }

        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component, { size: 'lg', backdrop: 'static' });
        confirmModalRef.componentInstance.title = 'Ungroup Germplasm';
        confirmModalRef.componentInstance.message = 'Are you sure you want to unfix/ungroup the selected germplasm? '
            + 'Ungrouping only applies to the the selected lines; other members of the group will not be affected.';
        confirmModalRef.result.then(() => {
            this.isLoading = true;
            this.germplasmGroupingService.ungroup(this.getSelectedItemIds()).subscribe((response) => {
                if (response.unfixedGids && response.unfixedGids.length > 0) {
                    if (response.numberOfGermplasmWithoutGroup === 0) {
                        this.alertService.success('germplasm-grouping.ungrouping.success');
                    } else {
                        this.alertService.warning('germplasm-grouping.ungrouping.warning', { param1: response.unfixedGids.length, param2: response.numberOfGermplasmWithoutGroup });
                    }
                    this.hiddenColumns[ColumnLabels['GROUP ID']] = false;
                    this.transition();
                } else {
                    this.alertService.warning('germplasm-grouping.ungrouping.none.successful');
                }
                this.isLoading = false;
            });
            this.activeModal.close();
        }, () => this.activeModal.dismiss());
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
