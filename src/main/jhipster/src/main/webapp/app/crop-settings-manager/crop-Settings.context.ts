import { Injectable } from '@angular/core';
import { NameTypeDetails } from '../shared/germplasm/model/name-type.model';
import { Location } from '../shared/location/model/location';

@Injectable()
export class CropSettingsContext {
    nameTypeDetails: NameTypeDetails;
    location: Location;
}
