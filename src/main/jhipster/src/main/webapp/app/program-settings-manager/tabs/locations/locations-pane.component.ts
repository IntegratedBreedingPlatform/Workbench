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
import { LocationService } from '../../../shared/location/service/location.service';
import { Location } from '../../../shared/location/model/location';
import { LocationType } from '../../../shared/location/model/location-type';
import { Select2OptionData } from 'ng-select2';
import { ParamContext } from '../../../shared/service/param.context';
import { ProgramFavoriteAddRequest } from '../../../shared/program/model/program-favorite-add-request.model';
import { ProgramFavoriteTypeEnum } from '../../../shared/program/model/program-favorite-type.enum';
import { ProgramService } from '../../../shared/program/service/program.service';
import { ProgramFavorite } from '../../../shared/program/model/program-favorite.model';
import { ColumnFilterRadioButtonOption } from '../../../shared/column-filter/column-filter-radio-component';
import { ColumnFilterTransitionEventModel } from '../../../shared/column-filter/column-filter-transition-event.model';

declare var $: any;

@Component({
    selector: 'jhi-locations-pane',
    templateUrl: 'locations-pane.component.html'
})
export class LocationsPaneComponent implements OnInit {

    static readonly COLUMN_FILTER_EVENT_NAME = 'searchColumnFiltersChanged';

    @ViewChild('fileUpload')
    fileUpload: ElementRef;

    COLUMN_FILTER_EVENT_NAME = LocationsPaneComponent.COLUMN_FILTER_EVENT_NAME;

    itemsPerPage = 20;

    ColumnLabels = ColumnLabels;

    locations: Location[];
    searchRequest: LocationSearchRequest;
    eventSubscriber: Subscription;
    resultSearch: SearchResult;

    currentSearch: string;
    totalItems: number;
    page: number;
    previousPage: number;
    predicate: any;
    reverse: boolean;

    isLoading: boolean;

    locationFilters: any;
    isProgramFavoriteFilterApplied: boolean;
    programFavoriteFilterKey = 'filterFavoriteProgramUUID';

    isTogglingFavoriteProgram: boolean;

    constructor(private activatedRoute: ActivatedRoute,
                private jhiLanguageService: JhiLanguageService,
                private eventManager: JhiEventManager,
                private locationService: LocationService,
                private router: Router,
                private alertService: AlertService,
                private context: ParamContext,
                private programService: ProgramService
    ) {
        this.page = 1;
        this.totalItems = 0;
        this.currentSearch = '';
        this.predicate = ColumnLabels.LOCATION_NAME;
        this.isProgramFavoriteFilterApplied = false;
        this.reverse = true;
        this.resultSearch = new SearchResult('');
        this.searchRequest = new LocationSearchRequest();
        this.searchRequest.filterFavoriteProgramUUID = true;
        this.searchRequest.favoriteProgramUUID = this.context.programUUID;
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

    loadAll(request: LocationSearchRequest) {
        this.isLoading = true;
        this.locationService.searchLocations(request, false,
            {
                page: this.page - 1,
                size: this.itemsPerPage,
                sort: this.getSort()
            }
        ).pipe(finalize(() => {
            this.isLoading = false;
        })).subscribe(
            (res: HttpResponse<Location[]>) => this.onSuccess(res.body, res.headers),
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

    private getSort() {
        if (this.predicate === SORT_PREDICATE_NONE) {
            return '';
        }
        return [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
    }

    private clearSort() {
        this.predicate = SORT_PREDICATE_NONE;
        this.reverse = null;
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
        this.eventSubscriber = this.eventManager.subscribe(LocationsPaneComponent.COLUMN_FILTER_EVENT_NAME, (event: ColumnFilterTransitionEventModel) => {
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
                key: 'locationNameFilter', name: 'Location Name', placeholder: 'Search Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS,
                matchType: MatchType.STARTSWITH, default: true
            },
            { key: 'locationAbbreviations', name: 'Abbreviation', placeholder: 'Match Text', type: FilterType.TEXT,
                transform(req) {
                    req[this.key] = [this.value];
                }},
            { key: 'locationIds', name: 'Location ID', type: FilterType.LIST },
            { key: 'countryName', name: 'Country Name', placeholder: 'Contains Text',  type: FilterType.TEXT },
            { key: 'provinceName', name: 'Province Name', placeholder: 'Contains Text',  type: FilterType.TEXT },
            {
                key: 'latitude', name: 'Latitude', type: FilterType.NUMBER_RANGE,
                fromKey: 'latitudeFrom',
                toKey: 'latitudeTo',
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
            {
                key: 'longitude', name: 'Longitude', type: FilterType.NUMBER_RANGE,
                fromKey: 'longitudeFrom',
                toKey: 'longitudeTo',
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
            {
                key: 'altitude', name: 'Altitude', type: FilterType.NUMBER_RANGE,
                fromKey: 'altitudeFrom',
                toKey: 'altitudeTo',
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
            {
                key: 'locationTypeIds', name: 'Location type', type: FilterType.DROPDOWN, values: this.getLocationTypesOptions(), multipleSelect: true,
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
                },
                default: true
            }
        ];
    }

    private getLocationTypesOptions(): Promise<Select2OptionData[]> {
        return this.locationService.getLocationTypes(false).toPromise().then((locationTypes: LocationType[]) => {
            return locationTypes.map((locationType: LocationType) => {
                    return { id: locationType.id.toString(),
                        text: locationType.name
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

    private onSuccess(data: Location[], headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.locations = data;
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
    }

    isProgramFavorite(location: Location): boolean {
        const programFavorite: ProgramFavorite = this.getProgramFavorite(location);
        return programFavorite && this.context.programUUID === programFavorite.programUUID;
    }

    private getProgramFavorite(location: Location): ProgramFavorite {
        // We get the first item because for now on the search location response only returns the program favorite of the current program
        return (location.programFavorites && location.programFavorites.length === 1) ? location.programFavorites[0] : null;
    }

    private toggleProgramFavorite(location: Location) {
        // Prevent multiple click while a program favorite is being added/removed
        if (this.isTogglingFavoriteProgram) {
            return;
        }

        this.isTogglingFavoriteProgram = true;

        if (this.isProgramFavorite(location)) {
            this.programService.removeProgramFavorite([location.programFavorites[0].programFavoriteId])
                .pipe(finalize(() => {
                    this.isTogglingFavoriteProgram = false;
                })).subscribe(
                    (res: void) => this.onToggleProgramFavoriteSuccess(location, false, null),
                    (res: HttpErrorResponse) => this.onError(res)
            );
        } else {
            this.programService.addProgramFavorite(
                new ProgramFavoriteAddRequest(ProgramFavoriteTypeEnum.LOCATION, [location.id]))
                .pipe(finalize(() => {
                    this.isTogglingFavoriteProgram = false;
                })).subscribe(
                    (res: HttpResponse<ProgramFavorite[]>) => this.onToggleProgramFavoriteSuccess(location, true, res.body[0]),
                    (res: HttpErrorResponse) => this.onError(res)
            );
        }
    }

    private onToggleProgramFavoriteSuccess(location: Location, wasAdded: boolean, programFavorite: ProgramFavorite) {
        if (wasAdded) {
            location.programFavorites = [new ProgramFavorite(programFavorite.programFavoriteId, programFavorite.entityType, programFavorite.entityId, programFavorite.programUUID)];
        } else {
            location.programFavorites = null;
        }

        if (this.isProgramFavoriteFilterApplied) {
            this.resetTable();
        }
    }

}

export enum ColumnLabels {
    'LOCATION_NAME' = 'LOCATION_NAME',
    'ABBREVIATION' = 'ABBREVIATION',
    'LOCATION_ID' = 'LOCATION_ID',
    'COUNTRY' = 'COUNTRY',
    'PROVINCE' = 'PROVINCE',
    'LATITUDE' = 'LATITUDE',
    'LONGITUDE' = 'LONGITUDE',
    'ALTITUDE' = 'ALTITUDE',
    'TYPE' = 'TYPE',
    'FAVORITE_PROGRAM_UUID' = 'FAVORITE_PROGRAM_UUID',
}
