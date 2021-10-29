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
import { GermplasmList } from '../shared/germplasm-list/model/germplasm-list.model';
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
import { MANAGE_GERMPLASM_LIST_PERMISSIONS } from '../shared/auth/permissions';
import { SearchResult } from '../shared/search-result.model';

declare var $: any;

@Component({
    selector: 'jhi-list',
    templateUrl: './list.component.html'
})
export class ListComponent implements OnInit {

    static readonly GERMPLASM_LIST_CHANGED = 'GermplasmListViewChanged';

    IMPORT_GERMPLASM_LIST_UPDATES_PERMISSION = [...MANAGE_GERMPLASM_LIST_PERMISSIONS, 'IMPORT_GERMPLASM_LIST_UPDATES'];
    ACTION_BUTTON_PERMISSIONS = [...MANAGE_GERMPLASM_LIST_PERMISSIONS, 'IMPORT_GERMPLASM_LIST_UPDATES'];

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

    germplasmList: GermplasmList;
    header: GermplasmListObservationVariable[];
    entries: GermplasmListDataSearchResponse[];
    eventSubscriber: Subscription;

    currentSearch: string;
    totalItems: number;
    page: number;
    previousPage: number;
    predicate: any;
    reverse: any;
    resultSearch: SearchResult;

    isLoading: boolean;

    germplasmListFilters: any;

    constructor(private activatedRoute: ActivatedRoute,
                private jhiLanguageService: JhiLanguageService,
                private eventManager: JhiEventManager,
                private germplasmListService: GermplasmListService,
                private router: Router,
                private alertService: AlertService,
                private principal: Principal,
                private modalService: NgbModal,
                public translateService: TranslateService
    ) {
        this.page = 1;
        this.totalItems = 0;
        this.currentSearch = '';
        this.resultSearch = new SearchResult('');
        this.setDefaultSort();
    }

    async ngOnInit() {
        this.variables = [];
        const identity = await this.principal.identity();
        this.user = identity;

        this.germplasmListService.getGermplasmListById(this.listId).subscribe(
            (res: HttpResponse<GermplasmList>) => this.germplasmList = res.body,
            (res: HttpErrorResponse) => this.onError(res)
        );

        this.load();
        this.registerGermplasmListChanged();
    }

    async load() {
        await this.loadEntryDetails();
        this.refreshTable();
    }

    private loadEntryDetails() {
        return this.germplasmListService.getVariables(this.listId, VariableTypeEnum.ENTRY_DETAILS).toPromise().then(
            (res: HttpResponse<VariableDetails[]>) => this.variables = res.body,
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    private refreshTable() {
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

    registerGermplasmListChanged() {
        this.eventSubscriber = this.eventManager.subscribe(this.listId + ListComponent.GERMPLASM_LIST_CHANGED, (event) => {
            this.load()
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
        return [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
    }

    private clearSort() {
        this.predicate = SORT_PREDICATE_NONE;
        this.reverse = '';
        $('.fa-sort').removeClass('fa-sort-up fa-sort-down');
    }

    private setDefaultSort() {
        this.predicate = ColumnAlias.ENTRY_NO;
        this.reverse = 'asc';
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
            this.alertService.warning('germplasm-list.variables.already.exsits');
            return;
        }

        this.germplasmListService.addVariable(this.listId, variable.id, VariableTypeEnum.ENTRY_DETAILS).subscribe(() => {
            this.variables.push(variable);
            this.refreshTable();
        });
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

             this.refreshTable();
         });
    }

}

export enum ColumnAlias {
    'ENTRY_NO' = 'ENTRY_NO',
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
    'FEMALE_PARENT_NAME' = 'FEMALE_PARENT_NAME'
}
