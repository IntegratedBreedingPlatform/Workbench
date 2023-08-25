import {Component, Input, OnInit, ViewChild, ViewContainerRef} from '@angular/core';
import {Germplasm} from '../entities/germplasm/germplasm.model';
import {Subscription} from 'rxjs';
import {Overlay} from '@angular/cdk/overlay';
import {GermplasmSearchRequest} from '../entities/germplasm/germplasm-search-request.model';
import {SearchResult} from '../shared/search-result.model';
import {NgbPopover} from '@ng-bootstrap/ng-bootstrap';
import {finalize} from 'rxjs/internal/operators/finalize';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {formatErrorList} from '../shared/alert/format-error-list';
import {ActivatedRoute, Router} from '@angular/router';
import {JhiEventManager, JhiLanguageService} from 'ng-jhipster';
import {GermplasmService} from '../shared/germplasm/service/germplasm.service';
import {AlertService} from '../shared/alert/alert.service';
import {ParamContext} from '../shared/service/param.context';
import {ParentListColumnCategory, ParentListColumnModel} from './parent-list-columns.component';
import {VariableTypeEnum} from '../shared/ontology/variable-type.enum';
import {GermplasmAttribute} from '../shared/germplasm/model/germplasm.model';

@Component({
    selector: 'jhi-parent-list',
    templateUrl: './parent-list.component.html',
    styleUrls: ['./cross-plan-design.component.scss'],
})
export class ParentListComponent implements OnInit {

    @Input() title: string;
    @Input() tableId: string;
    @Input() listGids: Array<number>;

    MAX_NAME_DISPLAY_SIZE = 30;
    page = 1;
    currentSearch: string;
    filteredItems: any;
    itemsPerPage = 10;
    previousPage: any;

    ColumnLabels = ColumnLabels;

    selectedItems: any[] = [];
    isSelectAllPages = false;
    lastClickIndex: any;
    germplasmList: Germplasm[] = [];

    @ViewChild('colVisPopOver') public colVisPopOver: NgbPopover;
    eventSubscriber: Subscription;

    germplasmSearchRequest = new GermplasmSearchRequest();
    germplasmHiddenColumns = {};
    resultSearch: SearchResult;
    isLoading: boolean;

    selectEntriesFlag = false;
    isListCollapsed: boolean;

    defaultColumns: any[] = [];
    nameColumns: any[] = [];
    passportColumns: any[] = [];
    attributesColumns: any[] = [];

    constructor(private activatedRoute: ActivatedRoute,
                private eventManager: JhiEventManager,
                private jhiLanguageService: JhiLanguageService,
                private germplasmService: GermplasmService,
                private router: Router,
                private alertService: AlertService,
                private paramContext: ParamContext,
                public overlay: Overlay,
                public viewContainerRef: ViewContainerRef) {

        this.page = 1;
        this.paramContext.readParams();
        this.isListCollapsed = false;

        this.currentSearch = this.activatedRoute.snapshot && this.activatedRoute.snapshot.params['search'] ?
            this.activatedRoute.snapshot.params['search'] : '';

        this.resultSearch = new SearchResult('');

    }

    ngOnInit(): void {
        this.registerGermplasmSelectorSelected();
        this.request.addedColumnsPropertyIds = [];

        this.defaultColumns = [
            new ParentListColumnModel(ColumnLabels.GID, ParentListColumnCategory.DEFAULT, true),
            new ParentListColumnModel(ColumnLabels.NAMES, ParentListColumnCategory.DEFAULT, true),
            new ParentListColumnModel(ColumnLabels.AVAILABLE, ParentListColumnCategory.DEFAULT, true),
            new ParentListColumnModel(ColumnLabels.LOT_UNITS, ParentListColumnCategory.DEFAULT, true),
            new ParentListColumnModel(ColumnLabels.LOTS, ParentListColumnCategory.DEFAULT, true),
            new ParentListColumnModel(ColumnLabels.CROSS, ParentListColumnCategory.DEFAULT, true),
            new ParentListColumnModel(ColumnLabels.LOCATION, ParentListColumnCategory.DEFAULT, true),
            new ParentListColumnModel(ColumnLabels.METHOD_NAME, ParentListColumnCategory.DEFAULT, true),

            new ParentListColumnModel(ColumnLabels.GERMPLASM_UUID, ParentListColumnCategory.DEFAULT, false),
            new ParentListColumnModel('GROUP ID', ParentListColumnCategory.DEFAULT, false),
            new ParentListColumnModel('GERMPLASM DATE', ParentListColumnCategory.DEFAULT, false),
            new ParentListColumnModel('METHOD ABBREV', ParentListColumnCategory.DEFAULT, false),
            new ParentListColumnModel('METHOD NUMBER', ParentListColumnCategory.DEFAULT, false),
            new ParentListColumnModel('METHOD GROUP', ParentListColumnCategory.DEFAULT, false),
            new ParentListColumnModel('PREFERRED NAME', ParentListColumnCategory.DEFAULT, false),
            new ParentListColumnModel('PREFERRED ID', ParentListColumnCategory.DEFAULT, false),
            new ParentListColumnModel('GROUP SOURCE GID', ParentListColumnCategory.DEFAULT, false),
            new ParentListColumnModel('GROUP SOURCE', ParentListColumnCategory.DEFAULT, false),
            new ParentListColumnModel('IMMEDIATE SOURCE GID', ParentListColumnCategory.DEFAULT, false),
            new ParentListColumnModel('IMMEDIATE SOURCE', ParentListColumnCategory.DEFAULT, false),
            new ParentListColumnModel('FGID', ParentListColumnCategory.DEFAULT, false),
            new ParentListColumnModel('CROSS-FEMALE PREFERRED NAME', ParentListColumnCategory.DEFAULT, false),
            new ParentListColumnModel('MGID', ParentListColumnCategory.DEFAULT, false),
            new ParentListColumnModel('CROSS-MALE PREFERRED NAME', ParentListColumnCategory.DEFAULT, false)

        ];

        this.defaultColumns.forEach((column) => {
            this.hiddenColumns[column.name] = !column.selected;
        });

    }

    addSortParam(params) {
        const sort = {};
        return Object.assign(params, sort);
    }

    onSelectAllPages(isSelectAllPages) {
        this.isSelectAllPages = !isSelectAllPages;
        this.selectedItems = [];
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

    private onSuccess(data, headers) {
        this.filteredItems = headers.get('X-Filtered-Count');
        this.germplasmList = data;
    }

    registerGermplasmSelectorSelected() {
        this.eventSubscriber = this.eventManager.subscribe('germplasmSelectorSelected', (event) => {
            if (this.selectEntriesFlag) {
                this.selectEntriesFlag = false;
                this.request.addedColumnsPropertyIds = [
                    'PREFERRED ID', 'PREFERRED NAME', 'GERMPLASM DATE',
                    'METHOD ABBREV', 'METHOD NUMBER', 'METHOD GROUP',
                    'FGID', 'CROSS-FEMALE PREFERRED NAME',
                    'MGID', 'CROSS-MALE PREFERRED NAME',
                    'GROUP SOURCE GID', 'GROUP SOURCE',
                    'IMMEDIATE SOURCE GID', 'IMMEDIATE SOURCE'
                ];

                if (this.listGids.length) {
                    const gids = this.request.gids.concat(event.content.split(',')).map((item) => {
                        return Number(item)
                    });
                    const filterGids = gids.filter((item, idx) => gids.indexOf(item) === idx);
                    this.listGids.length = 0;
                    filterGids.forEach((item) => {
                        this.listGids.push(item);
                    });
                    this.request.gids = this.listGids;
                } else {
                    event.content.split(',').forEach((item) => {
                        this.listGids.push(Number(item));
                    });
                    this.request.gids = this.listGids;
                }

                this.retrieveAttributes(this.request.gids, VariableTypeEnum.GERMPLASM_PASSPORT).then((passports) => {
                    this.addAttributesToSearch(passports, ParentListColumnCategory.PASSPORT);
                });

                this.retrieveAttributes(this.request.gids, VariableTypeEnum.GERMPLASM_ATTRIBUTE).then((attributes) => {
                    this.addAttributesToSearch(attributes, ParentListColumnCategory.ATTRIBUTES);
                });

                this.retrieveNames(this.request.gids).then((germplasmNames) => {
                    this.addNamesToSearch(germplasmNames);
                    this.resultSearch.searchResultDbId = '';
                    this.loadAll(this.request);
                });
            }
        });
    }

    retrieveAttributes(gidList: number[], variableTypeId: VariableTypeEnum): Promise<any> {
        return new Promise((resolve, reject) => {
            this.germplasmService.getGermplasmAttributesByGidsAndType(gidList, variableTypeId).subscribe((response) => {
                resolve(response);
            }, (error) => reject(error));
        });
    }

    retrieveNames(gidList: number[]): Promise<any> {
        return new Promise((resolve, reject) => {
            this.germplasmService.getGermplasmNamesByGids(gidList).subscribe((response) => {
                resolve(response);
            }, (error) => reject(error));
        });
    }

    addAttributesToSearch(attributes: GermplasmAttribute[], columnCategory: ParentListColumnCategory) {
        attributes.forEach((attribute) => {
            if (this.request.addedColumnsPropertyIds.indexOf(attribute.variableName) === -1) {
                this.request.addedColumnsPropertyIds.push(attribute.variableName);
                if (this.hiddenColumns[attribute.variableName] === undefined) {
                    this.hiddenColumns[attribute.variableName] = true;
                    if (ParentListColumnCategory.ATTRIBUTES === columnCategory) {
                        this.attributesColumns.push(new ParentListColumnModel(attribute.variableName.toUpperCase(), columnCategory, false))

                    } else if (ParentListColumnCategory.PASSPORT === columnCategory) {
                        this.passportColumns.push(new ParentListColumnModel(attribute.variableName.toUpperCase(), columnCategory, false))
                    }
                }
            }
        });
    }

    addNamesToSearch(germplasmNames: any[]) {
        germplasmNames.forEach((name) => {
            if (this.request.addedColumnsPropertyIds.indexOf(name.nameTypeDescription) === -1) {
                this.request.addedColumnsPropertyIds.push(name.nameTypeDescription);
                if (this.hiddenColumns[name.nameTypeDescription] === undefined) {
                    this.hiddenColumns[name.nameTypeDescription] = true;
                    this.nameColumns.push(new ParentListColumnModel(name.nameTypeDescription.toUpperCase(), ParentListColumnCategory.NAMES, false))

                }
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

    onColumnsSelected(columns: any[]) {
        columns.forEach((column) => {
            this.hiddenColumns[column.name] = !column.selected;
        });
    }

    resetTable() {
        this.page = 1;
        this.selectedItems = [];
        this.nameColumns = [];
        this.passportColumns = [];
        this.attributesColumns = [];
        this.germplasmSearchRequest = new GermplasmSearchRequest();
        this.germplasmList = [];
        this.listGids.length = 0;
        this.isSelectAllPages = false;
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', {param: msg});
        } else {
            this.alertService.error('error.general');
        }
    }

    get request() {
        return this.germplasmSearchRequest;
    }

    set request(request) {
        this.germplasmSearchRequest = request;
    }

    get hiddenColumns() {
        return this.germplasmHiddenColumns;
    }

    set hiddenColumns(hiddenColumns) {
        this.germplasmHiddenColumns = hiddenColumns;
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

    isPageSelected() {
        return this.germplasmList.length > 0 && !this.germplasmList.some((germplasm) => this.selectedItems.indexOf(germplasm.gid) === -1);
    }

    toggleSelect(germplasm: Germplasm) {
        if (this.isSelectAllPages) {
            return;
        }
        if (this.selectedItems.length > 0 && this.selectedItems.find((item) => item === germplasm.gid)) {
            this.selectedItems = this.selectedItems.filter((item) => item !== germplasm.gid);
        } else {
            this.selectedItems.push(germplasm.gid);
        }
    }

    isSelected(germplasm: Germplasm) {
        return germplasm && this.selectedItems.length > 0 && this.selectedItems.find((item) => item === germplasm.gid);
    }

    size(obj) {
        return Object.keys(obj).length;
    }

    trackId(index: number, item: Germplasm) {
        return item.gid;
    }

    pageOffset() {
        return (this.page - 1) * this.itemsPerPage;
    }

    selectAll() {
        this.onSelectPage();
    }

    removeSelected() {
        if (this.isSelectAllPages) {
            this.resetTable();
        } else {
            const gids = this.listGids.filter((item) => this.selectedItems.indexOf(item) === -1);
            this.listGids.length = 0;

            gids.forEach((item) => {
                this.listGids.push(item);
            });

            this.selectedItems = [];
            this.request.gids = this.listGids;
            this.germplasmList = [];
            this.resultSearch.searchResultDbId = '';
            this.loadAll(this.request);
        }

    }

    cleanList() {
        this.resetTable();
    }

    SelectEntries() {
        this.selectEntriesFlag = true;
        this.router.navigate(['/', {outlets: {popup: 'germplasm-selector-dialog'}}], {
            queryParamsHandling: 'merge',
            queryParams: {
                cropName: this.paramContext.cropName,
                loggedInUserId: this.paramContext.loggedInUserId,
                programUUID: this.paramContext.programUUID,
                selectMultiple: true
            }
        });
    }

}

export enum ColumnLabels {
    'GID' = 'GID',
    'GERMPLASM_UUID' = 'GERMPLASM UUID',
    'GROUP_ID' = 'GROUP ID',
    'NAMES' = 'NAMES',
    'AVAILABLE' = 'AVAILABLE',
    'LOT_UNITS' = 'UNITS',
    'LOTS' = 'LOTS',
    'CROSS' = 'CROSS',
    'PREFERRED ID' = 'PREFERRED ID',
    'PREFERRED NAME' = 'PREFERRED NAME',
    'GERMPLASM DATE' = 'GERMPLASM DATE',
    'LOCATION' = 'LOCATION',
    'METHOD_NAME' = 'METHOD NAME',
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
