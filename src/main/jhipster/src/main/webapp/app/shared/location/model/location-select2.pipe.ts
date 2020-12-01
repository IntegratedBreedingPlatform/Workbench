import { Pipe, PipeTransform } from '@angular/core';
import { Location } from './location';
import { Select2OptionData } from 'ng-select2';

@Pipe({ name: 'LocationSelect2Data' })
export class LocationSelect2DataPipe implements PipeTransform {
    transform(locations: Location[]): Select2OptionData[] {
        if (!locations) {
            return [];
        }

        return locations.map((location) => {
            return {
                id: location.abbreviation ? location.abbreviation : location.name, //TODO Fix me: Case when the abbreviation is null.
                text: location.abbreviation ?  location.name + ' - (' + location.abbreviation + ')' : location.name
            };
        });
    }
}
