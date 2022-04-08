import { Component, OnInit } from '@angular/core';
import { SearchResult } from '../../shared/search-result.model';
import { Subscription } from 'rxjs';
import { LocationSearchRequest } from '../../shared/location/model/location-search-request.model';
import { ActivatedRoute, Router } from '@angular/router';
import { JhiEventManager } from 'ng-jhipster';
import { LocationService } from '../../shared/location/service/location.service';
import { AlertService } from '../../shared/alert/alert.service';
import { ColumnFilterComponent, FilterType } from '../../shared/column-filter/column-filter.component';
import { finalize } from 'rxjs/operators';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { ColumnFilterTransitionEventModel } from '../../shared/column-filter/column-filter-transition-event.model';
import { MatchType } from '../../shared/column-filter/column-filter-text-with-match-options-component';
import { Select2OptionData } from 'ng-select2';
import { LocationType } from '../../shared/location/model/location-type';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { Location } from '../../shared/location/model/location';
import { CropSettingsContext } from '../crop-Settings.context';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { Pageable } from '../../shared/model/pageable';
import { JhiLanguageService } from 'ng-jhipster/src/language';

declare var $: any;

@Component({
    selector: 'jhi-locations-pane',
    templateUrl: 'locations-pane.component.html'
})
export class LocationsPaneComponent implements OnInit {

    static readonly COLUMN_FILTER_EVENT_NAME = 'searchColumnFiltersChanged';
    static readonly RESTRICTED_LOCATION_TYPE = [401, 405, 406];
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

    constructor(public translateService: TranslateService,
                private activatedRoute: ActivatedRoute,
                private jhiLanguageService: JhiLanguageService,
                private eventManager: JhiEventManager,
                private locationService: LocationService,
                private router: Router,
                private alertService: AlertService,
                private modalService: NgbModal,
                private cropSettingsContext: CropSettingsContext,
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
        this.registerLocationChanged();
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
            <Pageable>({
                page: this.page - 1,
                size: this.itemsPerPage,
                sort: this.getSort()
            })
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
            this.loadAll(this.request);
        }
    }

    private getSort() {
        if (!this.predicate) {
            return '';
        }
        return [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
    }

    private clearSort() {
        this.predicate = [ColumnLabels.LOCATION_NAME];
        this.reverse = 'asc';
        $('.fa-sort').removeClass('fa-sort-up fa-sort-down');
    }

    onClearSort($event) {
        $event.preventDefault();
        this.clearSort();
        this.loadAll(this.request);
    }

    sort() {
        this.page = 1;
        this.loadAll(this.request);
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

    async loadLocations() {
        this.loadAll(this.request);
    }

    resetTable() {
        this.page = 1;
        this.previousPage = 1;
        this.loadAll(this.request);
    }

    registerLocationChanged() {
        this.eventSubscriber = this.eventManager.subscribe('locationViewChanged', (event) => {
            this.loadAll(this.request);
        });
    }

    private getInitialFilters() {
        const me = this;

        return [
            { key: 'locationNameFilter', name: 'Location Name', placeholder: 'Search Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS,
                matchType: MatchType.STARTSWITH, default: true
            },
            { key: 'locationAbbreviations', name: 'Abbreviation', placeholder: 'Match Text', type: FilterType.TEXT,
                transform(req) {
                    req[this.key] = [this.value];
                }
            },
            { key: 'locationIds', name: 'Location ID', type: FilterType.LIST },
            { key: 'countryName', name: 'Country Name', placeholder: 'Contains Text', type: FilterType.TEXT },
            { key: 'provinceName', name: 'Province Name', placeholder: 'Contains Text', type: FilterType.TEXT },
            { key: 'latitude', name: 'Latitude', type: FilterType.NUMBER_RANGE,
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
            { key: 'longitude', name: 'Longitude', type: FilterType.NUMBER_RANGE,
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
            { key: 'altitude', name: 'Altitude', type: FilterType.NUMBER_RANGE,
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
            { key: 'locationTypeIds', name: 'Location type', type: FilterType.DROPDOWN, values: this.getLocationTypesOptions(), multipleSelect: true,
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
        this.cropSettingsContext.location = location;
        this.router.navigate(['/', { outlets: { popup: 'location-edit-dialog' }, }], { queryParamsHandling: 'merge' });

    }

    deleteLocation(location: any) {
        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.title = this.translateService.instant('crop-settings-manager.confirmation.title');
        confirmModalRef.componentInstance.message = this.translateService.instant('crop-settings-manager.location.modal.delete.warning', { param: location.name });
        confirmModalRef.result.then(() => {
            this.locationService.deleteLocation(location.id).toPromise().then((result) => {
                this.alertService.success('crop-settings-manager.location.modal.delete.success');
                this.loadLocations();
            }).catch((response) => {
                this.alertService.error('error.custom', { param: response.error.errors[0].message });
            });
        }, () => confirmModalRef.dismiss());
    }

    validLocation(type) {
        return LocationsPaneComponent.RESTRICTED_LOCATION_TYPE.includes(type);
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
