import { Injectable } from '@angular/core';
import { NameTypeDetails } from '../shared/germplasm/model/name-type.model';
import { Location } from '../shared/location/model/location';
import { BreedingMethod } from '../shared/breeding-method/model/breeding-method';

@Injectable()
export class CropSettingsContext {
    nameTypeDetails: NameTypeDetails;
    breedingMethod: BreedingMethod;
    location: Location;
}
