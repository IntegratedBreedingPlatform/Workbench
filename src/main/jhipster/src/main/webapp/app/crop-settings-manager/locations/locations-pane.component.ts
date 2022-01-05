import { Component, OnInit } from '@angular/core';
import { SearchResult } from '../../shared/search-result.model';
import { Subscription } from 'rxjs';
import { LocationSearchRequest } from '../../shared/location/model/location-search-request.model';
import { ActivatedRoute, Router } from '@angular/router';
import { JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { LocationService } from '../../shared/location/service/location.service';
import { AlertService } from '../../shared/alert/alert.service';
import { ParamContext } from '../../shared/service/param.context';
import { ProgramService } from '../../shared/program/service/program.service';
import { ColumnFilterComponent, FilterType } from '../../shared/column-filter/column-filter.component';
import { finalize } from 'rxjs/operators';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { SORT_PREDICATE_NONE } from '../../germplasm-manager/germplasm-search-resolve-paging-params';
import { ColumnFilterTransitionEventModel } from '../../shared/column-filter/column-filter-transition-event.model';
import { MatchType } from '../../shared/column-filter/column-filter-text-with-match-options-component';
import { Select2OptionData } from 'ng-select2';
import { LocationType } from '../../shared/location/model/location-type';
import { ColumnFilterRadioButtonOption } from '../../shared/column-filter/column-filter-radio-component';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { Location } from '../../shared/location/model/location';


declare var $: any;

@Component({
    selector: 'jhi-locations-pane',
    templateUrl: 'locations-pane.component.html'
})
export class LocationsPaneComponent implements OnInit {

    static readonly COLUMN_FILTER_EVENT_NAME = 'searchColumnFiltersChanged';

    COLUMN_FILTER_EVENT_NAME = LocationsPaneComponent.COLUMN_FILTER_EVENT_NAME;


    ColumnLabels = ColumnLabels;

    eventSubscriber: Subscription;
    locations: Location[];

    currentSearch: string;


    itemsPerPage: any = 20;
    page: number;
    predicate: any;
    previousPage: number;
    reverse: any;
    resultSearch: SearchResult;

    isLoading: boolean;

    locationSearchRequest: LocationSearchRequest;


    totalItems: number;
    locationFilters: any;


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
        this.predicate = [ColumnLabels.LOCATION_NAME];
        this.reverse = 'asc';
        this.resultSearch = new SearchResult('');
        this.locationSearchRequest = new LocationSearchRequest();
    }

    ngOnInit(): void {
        this.filters = this.getInitialFilters();
        ColumnFilterComponent.reloadFilters(this.filters, this.request);
        this.registerColumnFiltersChanged();
        this.loadAll(this.request);
    }

    get request() {
        return this.locationSearchRequest;
    }

    set request(request: LocationSearchRequest) {
        this.locationSearchRequest = request;
    }

    get filters() {
        return this.locationFilters;
    }

    set filters(filters) {
        this.locationFilters = filters;
    }

    loadAll(request: LocationSearchRequest) {
        this.isLoading = true;
        this.locationService.searchLocations(
            request, false,
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
        this.router.navigate(['./'], {
            queryParamsHandling: 'merge',
            queryParams: {
                page: this.page,
                size: this.itemsPerPage,
                search: this.currentSearch,
                sort: this.getSort()
            },
            relativeTo: this.activatedRoute
        });
        this.loadAll(this.request);
    }

    private getSort() {
        if (!this.predicate) {
            return '';
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

    sort() {
        this.page = 1;
        this.transition();
    }

    trackId(index: number, item: Location) {
        return item.id;
    }

    private registerColumnFiltersChanged() {
        this.eventSubscriber = this.eventManager.subscribe(LocationsPaneComponent.COLUMN_FILTER_EVENT_NAME, (event: ColumnFilterTransitionEventModel) => {
            this.resetTable();

            const programFavoriteFilter = event.filtersApplied.filter((filter: any) =>
                filter.key === (filter.value !== undefined));
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
            }
        ];
    }

    private getLocationTypesOptions(): Promise<Select2OptionData[]> {
        return this.locationService.getLocationTypes().toPromise().then((locationTypes: LocationType[]) => {
            return locationTypes.map((locationType: LocationType) => {
                    return { id: locationType.id.toString(),
                        text: locationType.name
                    }
                });
        });
    }

    private onSuccess(data: any[], headers) {
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

    editLocation(location: any) {

    }

    deleteLocation(location: any) {

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
    'TYPE' = 'TYPE'
}
