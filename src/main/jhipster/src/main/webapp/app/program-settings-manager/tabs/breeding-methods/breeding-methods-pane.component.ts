import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { LocationSearchRequest } from '../../../shared/location/model/location-search-request.model';
import { SearchResult } from '../../../shared/search-result.model';
import { Subscription } from 'rxjs';
import { ColumnFilterComponent, FilterType } from '../../../shared/column-filter/column-filter.component';
import { finalize } from 'rxjs/internal/operators/finalize';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { SORT_PREDICATE_NONE } from '../../../germplasm-manager/germplasm-search-resolve-paging-params';
import { MatchType } from '../../../shared/column-filter/column-filter-text-with-match-options-component';
import { formatErrorList } from '../../../shared/alert/format-error-list';
import { ActivatedRoute, Router } from '@angular/router';
import { JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { AlertService } from '../../../shared/alert/alert.service';
import { Location } from '../../../shared/location/model/location';
import { ParamContext } from '../../../shared/service/param.context';
import { ProgramFavoriteAddRequest } from '../../../shared/program/model/program-favorite-add-request.model';
import { ProgramFavoriteTypeEnum } from '../../../shared/program/model/program-favorite-type.enum';
import { ProgramService } from '../../../shared/program/service/program.service';
import { ProgramFavorite } from '../../../shared/program/model/program-favorite.model';
import { ColumnFilterTransitionEventModel } from '../../../shared/column-filter/column-filter-transition-event.model';
import { BreedingMethod } from '../../../shared/breeding-method/model/breeding-method';
import { BreedingMethodSearchRequest } from '../../../shared/breeding-method/model/breeding-method-search-request.model';
import { BreedingMethodService } from '../../../shared/breeding-method/service/breeding-method.service';
import { Select2OptionData } from 'ng-select2';
import { BreedingMethodGroup } from '../../../shared/breeding-method/model/breeding-method-group.model';
import { ColumnFilterRadioButtonOption } from '../../../shared/column-filter/column-filter-radio-component';
import { BreedingMethodType } from '../../../shared/breeding-method/model/breeding-method-type.model';
import { BreedingMethodClass } from '../../../shared/breeding-method/model/breeding-method-class.model';

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

    @ViewChild('fileUpload')
    fileUpload: ElementRef;

    COLUMN_FILTER_EVENT_NAME = BreedingMethodsPaneComponent.COLUMN_FILTER_EVENT_NAME;

    itemsPerPage = 20;

    ColumnLabels = ColumnLabels;

    breedingMethods: BreedingMethod[];
    searchRequest: BreedingMethodSearchRequest;
    eventSubscriber: Subscription;
    resultSearch: SearchResult;

    currentSearch: string;
    totalItems: number;
    page: number;
    previousPage: number;
    predicate: any;
    reverse: any;
    defaultSortApplied: boolean;

    isLoading: boolean;

    locationFilters: any;
    isProgramFavoriteFilterApplied: boolean;
    programFavoriteFilterKey = 'filterFavoriteProgramUUID';

    isTogglingFavoriteProgram: boolean;

    constructor(private activatedRoute: ActivatedRoute,
                private jhiLanguageService: JhiLanguageService,
                private eventManager: JhiEventManager,
                private breedingMethodService: BreedingMethodService,
                private router: Router,
                private alertService: AlertService,
                private context: ParamContext,
                private programService: ProgramService
    ) {
        this.page = 1;
        this.totalItems = 0;
        this.currentSearch = '';
        this.predicate = [ColumnLabels.FAVORITE_PROGRAM_UUID, ColumnLabels.NAME];
        this.defaultSortApplied = true;
        this.isProgramFavoriteFilterApplied = false;
        this.reverse = 'asc';
        this.resultSearch = new SearchResult('');
        this.searchRequest = new BreedingMethodSearchRequest();
    }

    ngOnInit(): void {
        this.filters = this.getInitialFilters();
        ColumnFilterComponent.reloadFilters(this.filters, this.request);
        this.registerColumnFiltersChanged();
        this.loadAll(this.request);
    }

    get request() {
        return this.searchRequest;
    }

    set request(request: LocationSearchRequest) {
        this.searchRequest = request;
    }

    get filters() {
        return this.locationFilters;
    }

    set filters(filters) {
        this.locationFilters = filters;
    }

    loadAll(request: BreedingMethodSearchRequest) {
        this.isLoading = true;
        this.breedingMethodService.searchBreedingMethods(request, false,
            {
                page: this.page - 1,
                size: this.itemsPerPage,
                sort: this.getSort()
            }
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
            this.transition();
        }
    }

    transition() {
        this.loadAll(this.request);
    }

    onPredicateChanged() {
        this.defaultSortApplied = false;
    }

    private getSort() {
        if (this.predicate === SORT_PREDICATE_NONE) {
            return '';
        }
        if (this.defaultSortApplied) {
            return [`${ColumnLabels.FAVORITE_PROGRAM_UUID},desc`, `${ColumnLabels.NAME},asc`];
        }
        return [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
    }

    private clearSort() {
        this.predicate = SORT_PREDICATE_NONE;
        this.reverse = '';
        $('.fa-sort').removeClass('fa-sort-up fa-sort-down');
    }

    onClearSort($event) {
        $event.preventDefault();
        this.clearSort();
        this.transition();
    }

    trackId(index: number, item: Location) {
        return item.id;
    }

    private registerColumnFiltersChanged() {
        this.eventSubscriber = this.eventManager.subscribe(BreedingMethodsPaneComponent.COLUMN_FILTER_EVENT_NAME, (event: ColumnFilterTransitionEventModel) => {
            this.resetTable();

            const programFavoriteFilter = event.filtersApplied.filter((filter: any) =>
                filter.key === this.programFavoriteFilterKey && (filter.value !== undefined));
            this.isProgramFavoriteFilterApplied = (programFavoriteFilter.length === 1) ? true : false;
        });
    }

    resetTable() {
        this.page = 1;
        this.previousPage = 1;
        this.loadAll(this.request);
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
            { key: this.programFavoriteFilterKey, name: 'Program Favorite', type: FilterType.RADIOBUTTON, options: this.getProgramFavoriteFilterOptions(),
                transform(req) {
                    req[this.key] = this.value;
                    req['favoriteProgramUUID'] = (this.value) ? me.context.programUUID : null;
                }
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

    private getProgramFavoriteFilterOptions(): Promise<ColumnFilterRadioButtonOption[]> {
        return new Promise<ColumnFilterRadioButtonOption[]>((resolve, reject) => {
            resolve([new ColumnFilterRadioButtonOption(true, 'Yes'),
                new ColumnFilterRadioButtonOption(false, 'No')]);
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

    isProgramFavorite(breedingMethod: BreedingMethod): boolean {
        const programFavorite: ProgramFavorite = this.getProgramFavorite(breedingMethod);
        return programFavorite && this.context.programUUID === programFavorite.programUUID;
    }

    private getProgramFavorite(breedingMethod: BreedingMethod): ProgramFavorite {
        // We get the first item because for now on the search location response only returns the program favorite of the current program
        return (breedingMethod.programFavorites && breedingMethod.programFavorites.length === 1) ? breedingMethod.programFavorites[0] : null;
    }

    private toggleProgramFavorite(breedingMethod: BreedingMethod) {
        // Prevent multiple click while a program favorite is being added/removed
        if (this.isTogglingFavoriteProgram) {
            return;
        }

        this.isTogglingFavoriteProgram = true;

        if (this.isProgramFavorite(breedingMethod)) {
            this.programService.removeProgramFavorite([breedingMethod.programFavorites[0].programFavoriteId])
                .pipe(finalize(() => {
                    this.isTogglingFavoriteProgram = false;
                })).subscribe(
                (res: void) => this.onToggleProgramFavoriteSuccess(breedingMethod, false, null),
                (res: HttpErrorResponse) => this.onError(res)
            );
        } else {
            this.programService.addProgramFavorite(
                new ProgramFavoriteAddRequest(ProgramFavoriteTypeEnum.METHOD, [breedingMethod.mid]))
                .pipe(finalize(() => {
                    this.isTogglingFavoriteProgram = false;
                })).subscribe(
                (res: HttpResponse<ProgramFavorite[]>) => this.onToggleProgramFavoriteSuccess(breedingMethod, true, res.body[0]),
                (res: HttpErrorResponse) => this.onError(res)
            );
        }
    }

    private onToggleProgramFavoriteSuccess(breedingMethod: BreedingMethod, wasAdded: boolean, programFavorite: ProgramFavorite) {
        if (wasAdded) {
            breedingMethod.programFavorites = [
                new ProgramFavorite(programFavorite.programFavoriteId, programFavorite.entityType, programFavorite.entityId, programFavorite.programUUID)
            ];
        } else {
            breedingMethod.programFavorites = null;
        }

        if (this.isProgramFavoriteFilterApplied) {
            this.resetTable();
        }
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
    'FAVORITE_PROGRAM_UUID' = 'FAVORITE_PROGRAM_UUID',
}
