import { Component, Input, OnInit } from '@angular/core';
import { GermplasmListService } from '../shared/germplasm-list/service/germplasm-list.service';
import { GermplasmListSearchResponse } from '../shared/germplasm-list/model/germplasm-list-search-response.model';
import { Subscription } from 'rxjs';
import { GermplasmListDataSearchResponse } from '../shared/germplasm-list/model/germplasm-list-data-search-response.model';
import { FilterType } from '../shared/column-filter/column-filter.component';
import { finalize } from 'rxjs/internal/operators/finalize';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { SORT_PREDICATE_NONE } from '../germplasm-manager/germplasm-search-resolve-paging-params';
import { formatErrorList } from '../shared/alert/format-error-list';
import { ActivatedRoute, Router } from '@angular/router';
import { JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { AlertService } from '../shared/alert/alert.service';
import { GermplasmListModel } from '../shared/germplasm-list/model/germplasm-list.model';
import { GermplasmListSearchComponent } from './germplasm-list-search.component';
import { Principal } from '../shared';
import { GermplasmListObservationVariable } from '../shared/germplasm-list/model/germplasm-list-observation-variable.model';
import { GermplasmListColumnCategory } from '../shared/germplasm-list/model/germplasm-list-column-category.type';
import { GermplasmListColumnModel } from './list-columns.component';
import { GermplasmListDataUpdateViewRequest } from '../shared/germplasm-list/model/germplasm-list-data-update-view-request.model';
import { MatchType } from '../shared/column-filter/column-filter-text-with-match-options-component';
import { VariableTypeEnum } from '../shared/ontology/variable-type.enum';
import { VariableDetails } from '../shared/ontology/model/variable-details';
import { ModalConfirmComponent } from '../shared/modal/modal-confirm.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { MANAGE_GERMPLASM_LIST_PERMISSION } from '../shared/auth/permissions';
import { SearchResult } from '../shared/search-result.model';
import { GERMPLASM_LIST_LABEL_PRINTING_TYPE } from '../app.constants';
import { ParamContext } from '../shared/service/param.context';
import { GermplasmListReorderEntriesDialogComponent } from './reorder-entries/germplasm-list-reorder-entries-dialog.component';
import { SearchComposite } from '../shared/model/search-composite';
import { GermplasmSearchRequest } from '../entities/germplasm/germplasm-search-request.model';
import { GermplasmListDataSearchRequest } from '../entities/germplasm-list-data/germplasm-list-data-search-request.model';
import { GermplasmListMetadataComponent } from './germplasm-list-metadata.component';
import { GermplasmListManagerContext } from './germplasm-list-manager.context';
import { GermplasmListFolderSelectorComponent } from '../shared/tree/germplasm/germplasm-list-folder-selector.component';
import { TreeComponentResult } from '../shared/tree';
import { GermplasmTreeService } from '../shared/tree/germplasm/germplasm-tree.service';
import { TermIdEnum } from '../shared/ontology/model/termid.enum';
import { MetadataDetails } from '../shared/ontology/model/metadata-details';

declare var $: any;

@Component({
    selector: 'jhi-list',
    templateUrl: './list.component.html',
    providers: [{ provide: GermplasmTreeService, useClass: GermplasmTreeService }]
})
export class ListComponent implements OnInit {

    static readonly GERMPLASMLIST_REORDER_EVENT_SUFFIX = 'GermplasmListReordered';
    static readonly GERMPLASM_LIST_CHANGED = 'GermplasmListViewChanged';
    static readonly SORT_ENTRY_NO_VARIABLE = 'VARIABLE_8230';

    readonly TermIdEnum = TermIdEnum;

    IMPORT_GERMPLASM_LIST_UPDATES_PERMISSION = [...MANAGE_GERMPLASM_LIST_PERMISSION, 'IMPORT_GERMPLASM_LIST_UPDATES'];
    REORDER_ENTRIES_GERMPLASM_LISTS_PERMISSIONS = [...MANAGE_GERMPLASM_LIST_PERMISSION, 'REORDER_ENTRIES_GERMPLASM_LISTS'];
    GERMPLASM_LIST_LABEL_PRINTING_PERMISSIONS = [...MANAGE_GERMPLASM_LIST_PERMISSION, 'GERMPLASM_LIST_LABEL_PRINTING'];
    ADD_GERMPLASM_LIST_ENTRIES_PERMISSIONS = [...MANAGE_GERMPLASM_LIST_PERMISSION, 'ADD_GERMPLASM_LIST_ENTRIES'];
    ADD_ENTRIES_TO_LIST_PERMISSIONS = [...MANAGE_GERMPLASM_LIST_PERMISSION, 'ADD_ENTRIES_TO_LIST'];
    DELETE_LIST_PERMISSIONS = [...MANAGE_GERMPLASM_LIST_PERMISSION, 'DELETE_GERMPLASM_LIST'];
    CLONE_GERMPLASM_LIST_PERMISSIONS = [...MANAGE_GERMPLASM_LIST_PERMISSION, 'CLONE_GERMPLASM_LIST'];
    REMOVE_ENTRIES_GERMPLASM_LISTS_PERMISSIONS = [...MANAGE_GERMPLASM_LIST_PERMISSION, 'REMOVE_ENTRIES_GERMPLASM_LISTS'];
    // Used also for "move to folders" for now
    EDIT_LIST_METADATA_PERMISSIONS = [...MANAGE_GERMPLASM_LIST_PERMISSION, 'EDIT_LIST_METADATA'];
    LOCK_UNLOCK_PERMISSIONS = [...MANAGE_GERMPLASM_LIST_PERMISSION, 'LOCK_UNLOCK_GERMPLASM_LIST'];

    ACTION_BUTTON_PERMISSIONS = [
        ...MANAGE_GERMPLASM_LIST_PERMISSION,
        'ADD_ENTRIES_TO_LIST',
        'CLONE_GERMPLASM_LIST',
        'GERMPLASM_LIST_LABEL_PRINTING'
    ];

    ACTION_ITEM_PERMISSIONS_WITH_LOCK_RESTRICTION = [
        'IMPORT_GERMPLASM_LIST_UPDATES',
        'REORDER_ENTRIES_GERMPLASM_LISTS',
        'ADD_GERMPLASM_LIST_ENTRIES',
        'REMOVE_ENTRIES_GERMPLASM_LISTS'
    ];

    readonly STATIC_FILTERS = {
        ENTRY_NO: {
            key: 'entryNumbers', type: FilterType.LIST, category: GermplasmListColumnCategory.STATIC
        },
        GID: {
            key: 'gids', type: FilterType.LIST, category: GermplasmListColumnCategory.STATIC
        },
        GUID: {
            key: 'germplasmUUID', placeholder: 'Match Text', type: FilterType.TEXT, category: GermplasmListColumnCategory.STATIC
        },
        GROUP_ID: {
            key: 'groupId', placeholder: 'Match Text', type: FilterType.TEXT, category: GermplasmListColumnCategory.STATIC
        },
        DESIGNATION: {
            key: 'designationFilter', placeholder: 'Search Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS, matchType: MatchType.STARTSWITH,
            category: GermplasmListColumnCategory.STATIC
        },
        IMMEDIATE_SOURCE_NAME: {
            key: 'immediateSourceName', placeholder: 'Contains Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS, matchType: MatchType.STARTSWITH,
            category: GermplasmListColumnCategory.STATIC

        },
        GROUP_SOURCE_NAME: {
            key: 'groupSourceName', placeholder: 'Contains Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS, matchType: MatchType.STARTSWITH
            , category: GermplasmListColumnCategory.STATIC
        },
        FEMALE_PARENT_NAME: {
            key: 'femaleParentName', placeholder: 'Search Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS, matchType: MatchType.STARTSWITH
            , category: GermplasmListColumnCategory.STATIC
        },
        MALE_PARENT_NAME: {
            key: 'maleParentName', placeholder: 'Search Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS, matchType: MatchType.STARTSWITH,
            category: GermplasmListColumnCategory.STATIC
        },
        BREEDING_METHOD_PREFERRED_NAME: {
            key: 'breedingMethodName', placeholder: 'Contains Text', type: FilterType.TEXT, category: GermplasmListColumnCategory.STATIC
        },
        BREEDING_METHOD_ABBREVIATION: {
            key: 'breedingMethodAbbreviation', placeholder: 'Contains Text', type: FilterType.TEXT, category: GermplasmListColumnCategory.STATIC
        },
        BREEDING_METHOD_GROUP: {
            key: 'breedingMethodGroup', placeholder: 'Contains Text', type: FilterType.TEXT, category: GermplasmListColumnCategory.STATIC
        },
        LOCATION_NAME: {
            key: 'locationName', placeholder: 'Contains Text', type: FilterType.TEXT, category: GermplasmListColumnCategory.STATIC
        },
        LOCATION_ABBREVIATION: {
            key: 'locationAbbreviation', placeholder: 'Contains Text', type: FilterType.TEXT, category: GermplasmListColumnCategory.STATIC
        },
        GERMPLASM_DATE: {
            key: 'germplasmDate', type: FilterType.DATE, fromKey: 'germplasmDateFrom', toKey: 'germplasmDateTo', category: GermplasmListColumnCategory.STATIC
        },
        GERMPLASM_REFERENCE: {
            key: 'reference', placeholder: 'Contains Text', type: FilterType.TEXT, category: GermplasmListColumnCategory.STATIC
        }
    };

    public isCollapsed = false;
    variables: VariableDetails[];
    selectedVariables: { [key: number]: VariableDetails } = {};
    title = 'Entry details';

    @Input()
    listId: number;

    itemsPerPage = 20;

    user?: any;

    germplasmList: GermplasmListModel;
    header: GermplasmListObservationVariable[];
    entries: GermplasmListDataSearchResponse[];
    eventSubscriber: Subscription;

    currentSearch: string;
    totalItems: number;
    page: number;
    previousPage: number;
    predicate: any;
    isAscending: boolean;
    resultSearch: SearchResult;
    isLoading: boolean;

    germplasmListFilters: any;

    selectedItems: Map<number, GermplasmListDataSearchResponse> = new Map<number, GermplasmListDataSearchResponse>();
    isSelectAll: boolean;
    lastClickIndex: any;

    generationLevels = Array.from(Array(10).keys()).map((k) => k + 1);
    generationLevel = 1;

    constructor(private activatedRoute: ActivatedRoute,
                private jhiLanguageService: JhiLanguageService,
                private eventManager: JhiEventManager,
                private germplasmListService: GermplasmListService,
                private germplasmTreeService: GermplasmTreeService,
                private router: Router,
                private alertService: AlertService,
                public principal: Principal,
                private modalService: NgbModal,
                public translateService: TranslateService,
                private paramContext: ParamContext,
                private germplasmListManagerContext: GermplasmListManagerContext
    ) {
        this.page = 1;
        this.totalItems = 0;
        this.currentSearch = '';
        this.resultSearch = new SearchResult('');
        this.isSelectAll = false;
        this.setDefaultSort();
    }

    async ngOnInit() {
        this.variables = [];
        const identity = await this.principal.identity();
        this.user = identity;

        this.germplasmListService.getGermplasmListById(this.listId).subscribe(
            (res: HttpResponse<GermplasmListModel>) => {
                this.germplasmList = res.body
                if (this.germplasmList.generationLevel) {
                    this.generationLevel = this.germplasmList.generationLevel;
                }
            },
            (res: HttpErrorResponse) => this.onError(res)
        );

        this.registerEvents();
        this.load();
    }

    async load() {
        await this.loadEntryDetails();
        this.refreshTable();
    }

    private loadEntryDetails() {
        return this.germplasmListService.getVariables(this.listId, VariableTypeEnum.ENTRY_DETAILS).toPromise().then(
            (res: HttpResponse<VariableDetails[]>) => {
                res.body.forEach((variable) => {
                    const metadataDetails = new MetadataDetails();
                    variable.metadata = metadataDetails;
                    variable.metadata.deletable = TermIdEnum.ENTRY_NO !== Number(variable.id);
                })
                this.variables = this.sortByUndeletable(res.body);
            },
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    sortByUndeletable(variables) {
        return variables.sort((a, b) => {
                return Number(a.metadata.deletable) < Number(b.metadata.deletable) ? -1 : 1
            }
        );
    }

    refreshTable() {
        this.germplasmListService.getGermplasmListDataTableHeader(this.listId).subscribe(
            (res: HttpResponse<GermplasmListObservationVariable[]>) => this.onGetTableHeaderSuccess(res.body),
            (res: HttpErrorResponse) => this.onError(res));
    }

    get filters() {
        return this.germplasmListFilters;
    }

    set filters(filters) {
        this.germplasmListFilters = filters;
    }

    postSearch(): Promise<string> {
        return new Promise((resolve, reject) => {
            const request = this.mapFiltersToRequest();
            this.germplasmListService.postSearchListData(this.listId, request).subscribe((response: string) => {
                this.resultSearch.searchResultDbId = response;
                resolve(this.resultSearch.searchResultDbId);
            }, (error) => reject(error));
        });
    }

    loadAll() {
        this.isLoading = true;
        this.postSearch().then((searchId: string) => {
            this.germplasmListService.getSearchResults(
                this.listId,
                {
                    page: this.page - 1,
                    size: this.itemsPerPage,
                    sort: this.getSort(),
                    searchRequestId: searchId
                }
            ).pipe(finalize(() => {
                this.isLoading = false;
            })).subscribe(
                (res: HttpResponse<GermplasmListDataSearchResponse[]>) => this.onSearchSuccess(res.body, res.headers),
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
        this.router.navigate([`./list/${this.listId}`], {
            queryParamsHandling: 'merge',
            queryParams: {
                page: this.page,
                size: this.itemsPerPage,
                search: this.currentSearch,
                listId: this.listId,
                sort: this.getSort()
            },
            relativeTo: this.activatedRoute
        });
        this.loadAll();
    }

    onColumnsSelected(columns: GermplasmListColumnModel[]) {
        const request = this.mapSelectedColumnsToUpdateViewRequest(columns);
        this.germplasmListService.updateGermplasmListDataView(this.listId, request).subscribe(
            (res: HttpResponse<any>) => this.refreshTable(),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    onClearSort($event) {
        $event.preventDefault();
        this.clearSort();
        this.transition();
    }

    trackId(index: number, item: GermplasmListSearchResponse) {
        return item.listId;
    }

    registerEvents() {
        this.eventSubscriber = this.eventManager.subscribe(this.listId + ListComponent.GERMPLASM_LIST_CHANGED, (event) => {
            this.load();
        });

        this.eventManager.subscribe(ListComponent.GERMPLASMLIST_REORDER_EVENT_SUFFIX, (event) => {
            this.clearSelectedItems();
            this.loadAll();
        });

        this.eventSubscriber = this.eventManager.subscribe('germplasmSelectorSelected', (event) => {
            if (this.listId === this.germplasmListManagerContext.activeGermplasmListId) {
                const searchComposite = new SearchComposite<GermplasmSearchRequest, number>();
                searchComposite.itemIds = event.content.split(',');
                this.germplasmListService.addGermplasmEntriesToList(this.listId, searchComposite)
                    .pipe(finalize(() => {
                        this.isLoading = false;
                    })).subscribe(
                    (res: void) => {
                        this.eventManager.broadcast({ name: 'addToGermplasmList', content: this.listId });
                    },
                    (res: HttpErrorResponse) => this.onError(res)
                );
            }
        });

        this.eventSubscriber = this.eventManager.subscribe('addToGermplasmList', (event) => {
            if (this.listId === event.content) {
                this.load();
            }
        });
    }

    resetTable() {
        this.page = 1;
        this.previousPage = 1;
        this.loadAll();
    }

    toggleListStatus() {
        this.germplasmListService.toggleGermplasmListStatus(this.listId).subscribe(
            (res: boolean) => this.onToggleListStatusSuccess(res),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    getHeaderDisplayName(column: GermplasmListObservationVariable): string {
        if (this.isStaticColumn(column.columnCategory)) {
            return column.name;
        }
        return column.alias ? column.alias : column.name;
    }

    getColumnSortName(column: GermplasmListObservationVariable): string {
        if (this.isStaticColumn(column.columnCategory)) {
            return column.alias;
        }
        return column.columnCategory + '_' + column.termId;
    }

    isColumnFilterable(column: GermplasmListObservationVariable): boolean {
        return (this.isStaticColumn(column.columnCategory) && this.STATIC_FILTERS[column.alias]) || this.isNotStaticColumn(column.columnCategory);
    }

    isColumnSortable(column: GermplasmListObservationVariable): boolean {
        return !(column.alias === ColumnAlias.MALE_PARENT_NAME ||
            column.alias === ColumnAlias.FEMALE_PARENT_NAME ||
            column.alias === ColumnAlias.LOTS ||
            column.alias === ColumnAlias.AVAILABLE ||
            column.alias === ColumnAlias.UNIT ||
            column.alias === ColumnAlias.CROSS ||
            column.alias === ColumnAlias.MALE_PARENT_GID ||
            column.alias === ColumnAlias.FEMALE_PARENT_GID
        );
    }

    applyFilters() {
        this.resetTable();
    }

    resetFilters() {
        this.loadAll();
    }

    getFilter(column: GermplasmListObservationVariable) {
        if (this.isStaticColumn(column.columnCategory)) {
            return this.filters[column.alias];
        }
        return this.filters[this.getNotStaticFilterKey(column)];
    }

    exportDataAndLabels() {
        this.paramContext.resetQueryParams('/germplasm-list/').then(() => {
            /*
             * FIXME workaround for history.back() with base-href
             *  Find solution for IBP-3534 / IBP-4177 that doesn't involve base-href
             *  or 'inventory-manager' string
             */
            window.history.pushState({}, '', window.location.hash);

            window.location.href = '/ibpworkbench/controller/jhipster#label-printing'
                + '?cropName=' + this.paramContext.cropName
                + '&programUUID=' + this.paramContext.programUUID
                + '&printingLabelType=' + GERMPLASM_LIST_LABEL_PRINTING_TYPE
                + '&listId=' + this.listId;
        });
    }

    openCloneGermplasmList() {
        this.router.navigate(['/', { outlets: { popup: 'germplasm-list-clone-dialog' }, }], {
            replaceUrl: true,
            queryParamsHandling: 'merge'
        });
    }

    isPageSelected() {
        return this.size() && this.entries.every((entry: GermplasmListDataSearchResponse) => Boolean(this.selectedItems.get(entry.listDataId)));
    }

    // TODO parameterize
    size() {
        return this.selectedItems.size;
    }

    onSelectPage() {
        if (this.isPageSelected()) {
            // remove all items
            this.entries.forEach((entry: GermplasmListDataSearchResponse) => this.selectedItems.delete(entry.listDataId));
        } else {
            // check remaining items
            this.entries.forEach((entry: GermplasmListDataSearchResponse) => this.selectedItems.set(entry.listDataId, entry));
        }
    }

    isSelected(entry: GermplasmListDataSearchResponse) {
        return this.selectedItems.get(entry.listDataId);
    }

    toggleSelect($event, index, entry: GermplasmListDataSearchResponse, checkbox = false) {
        if (this.isSelectAll) {
            return;
        }
        if (!$event.ctrlKey && !checkbox) {
            this.selectedItems.clear();
        }
        let items;
        if ($event.shiftKey) {
            const max = Math.max(this.lastClickIndex, index) + 1,
                min = Math.min(this.lastClickIndex, index);
            items = this.entries.slice(min, max);
        } else {
            items = [entry];
            this.lastClickIndex = index;
        }
        const isClickedItemSelected = this.selectedItems.get(entry.listDataId);
        for (const item of items) {
            if (isClickedItemSelected) {
                this.selectedItems.delete(item.listDataId);
            } else {
                this.selectedItems.set(item.listDataId, item);
            }
        }
    }

    clearSelectedItems() {
        this.selectedItems.clear();
    }

    openReorderEntries() {
        if (!this.validateSelection()) {
            return;
        }

        const reOrderEntriesModal = this.modalService.open(GermplasmListReorderEntriesDialogComponent as Component);
        reOrderEntriesModal.componentInstance.listId = this.listId;
        reOrderEntriesModal.componentInstance.selectedEntries = this.getSelectedItemIds();
    }

    async calculateCop(reset = false) {
        if (this.entries.length === 0 || (this.size() === 0)) {
            this.alertService.error('germplasm-list.list-data.cop.no.entries.error');
            return false;
        }

        // listIdModalParam: different name to avoid clearing up listId component query param
        this.router.navigate(['/', { outlets: { popup: 'cop-matrix' } }], {
            queryParamsHandling: 'merge',
            queryParams: {
                gids: Array.from(this.selectedItems.values()).map((l) => l.data[ColumnAlias.GID]).join(','),
                calculate: true,
                listIdModalParam: null,
                reset
            }
        });
    }

    viewCop() {
        if (this.entries.length === 0 || (this.size() === 0)) {
            this.alertService.error('germplasm-list.list-data.cop.no.entries.error');
            return false;
        }

        this.router.navigate(['/', { outlets: { popup: 'cop-matrix' } }], {
            queryParamsHandling: 'merge',
            queryParams: {
                gids: Object.values(this.selectedItems).map((l) => l.data[ColumnAlias.GID]).join(','),
                calculate: false,
                listIdModalParam: null
            }
        });
    }

    async calculateCopForList() {
        this.router.navigate(['/', { outlets: { popup: 'cop-matrix' } }], {
            queryParamsHandling: 'merge',
            queryParams: {
                gids: null,
                listIdModalParam: this.listId,
                calculate: true
            }
        });
    }

    viewCopForList() {
        this.router.navigate(['/', { outlets: { popup: 'cop-matrix' } }], {
            queryParamsHandling: 'merge',
            queryParams: {
                gids: null,
                calculate: false,
                listIdModalParam: this.listId
            }
        });
    }

    openGermplasmSelectorModal() {
        this.router.navigate(['/', { outlets: { popup: 'germplasm-selector-dialog' } }], {
            queryParamsHandling: 'merge',
            queryParams: {
                cropName: this.paramContext.cropName,
                loggedInUserId: this.paramContext.loggedInUserId,
                programUUID: this.paramContext.programUUID,
                selectMultiple: true
            }
        });
    }

    openEditListMetadata() {
        const editListMetadataModal = this.modalService.open(GermplasmListMetadataComponent as Component);
        editListMetadataModal.componentInstance.listId = this.listId;
    }

    openAddToList() {
        if (!this.validateSelection()) {
            return;
        }
        const searchRequest = new GermplasmListDataSearchRequest();
        searchRequest.entryNumbers = [];
        this.getSelectedItemIds().forEach((selectedItemId) => {
            searchRequest.entryNumbers.push(this.selectedItems.get(selectedItemId).data[ListComponent.SORT_ENTRY_NO_VARIABLE]);
        });
        const searchComposite = new SearchComposite<GermplasmListDataSearchRequest, number>();
        searchComposite.searchRequest = searchRequest;
        this.germplasmListManagerContext.searchComposite = searchComposite;
        this.germplasmListManagerContext.activeGermplasmListId = this.listId;
        this.router.navigate(['/', { outlets: { popup: 'germplasm-list-add-dialog' }, }], {
            replaceUrl: true,
            queryParamsHandling: 'merge'
        });
    }

    deleteList() {
        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.message = this.translateService.instant('germplasm-list.list-data.delete-list.message');
        confirmModalRef.componentInstance.title = this.translateService.instant('germplasm-list.list-data.delete-list.header');

        confirmModalRef.result.then(() => {
            this.submitDeleteList();
        }, () => confirmModalRef.dismiss());
    }

    submitDeleteList() {
        this.germplasmListService.deleteGermplasmList(this.listId)
            .subscribe(
                () => {
                    this.eventManager.broadcast({ name: 'germplasmListDeleted', content: this.listId });
                },
                (error) => this.onError(error)
            );
    }

    moveToFolder() {
        const modal = this.modalService.open(GermplasmListFolderSelectorComponent as Component, { size: 'lg', backdrop: 'static' });
        modal.result.then((selectedNodes: TreeComponentResult[]) => {
            const node = selectedNodes[0];
            this.germplasmTreeService.move(String(this.listId), String(node.id)).subscribe(
                () => this.alertService.success('germplasm-list.list-data.move-to-folder.success'),
                (error) => this.onError(error)
            );
        });
    }

    private getFilters() {
        const filters = this.STATIC_FILTERS;
        this.header.filter((value: GermplasmListObservationVariable) => this.isNotStaticColumn(value.columnCategory))
            .forEach((column: GermplasmListObservationVariable) => {
                const key = this.getNotStaticFilterKey(column);
                filters[key] = {
                    key,
                    placeholder: 'Contains Text',
                    type: FilterType.TEXT,
                    category: column.columnCategory,
                    termId: column.termId,
                    variableType: column.variableType
                };
            });
        return filters;
    }

    private getNotStaticFilterKey(column: GermplasmListObservationVariable): string {
        return `${column.columnCategory}_${column.termId}`;
    }

    private onSearchSuccess(data: GermplasmListDataSearchResponse[], headers) {
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

    private onGetTableHeaderSuccess(header: GermplasmListObservationVariable[]) {
        this.header = header;

        if (!this.isSortColumnExists()) {
            this.setDefaultSort();
        }

        this.filters = this.getFilters();
        this.loadAll();
    }

    private isSortColumnExists(): boolean {
        const sortColumnExists = this.header.filter(
            (column: GermplasmListObservationVariable) => {
                if (this.isStaticColumn(column.columnCategory)) {
                    return column.alias === this.predicate;
                } else {
                    return this.getNotStaticFilterKey(column) === this.predicate;
                }
            });
        return sortColumnExists.length !== 0;
    }

    private getSort() {
        if (this.predicate === SORT_PREDICATE_NONE) {
            return '';
        }
        return [this.predicate + ',' + (this.isAscending ? 'asc' : 'desc')];
    }

    private clearSort() {
        this.predicate = SORT_PREDICATE_NONE;
        this.isAscending = false;
        $('.fa-sort').removeClass('fa-sort-up fa-sort-down');
    }

    private setDefaultSort() {
        this.predicate = ListComponent.SORT_ENTRY_NO_VARIABLE;
        this.isAscending = true;
    }

    private isStaticColumn(category: GermplasmListColumnCategory): boolean {
        return category === GermplasmListColumnCategory.STATIC;
    }

    private isNotStaticColumn(category: GermplasmListColumnCategory): boolean {
        return category !== GermplasmListColumnCategory.STATIC;
    }

    private isNamesColumn(category: GermplasmListColumnCategory): boolean {
        return category === GermplasmListColumnCategory.NAMES;
    }

    private isDescriptorColumn(variableType: VariableTypeEnum): boolean {
        return variableType && (variableType.toString() === VariableTypeEnum[VariableTypeEnum.GERMPLASM_PASSPORT]
            || variableType.toString() === VariableTypeEnum[VariableTypeEnum.GERMPLASM_ATTRIBUTE]);
    }

    private isEntryDetailColumn(variableType: VariableTypeEnum): boolean {
        return variableType && (variableType.toString() !== VariableTypeEnum[VariableTypeEnum.GERMPLASM_PASSPORT]
            && variableType.toString() !== VariableTypeEnum[VariableTypeEnum.GERMPLASM_ATTRIBUTE]);
    }

    private isNotEditableColumn(variable: GermplasmListObservationVariable): boolean {
        return variable && variable.termId === 8230 || !this.isEntryDetailColumn(variable.variableType);
    }

    private mapSelectedColumnsToUpdateViewRequest(selectedColumns: GermplasmListColumnModel[]): GermplasmListDataUpdateViewRequest[] {
        return selectedColumns.map((column: GermplasmListColumnModel) =>
            new GermplasmListDataUpdateViewRequest(column.id, column.category, column.typeId));
    }

    private mapFiltersToRequest() {
        const request = {};
        for (const filterKey of Object.keys(this.filters)) {
            const filter = this.filters[filterKey];
            if (filter.value || (filter.type === FilterType.DATE && (filter.from || filter.to))) {
                if (filter.type === FilterType.LIST) {
                    const filterValue = filter.value.split(',');
                    this.addFilterToRequest(request, filter, filter.key, filterValue);
                } else if (filter.type === FilterType.TEXT_WITH_MATCH_OPTIONS) {
                    const filterValue = {
                        type: filter.matchType,
                        value: filter.value
                    };
                    this.addFilterToRequest(request, filter, filter.key, filterValue);
                } else if (filter.type === FilterType.DATE) {
                    if (filter.from) {
                        const filterValue = `${filter.from.year}-${filter.from.month}-${filter.from.day}`;
                        this.addFilterToRequest(request, filter, filter.fromKey, filterValue);
                    }
                    if (filter.to) {
                        const filterValue = `${filter.to.year}-${filter.to.month}-${filter.to.day}`;
                        this.addFilterToRequest(request, filter, filter.toKey, filterValue);
                    }
                } else {
                    this.addFilterToRequest(request, filter, filter.key, filter.value);
                }
            }
        }
        return request;
    }

    private addFilterToRequest(request: any, filter: any, key: string, value: any) {
        if (this.isStaticColumn(filter.category)) {
            request[key] = value;
            return;
        }

        if (this.isNamesColumn(filter.category)) {
            if (!request.namesFilters) {
                request.namesFilters = {};
            }
            request.namesFilters[filter.termId] = value;
            return;
        }

        if (this.isDescriptorColumn(filter.variableType)) {
            if (!request.descriptorsFilters) {
                request.descriptorsFilters = {};
            }
            request.descriptorsFilters[filter.termId] = value;
            return;
        }

        if (this.isEntryDetailColumn(filter.variableType)) {
            if (!request.variablesFilters) {
                request.variablesFilters = {};
            }
            request.variablesFilters[filter.termId] = value;
            return;
        }
    }

    addVariable(variable: VariableDetails) {
        const variableAdded = this.variables.filter((x) =>
            Number(x.id) === Number(variable.id));

        if (variableAdded.length > 0) {
            this.alertService.warning('germplasm-list.variables.already.exists');
            return;
        }

        this.germplasmListService.addVariable(this.listId, variable.id, VariableTypeEnum.ENTRY_DETAILS).subscribe(() => {
            const metadataDetails = new MetadataDetails();
            variable.metadata = metadataDetails;
            variable.metadata.deletable = TermIdEnum.ENTRY_NO !== Number(variable.id);
            this.variables.push(variable);
            this.variables = this.sortByUndeletable(this.variables);
            this.refreshTable();
        }, (error) => this.onError(error));
    }

    async deleteVariables(variableIds: any[]) {
        const countObservationsByVariablesResp = await this.germplasmListService.countObservationsByVariables(this.listId, variableIds).toPromise();
        const variablesCount = Number(countObservationsByVariablesResp.headers.get('X-Total-Count'));

        if (variablesCount > 0) {
            const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
            confirmModalRef.componentInstance.title = this.translateService.instant('germplasm-list.variables.confirm.delete.title');
            confirmModalRef.componentInstance.message = this.translateService.instant('germplasm-list.variables.confirm.delete.warning');

            try {
                await confirmModalRef.result
            } catch (e) {
                return
            }
        }

        this.germplasmListService.deleteVariables(this.listId, variableIds).subscribe(() => {
            const variableDeleted = this.variables.filter((variable) =>
                variableIds.indexOf(variable.id.toString()) !== -1
            );

            variableDeleted.forEach((variable) => {
                this.variables.splice(this.variables.indexOf(variable), 1);
                delete this.selectedVariables[variable.id];
            });

            this.variables = this.sortByUndeletable(this.variables);
            this.refreshTable();
        }, (error) => this.onError(error));
    }

    removeEntries() {
        if (!this.validateSelection()) {
            return;
        }
        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.message = this.translateService.instant('germplasm-list.list-data.remove-entries.confirm.message');
        confirmModalRef.componentInstance.title = this.translateService.instant('germplasm-list.list-data.remove-entries.confirm.header');

        confirmModalRef.result.then(() => {
            this.germplasmListService.removeEntries(this.listId, this.getSelectedItemIds()).subscribe(() => {
                if (this.isPageSelected() && this.page === Math.ceil(this.totalItems / this.itemsPerPage)) {
                    this.page = 1;
                }
                this.clearSelectedItems();
                this.refreshTable();
                this.alertService.success('germplasm-list.list-data.remove-entries.remove.success');
            }, (error) => this.onError(error));
        }, () => confirmModalRef.dismiss());
    }

    private validateSelection() {
        if (this.entries.length === 0 || (!this.isSelectAll && this.size() === 0)) {
            this.alertService.error('germplasm-list.list-data.selection.empty');
            return false;
        }
        return true;
    }

    private getSelectedItemIds() {
        return Array.from(this.selectedItems.keys()).map((listDataId: any) => Number(listDataId));
    }

    fillWithCrossExpansion() {
        this.isLoading = true;
        this.germplasmListService.fillWithCrossExpansion(this.listId, this.generationLevel).pipe(
            finalize(() => this.isLoading = false)
        ).subscribe(
            () => this.refreshTable(),
            (error) => this.onError(error)
        );
    }

    isActionMenuAvailable() {
        return this.principal.hasAnyAuthorityDirect(this.ACTION_BUTTON_PERMISSIONS) ||
            this.isDeleteActionItemAvailable() ||
            (!this.germplasmList.locked && this.principal.hasAnyAuthorityDirect(this.ACTION_ITEM_PERMISSIONS_WITH_LOCK_RESTRICTION));
    }

    isDeleteActionItemAvailable() {
        return this.germplasmList
            && !this.germplasmList.locked
            && (this.principal.hasAnyAuthorityDirect(this.DELETE_LIST_PERMISSIONS) || this.user.id === this.germplasmList.ownerId);
    }
}

export enum ColumnAlias {
    'GID' = 'GID',
    'DESIGNATION' = 'DESIGNATION',
    'LOTS' = 'LOTS',
    'AVAILABLE' = 'AVAILABLE',
    'UNIT' = 'UNIT',
    'GROUP_ID' = 'GROUP_ID',
    'CROSS' = 'CROSS',
    'MALE_PARENT_GID' = 'MALE_PARENT_GID',
    'FEMALE_PARENT_GID' = 'FEMALE_PARENT_GID',
    'LOCATION_NAME' = 'LOCATION_NAME',
    'BREEDING_METHOD_PREFERRED_NAME' = 'BREEDING_METHOD_PREFERRED_NAME',
    'MALE_PARENT_NAME' = 'MALE_PARENT_NAME',
    'FEMALE_PARENT_NAME' = 'FEMALE_PARENT_NAME',
    'IMMEDIATE_SOURCE_NAME' = 'IMMEDIATE_SOURCE_NAME',
    'GROUP_SOURCE_NAME' = 'GROUP_SOURCE_NAME'
}
