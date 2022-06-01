import { Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { LocationService } from '../location/service/location.service';
import { Select2OptionData } from 'ng-select2';
import { LocationTypeEnum } from '../location/model/location-type.enum';
import { LocationSearchRequest } from '../location/model/location-search-request.model';
import { MatchType } from '../column-filter/column-filter-text-with-match-options-component';
import { JhiEventManager } from 'ng-jhipster';
import { Subscription } from 'rxjs';

@Component({
    selector: 'jhi-locations-select',
    templateUrl: './locations-select.component.html'
})
export class LocationsSelectComponent implements OnInit, OnChanges {

    @Input() showFilterOptions?: boolean;
    @Input() value: number;
    @Input() cropName?: string;
    @Input() disabled = false;
    @Output() valueChange = new EventEmitter<number>();

    // If selectBoxOnly is true, the component will only display the select box with all locations options.
    hideFilterOptions: boolean;

    eventSubscriber: Subscription;

    locationsOptions: any;
    locationSelected: string;
    useFavoriteLocations = true;
    isBreedingAndCountryLocationsOnly;
    locationsFilteredItemsCount;
    initialData: Select2OptionData[];
    previousCropName: string;

    constructor(private locationService: LocationService,
                private eventManager: JhiEventManager) {
        this.eventSubscriber = this.eventManager.subscribe('resetLocationToDefault', (event) => {
            this.setLocationToDefault();
        });
    }

    ngOnInit(): void {
        this.useFavoriteLocations = this.showFilterOptions;
        // The locations are retrieved only when the dropdown is opened, so we have to manually set the initial selected item on first load.
        // Get the location method and add it to the initial data.
        if (this.value) {
            this.locationSelected = String(this.value);
            this.locationService.getLocationById(this.locationSelected).toPromise().then((location) => {
                this.initialData = [{ id: String(location.id), text: location.abbreviation ? location.name + ' - (' + location.abbreviation + ')' : location.name }];
            });
        } else if (!this.cropName) {
            this.setLocationToDefault();
        }

        this.instantiateLocationOptions();
    }

    ngOnChanges(changes): void {
        if (this.cropName && this.cropName !== this.previousCropName) {
            this.initialData = [];
            this.locationSelected = null;
            this.value = null;
            this.previousCropName = this.cropName;
            this.instantiateLocationOptions();
        }
    }

    setLocationToDefault(): void {
        // Set the selected location to the default location
        this.locationService.getDefaultLocation().toPromise().then((location) => {
            this.initialData = [{ id: String(location.id), text: location.abbreviation ? location.name + ' - (' + location.abbreviation + ')' : location.name }];
            this.value = location.id;
            this.locationSelected = String(this.value);
        });
    }

    instantiateLocationOptions(): void {
        this.locationsOptions = {
            ajax: {
                delay: 500,
                transport: function(params, success, failure) {
                    params.data.page = params.data.page || 1;

                    const locationSearchRequest: LocationSearchRequest = new LocationSearchRequest();
                    locationSearchRequest.locationTypeIds = (this.isBreedingAndCountryLocationsOnly) ? [LocationTypeEnum.BREEDING_LOCATION, LocationTypeEnum.COUNTRY] : [];
                    locationSearchRequest.locationNameFilter = {
                        type: MatchType.STARTSWITH,
                        value: params.data.term
                    };

                    const pagination = {
                        page: (params.data.page - 1),
                        size: 300
                    };

                    const useFaveLocations = this.cropName ? false : this.useFavoriteLocations;
                    this.locationService.searchLocations(locationSearchRequest, useFaveLocations, pagination, this.cropName).subscribe((res) => {
                        this.locationsFilteredItemsCount = res.headers.get('X-Total-Count');
                        success(res.body);
                    }, failure);
                }.bind(this),
                processResults: function(locations, params) {
                    params.page = params.page || 1;

                    return {
                        results: locations.map((location) => {
                            return {
                                id: String(location.id),
                                text: location.abbreviation ? location.name + ' - (' + location.abbreviation + ')' : location.name
                            };
                        }),
                        pagination: {
                            more: (params.page * 300) < this.locationsFilteredItemsCount
                        }
                    };
                }.bind(this)
            }
        };
    }

    onValueChanged($event): void {
        this.valueChange.emit(Number($event));
    }
}
