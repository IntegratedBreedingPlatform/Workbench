import { Component, OnInit } from '@angular/core';
import { BreedingMethod } from '../../shared/breeding-method/model/breeding-method';
import { BreedingMethodSearchRequest } from '../../shared/breeding-method/model/breeding-method-search-request.model';
import { SearchResult } from '../../shared/search-result.model';
import { ActivatedRoute, Router } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster/src/language';
import { JhiEventManager } from 'ng-jhipster';
import { AlertService } from '../../shared/alert/alert.service';
import { Subscription } from 'rxjs';
import { BreedingMethodService } from '../../shared/breeding-method/service/breeding-method.service';
import { ColumnFilterComponent, FilterType } from '../../shared/column-filter/column-filter.component';
import { LocationSearchRequest } from '../../shared/location/model/location-search-request.model';
import { finalize } from 'rxjs/operators';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { MatchType } from '../../shared/column-filter/column-filter-text-with-match-options-component';
import { Select2OptionData } from 'ng-select2';
import { BreedingMethodType } from '../../shared/breeding-method/model/breeding-method-type.model';
import { BreedingMethodGroup } from '../../shared/breeding-method/model/breeding-method-group.model';
import { BreedingMethodClass } from '../../shared/breeding-method/model/breeding-method-class.model';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { ColumnFilterTransitionEventModel } from '../../shared/column-filter/column-filter-transition-event.model';
import { Location } from '../../shared/location/model/location';
import { Pageable } from '../../shared/model/pageable';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CropSettingsContext } from '../crop-Settings.context';
import { TranslateService } from '@ngx-translate/core';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { NameType } from '../../shared/germplasm/model/name-type.model';

declare var $: any;

@Component({
    selector: 'jhi-breeding-methods-pane',
    templateUrl: 'breeding-methods-pane.component.html',
    styleUrls: [
        './breeding-methods-pane.component.scss'
    ]
})
export class BreedingMethodsPaneComponent implements OnInit {

    static readonly COLUMN_FILTER_EVENT_NAME = 'searchColumnFiltersChanged';

    COLUMN_FILTER_EVENT_NAME = BreedingMethodsPaneComponent.COLUMN_FILTER_EVENT_NAME;

    ColumnLabels = ColumnLabels;

    eventSubscriber: Subscription;
    breedingMethods: BreedingMethod[];

    currentSearch: string;

    itemsPerPage = 20;
    page: number;
    previousPage: number;
    predicate: any;
    reverse: any;
    resultSearch: SearchResult;

    isLoading: boolean;

    searchRequest: BreedingMethodSearchRequest;

    totalItems: number;
    breedingMethodFilters: any;

    constructor(public translateService: TranslateService,
                private activatedRoute: ActivatedRoute,
                private jhiLanguageService: JhiLanguageService,
                private eventManager: JhiEventManager,
                private breedingMethodService: BreedingMethodService,
                private germplasmService: GermplasmService,
                private router: Router,
                private alertService: AlertService,
                private modalService: NgbModal,
                private cropSettingsContext: CropSettingsContext
    ) {
        this.page = 1;
        this.totalItems = 0;
        this.currentSearch = '';
        this.predicate = [ColumnLabels.NAME];
        this.reverse = 'asc';
        this.resultSearch = new SearchResult('');
        this.searchRequest = new BreedingMethodSearchRequest();
    }

    ngOnInit(): void {
        this.filters = this.getInitialFilters();
        ColumnFilterComponent.reloadFilters(this.filters, this.request);
        this.registerColumnFiltersChanged();
        this.loadAll(this.request);
        this.registerBreedingMethodsChanged();
    }

    get request() {
        return this.searchRequest;
    }

    set request(request: LocationSearchRequest) {
        this.searchRequest = request;
    }

    get filters() {
        return this.breedingMethodFilters;
    }

    set filters(filters) {
        this.breedingMethodFilters = filters;
    }

    loadAll(request: BreedingMethodSearchRequest) {
        this.isLoading = true;
        this.breedingMethodService.searchBreedingMethods(request, false,
            <Pageable>({
                page: this.page - 1,
                size: this.itemsPerPage,
                sort: this.getSort()
            })
        ).pipe(finalize(() => {
            this.isLoading = false;
        })).subscribe(
            (res: HttpResponse<BreedingMethod[]>) => this.onSuccess(res.body, res.headers),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    loadPage(page: number) {
        if (page !== this.previousPage) {
            this.previousPage = page;
            this.loadAll(this.request);
        }
    }

    sort() {
        this.page = 1;
        this.loadAll(this.request);
    }

    private getSort() {
        if (!this.predicate) {
            return '';
        }

        return [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
    }

    private clearSort() {
        this.predicate = [ColumnLabels.NAME];
        this.reverse = 'asc'
        $('.fa-sort').removeClass('fa-sort-up fa-sort-down');
    }

    onClearSort($event) {
        $event.preventDefault();
        this.clearSort();
        this.loadAll(this.request);
    }

    trackId(index: number, item: Location) {
        return item.id;
    }

    private registerColumnFiltersChanged() {
        this.eventSubscriber = this.eventManager.subscribe(BreedingMethodsPaneComponent.COLUMN_FILTER_EVENT_NAME, (event: ColumnFilterTransitionEventModel) => {
            this.resetTable();
        });
    }

    async loadBreedingMethod() {
        this.loadAll(this.request);
    }

    resetTable() {
        this.page = 1;
        this.previousPage = 1;
        this.loadAll(this.request);
    }

    registerBreedingMethodsChanged(): any {
        this.eventSubscriber = this.eventManager.subscribe('breedingMethodViewChanged', (event) => {
            this.loadAll(this.request);
        });
    }

    private getInitialFilters() {
        const me = this;

        return [
            {
                key: 'nameFilter', name: 'Method Name', placeholder: 'Search Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS,
                matchType: MatchType.STARTSWITH, default: true
            },
            { key: 'description', name: 'Description', placeholder: 'Contains Text', type: FilterType.TEXT },
            {
                key: 'groups', name: 'Group', type: FilterType.DROPDOWN, values: this.getBreedingMethodGroupsOptions(), multipleSelect: true,
                transform(req) {
                    ColumnFilterComponent.transformDropdownFilter(this, req);
                },
                reset(req) {
                    ColumnFilterComponent.resetDropdownFilter(this, req);
                },
            },
            { key: 'methodAbbreviations', name: 'Code', placeholder: 'Match Text', type: FilterType.TEXT,
                transform(req) {
                    req[this.key] = [this.value];
                }
            },
            {
                key: 'methodTypes', name: 'Type', type: FilterType.DROPDOWN, values: this.getBreedingMethodTypesOptions(), multipleSelect: true,
                transform(req) {
                    ColumnFilterComponent.transformDropdownFilter(this, req);
                },
                reset(req) {
                    ColumnFilterComponent.resetDropdownFilter(this, req);
                },
            },
            {
                key: 'methodDate', name: 'Date', type: FilterType.DATE,
                fromKey: 'methodDateFrom',
                toKey: 'methodDateTo',
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
                key: 'methodClassIds', name: 'Class', type: FilterType.DROPDOWN, values: this.getBreedingMethodClassOptions(), multipleSelect: true,
                transform(req) {
                    ColumnFilterComponent.transformDropdownFilter(this, req);
                },
                reset(req) {
                    ColumnFilterComponent.resetDropdownFilter(this, req);
                },
            },
            {
                key: 'methodClassIds', name: 'Class', type: FilterType.DROPDOWN, values: this.getBreedingMethodClassOptions(), multipleSelect: true,
                transform(req) {
                    ColumnFilterComponent.transformDropdownFilter(this, req);
                },
                reset(req) {
                    ColumnFilterComponent.resetDropdownFilter(this, req);
                },
            },
            {
                key: 'snameTypeIds', name: 'Source Name Types', type: FilterType.DROPDOWN, values: this.getNameTypeOptions(), multipleSelect: true,
                transform(req) {
                    ColumnFilterComponent.transformDropdownFilter(this, req);
                },
                reset(req) {
                    ColumnFilterComponent.resetDropdownFilter(this, req);
                },
            }
        ];
    }

    private getBreedingMethodTypesOptions(): Promise<Select2OptionData[]> {
        return this.breedingMethodService.queryBreedingMethodTypes().toPromise().then((types: BreedingMethodType[]) => {
            return types.map((type: BreedingMethodType) => {
                return { id: type.code,
                    text: type.name + ' (' + type.code + ')'
                }
            });
        });
    }

    private getBreedingMethodGroupsOptions(): Promise<Select2OptionData[]> {
        return this.breedingMethodService.queryBreedingMethodGroups().toPromise().then((groups: BreedingMethodGroup[]) => {
            return groups.map((group: BreedingMethodGroup) => {
                return { id: group.code,
                    text: group.name + ' (' + group.code + ')'
                }
            });
        });
    }

    private getBreedingMethodClassOptions(): Promise<Select2OptionData[]> {
        return this.breedingMethodService.queryBreedingMethodClasses().toPromise().then((classes: BreedingMethodClass[]) => {
            return classes.map((clazz: BreedingMethodClass) => {
                return { id: clazz.id.toString(),
                    text: clazz.name
                }
            });
        });
    }

    private getNameTypeOptions(): Promise<Select2OptionData[]> {
        return this.germplasmService.getGermplasmNameTypes([]).toPromise().then((nameTypes: NameType[]) => {
            return nameTypes.map((nameType: NameType) => {
                return { id: nameType.id.toString(),
                    text: nameType.code
                }
            });
        });
    }

    private onSuccess(data: BreedingMethod[], headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.breedingMethods = data;
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
    }

    editBreedingMethod(breedingMethod: any) {
        this.cropSettingsContext.breedingMethod = breedingMethod;
        this.router.navigate(['/', { outlets: { popup: 'breeding-method-edit-dialog' }, }], { queryParamsHandling: 'merge' });

    }

    deleteBreedingMethod(breedingMethod: any) {
        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.title = this.translateService.instant('crop-settings-manager.confirmation.title');
        confirmModalRef.componentInstance.message = this.translateService.instant('crop-settings-manager.breeding-method.modal.delete.warning', { param: breedingMethod.name });
        confirmModalRef.result.then(() => {
            this.breedingMethodService.deleteBreedingMethod(breedingMethod.mid).toPromise().then((result) => {
                this.alertService.success('crop-settings-manager.breeding-method.modal.delete.success');
                this.loadBreedingMethod();
            }).catch((response) => {
                this.alertService.error('error.custom', { param: response.error.errors[0].message });
            });
        }, () => confirmModalRef.dismiss());
    }

}

export enum ColumnLabels {
    'NAME' = 'NAME',
    'DESCRIPTION' = 'DESCRIPTION',
    'GROUP' = 'GROUP',
    'CODE' = 'CODE',
    'TYPE' = 'TYPE',
    'DATE' = 'DATE',
    'CLASS_NAME' = 'CLASS_NAME',
    'SNAME_TYPE_CODE' = 'SNAME_TYPE_CODE'
}
