import { Pipe, PipeTransform } from '@angular/core';
import { BreedingMethod } from './breeding-method';
import { Select2OptionData } from 'ng-select2';

@Pipe({ name: 'BreedingMethodSelect2Data' })
export class BreedingMethodSelect2DataPipe implements PipeTransform {
    transform(breedingMethods: BreedingMethod[]): Select2OptionData[] {
        if (!breedingMethods) {
            return [];
        }
        return breedingMethods.map((breedingMethod) => {
            return {
                id: breedingMethod.code,
                text: breedingMethod.code + ' - ' + breedingMethod.name
            };
        });
    }
}
