import { Pipe, PipeTransform } from '@angular/core';
import { Select2OptionData } from 'ng-select2';
import { NameType } from '../../germplasm/model/name-type.model';

@Pipe({ name: 'NameTypeSelect2Data' })
export class NameTypeSelect2Pipe implements PipeTransform {
    transform(nameTypes: NameType[]): Select2OptionData[] {
        if (!nameTypes) {
            return [];
        }
        return nameTypes.map((nameType) => {
            return {
                id: nameType.code,
                text: nameType.code
            };
        });
    }
}
