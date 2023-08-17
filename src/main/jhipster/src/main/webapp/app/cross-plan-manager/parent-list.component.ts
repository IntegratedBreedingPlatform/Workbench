import {Component, Input, OnInit, ViewChild, ViewContainerRef} from '@angular/core';
import {ListEntry} from '../shared/list-builder/model/list.model';
import {Germplasm} from '../entities/germplasm/germplasm.model';
import {Subscription} from 'rxjs';
import {Overlay} from '@angular/cdk/overlay';
import {GermplasmSearchRequest} from '../entities/germplasm/germplasm-search-request.model';
import {SearchResult} from '../shared/search-result.model';
import {NgbPopover} from '@ng-bootstrap/ng-bootstrap';
import {finalize} from 'rxjs/internal/operators/finalize';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {formatErrorList} from '../shared/alert/format-error-list';
import {Router} from '@angular/router';
import {JhiEventManager, JhiLanguageService} from 'ng-jhipster';
import {GermplasmService} from '../shared/germplasm/service/germplasm.service';
import {AlertService} from '../shared/alert/alert.service';
import {ParamContext} from '../shared/service/param.context';
import {GermplasmDetailsUrlService} from '../shared/germplasm/service/germplasm-details.url.service';
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
    @Input() listData: ListEntry[];

    MAX_NAME_DISPLAY_SIZE = 30;
    page = 1;
    pageSize = 20;
    itemsPerPage = 500;
    ColumnLabels = ColumnLabels;

    selectedItems = {};
    isSelectAllPages = false;
    lastClickIndex: any;

    @ViewChild('colVisPopOver') public colVisPopOver: NgbPopover;
    eventSubscriber: Subscription;

    germplasmSearchRequest = new GermplasmSearchRequest();
    germplasmHiddenColumns = {};
    resultSearch: SearchResult;
    isLoading: boolean;

    selectEntriesFlag = false;
    gidSelected = [];
    isListCollapsed: boolean;

    defaultColumns: any[] = [];
    nameColumns: any[] = [];
    passportColumns: any[] = [];
    attributesColumns: any[] = [];

    constructor(private eventManager: JhiEventManager,
                private jhiLanguageService: JhiLanguageService,
                private germplasmService: GermplasmService,
                private router: Router,
                private alertService: AlertService,
                private paramContext: ParamContext,
                public germplasmDetailsUrlService: GermplasmDetailsUrlService,
                public overlay: Overlay,
                public viewContainerRef: ViewContainerRef) {

        this.page = 1;
        this.paramContext.readParams();
        this.isListCollapsed = false;
        this.resultSearch = new SearchResult('');

    }

    ngOnInit(): void {
        this.listData = [];
        this.gidSelected = [];
        this.registerGermplasmSelectorSelected();
        this.request.addedColumnsPropertyIds = [];

        this.defaultColumns = [
            new ParentListColumnModel('GID', ParentListColumnCategory.DEFAULT, true),
            new ParentListColumnModel('NAMES', ParentListColumnCategory.DEFAULT, true),
            new ParentListColumnModel('AVAILABLE', ParentListColumnCategory.DEFAULT, true),
            new ParentListColumnModel('LOT UNITS', ParentListColumnCategory.DEFAULT, true),
            new ParentListColumnModel('LOTS', ParentListColumnCategory.DEFAULT, true),
            new ParentListColumnModel('CROSS', ParentListColumnCategory.DEFAULT, true),
            new ParentListColumnModel('LOCATION', ParentListColumnCategory.DEFAULT, true),
            new ParentListColumnModel('METHOD NAME', ParentListColumnCategory.DEFAULT, true),

            new ParentListColumnModel('GERMPLASM UUID', ParentListColumnCategory.DEFAULT, false),
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

    onSelectAllPages() {
        this.isSelectAllPages = !this.isSelectAllPages;
        this.selectedItems = {};
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

    private onSuccess(data) {
        const listEntries = this.buildListEntry(data);
        if (this.listData.length) {
            this.listData = this.listData.concat(listEntries);
        } else {
            this.listData = listEntries;
        }

        const gids = data.map((germplasm) => germplasm.gid);
        this.germplasmService.getGermplasmAttributesByGidsAndType(gids, VariableTypeEnum.GERMPLASM_PASSPORT).toPromise().then((passports) => {
            this.setAttributesToListEntry(passports, ParentListColumnCategory.PASSPORT);
        });

        this.germplasmService.getGermplasmAttributesByGidsAndType(gids, VariableTypeEnum.GERMPLASM_ATTRIBUTE).toPromise().then((attributes) => {
            this.setAttributesToListEntry(attributes, ParentListColumnCategory.ATTRIBUTES);
        });

        this.germplasmService.getGermplasmNamesByGids(gids).toPromise().then((germplasmNames) => {
            this.setNamesToListEntry(germplasmNames);
        });
    }

    private buildListEntry(data): ListEntry[] {
        return data.map((germplasm: Germplasm) => {
            const row: ListEntry = new ListEntry();
            row[ColumnLabels.GID] = germplasm.gid;
            row[ColumnLabels.GERMPLASM_UUID] = germplasm.germplasmUUID;
            row[ColumnLabels.GROUP_ID] = germplasm.groupId;
            row[ColumnLabels.NAMES] = germplasm.names;
            row[ColumnLabels.AVAILABLE] = germplasm.availableBalance;
            row['UNIT'] = germplasm.unit;
            row['LOTS'] = germplasm.lotCount;
            row[ColumnLabels.CROSS] = germplasm.pedigreeString;
            row['PREFERRED ID'] = germplasm.germplasmPreferredId
            row['PREFERRED NAME'] = germplasm.germplasmPreferredName
            row['GERMPLASM DATE'] = germplasm.germplasmDate
            row['LOCATION'] = germplasm.locationName;
            row['LOCATION ID'] = germplasm.locationId;
            row['METHOD ID'] = germplasm.breedingMethodId;
            row['METHOD NAME'] = germplasm.methodName;
            row['METHOD ABBREV'] = germplasm.methodCode;
            row['METHOD NUMBER'] = germplasm.methodNumber;
            row['METHOD GROUP'] = germplasm.methodGroup;
            row['FGID'] = germplasm.femaleParentGID;
            row['CROSS-FEMALE PREFERRED NAME'] = germplasm.femaleParentPreferredName;
            row['MGID'] = germplasm.maleParentGID;
            row['CROSS-MALE PREFERRED NAME'] = germplasm.maleParentPreferredName;
            row['GROUP SOURCE GID'] = germplasm.groupSourceGID;
            row['GROUP SOURCE'] = germplasm.groupSourcePreferredName;
            row['IMMEDIATE SOURCE GID'] = germplasm.immediateSourceGID;
            row['IMMEDIATE SOURCE'] = germplasm.immediateSourceName;
            return row;
        });
    }

    private setAttributesToListEntry(attributes: GermplasmAttribute[], columnCategory: ParentListColumnCategory) {
        const attributeMap = new Map();
        attributes.forEach((attribute) => {
            if (!attributeMap.get(attribute.gid)) {
                attributeMap.set(attribute.gid, []);
            }
            attributeMap.get(attribute.gid).push(attribute);
        });

        this.listData.forEach((entry) => {
            const attributeLists = attributeMap.get(entry[ColumnLabels.GID]);

            if (attributeLists && attributeLists.length) {
                attributeLists.forEach((attribute) => {
                    entry[attribute.variableName] = attribute.value;
                    if (this.hiddenColumns[attribute.variableName] === undefined) {
                        this.hiddenColumns[attribute.variableName] = true;
                        if (ParentListColumnCategory.ATTRIBUTES === columnCategory) {
                            this.attributesColumns.push(new ParentListColumnModel(attribute.variableName, columnCategory, false))

                        } else if (ParentListColumnCategory.PASSPORT === columnCategory) {
                            this.passportColumns.push(new ParentListColumnModel(attribute.variableName, columnCategory, false))
                        }
                    }
                });
            }
        });
    }

    private setNamesToListEntry(germplasmNames: any[]) {
        const germplasmNameMap = new Map();
        germplasmNames.forEach((name) => {
            if (!germplasmNameMap.get(name.gid)) {
                germplasmNameMap.set(name.gid, []);
            }
            germplasmNameMap.get(name.gid).push(name);
        });

        this.listData.forEach((entry) => {
            const names = germplasmNameMap.get(entry[ColumnLabels.GID]);
            if (names && names.length) {
                names.forEach((name) => {
                    entry[name.nameTypeCode] = name.name;
                    if (this.hiddenColumns[name.nameTypeCode] === undefined) {
                        this.hiddenColumns[name.nameTypeCode] = true;
                        this.nameColumns.push(new ParentListColumnModel(name.nameTypeCode, ParentListColumnCategory.NAMES, false))

                    }
                });
            }
        });
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

                if (this.gidSelected.length) {
                    this.gidSelected = this.request.gids.concat(event.content.split(','));
                    this.request.gids = event.content.split(',');
                } else {
                    this.gidSelected = event.content.split(',');
                    this.request.gids = this.gidSelected;
                }

                const pages = this.request.gids.length < this.itemsPerPage ? 1 :
                    this.request.gids.length / this.itemsPerPage;
                this.resultSearch.searchResultDbId = '';

                for (let page = 1; page <= pages; page++) {
                    this.isLoading = true;
                    this.search(this.request).then((searchId) => {
                        this.germplasmService.getSearchResults(
                            this.addSortParam({
                                searchRequestId: searchId,
                                page: page - 1,
                                size: this.itemsPerPage
                            })
                        ).pipe(finalize(() => {
                            this.isLoading = false;
                        })).subscribe(
                            (res: HttpResponse<Germplasm[]>) => this.onSuccess(res.body),
                            (res: HttpErrorResponse) => this.onError(res)
                        );
                    }, (error) => this.onError(error));
                }
            }
        });
    }

    onColumnsSelected(columns: any[]) {
        columns.forEach((column) => {
            this.hiddenColumns[column.name] = !column.selected;
        });
    }

    resetTable() {
        this.page = 1;
        this.selectedItems = {};
        this.listData = [];
        this.nameColumns = [];
        this.passportColumns = [];
        this.attributesColumns = [];
        this.gidSelected = [];
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
        const pageItemIds = this.getPageItemIds();
        if (this.isPageSelected()) {
            // remove all items
            pageItemIds.forEach((itemId) => delete this.selectedItems[itemId]);
        } else {
            // check remaining items
            pageItemIds.forEach((itemId) => this.selectedItems[itemId] = true);
        }
    }

    toggleSelect($event, index, internal_id, checkbox = false) {
        if (!$event.ctrlKey && !checkbox) {
            this.selectedItems = {};
        }
        let ids;
        if ($event.shiftKey) {
            const max = Math.max(this.lastClickIndex, index) + 1 + this.pageOffset(),
                min = Math.min(this.lastClickIndex, index) + this.pageOffset();
            ids = this.listData.slice(min, max).map((g) => g.internal_id);
        } else {
            ids = [internal_id];
            this.lastClickIndex = index;
        }
        const isClickedItemSelected = this.selectedItems[internal_id];
        for (const id of ids) {
            if (isClickedItemSelected) {
                delete this.selectedItems[id];
            } else {
                this.selectedItems[id] = true;
            }
        }
    }

    isPageSelected() {
        const pageItemIds = this.getPageItemIds();
        return this.size(this.selectedItems) && pageItemIds.every((itemId) => this.selectedItems[itemId]);
    }

    getPageItemIds(): any[] {
        if (!(this.listData && this.listData.length)) {
            return [];
        }
        return this.listData.slice(this.pageOffset(), this.page * this.pageSize)
            .map((row) => row.internal_id);
    }

    size(obj) {
        return Object.keys(obj).length;
    }

    isSelected(index) {
        return this.selectedItems[index];
    }

    pageOffset() {
        return (this.page - 1) * this.pageSize;
    }

    selectAll() {
        this.onSelectPage();
    }

    removeSelected() {
        if (this.isSelectAllPages) {
            this.resetTable();
        } else {
            this.listData = this.listData.filter((row) => !this.selectedItems[row.internal_id]);
        }
        this.selectedItems = {};
    }

    cleanList() {
        this.listData = [];
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
