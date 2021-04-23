import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { LocationService } from '../location/service/location.service';
import { LocationTypeEnum } from '../location/model/location.model';
import { Select2OptionData } from 'ng-select2';

@Component({
    selector: 'jhi-locations-select',
    templateUrl: './locations-select.component.html'
})
export class LocationsSelectComponent implements OnInit {

    @Input() showFilterOptions?: boolean;
    @Input() value: number;
    @Output() valueChange = new EventEmitter<number>();

    // If selectBoxOnly is true, the component will only display the select box with all locations options.
    hideFilterOptions: boolean;

    locationsOptions: any;
    locationSelected: string;
    useFavoriteLocations = true;
    isBreedingAndCountryLocationsOnly;
    locationsFilteredItemsCount;
    initialData: Select2OptionData[];

    constructor(private locationService: LocationService) {

    }

    ngOnInit(): void {

        if (this.value) {
            this.locationSelected = String(this.value);
        }

        this.useFavoriteLocations = this.showFilterOptions

        // The locations are retrieved only when the dropdown is opened, so we have to manually set the initial selected item on first load.
        // Get the location method and add it to the initial data.
        if (this.locationSelected) {
            this.locationService.queryBreedingLocation(this.locationSelected).toPromise().then((location) => {
                this.initialData = [{ id: String(location.id), text: location.abbreviation ? location.name + ' - (' + location.abbreviation + ')' : location.name }];
            });
        }

        this.locationsOptions = {
            ajax: {
                delay: 500,
                transport: function(params, success, failure) {
                    const locationTypes = this.isBreedingAndCountryLocationsOnly ? [LocationTypeEnum.BREEDING_LOCATION, LocationTypeEnum.COUNTRY] : [];
                    this.locationService.queryLocationsByType(locationTypes, this.useFavoriteLocations, params.data.term, params.page, 300).subscribe((res) => {
                        this.locationsFilteredItemsCount = res.headers.get('X-Filtered-Count');
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
