import { Component, Input, OnInit } from '@angular/core';
import { LocationService } from '../location/service/location.service';
import { LocationTypeEnum } from '../location/model/location.model';

@Component({
    selector: 'jhi-locations-select',
    templateUrl: './locations-select.component.html'
})
export class LocationsSelectComponent implements OnInit {

    @Input() name: string;

    locationsOptions: any;
    locationSelected: string;
    useFavoriteLocations = true;
    isBreedingAndCountryLocationsOnly = false;
    locationsFilteredItemsCount;

    constructor(private locationService: LocationService) {
    }

    ngOnInit(): void {

        this.locationsOptions = {
            ajax: {
                delay: 500,
                transport: function (params, success, failure) {
                    let locationTypes = this.isBreedingAndCountryLocationsOnly ? [LocationTypeEnum.BREEDING_LOCATION, LocationTypeEnum.COUNTRY] : [];
                    this.locationService.queryLocationsByType(locationTypes, this.useFavoriteLocations, params.data.term, params.page, 300).subscribe((res) => {
                        this.locationsFilteredItemsCount = res.headers.get('X-Filtered-Count');
                        success(res.body);
                    }, failure);
                }.bind(this),
                processResults: function (locations, params) {
                    params.page = params.page || 1;

                    return {
                        results: locations.map((location) => {
                            return {
                                id: location.abbreviation ? location.abbreviation : location.name,
                                text: location.abbreviation ? location.name + ' - (' + location.abbreviation + ')' : location.name
                            };
                        }),
                        pagination: {
                            more: (params.page * 300) < this.locationsFilteredItemsCount
                        }
                    };
                }.bind(this)
            },
            selectionCssClass: 'form-control'
        };

    }


}
