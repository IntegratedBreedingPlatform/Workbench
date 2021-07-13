import { Pipe, PipeTransform } from '@angular/core';
import { Select2OptionData } from 'ng-select2';
import { Attribute } from './attribute.model';

@Pipe({ name: 'AttributeSelect2Data' })
export class AttributeSelect2DataPipe implements PipeTransform {
    transform(attributes: Attribute[]): Select2OptionData[] {
        if (!attributes) {
            return [];
        }
        return attributes.map((attribute) => {
            return {
                id: attribute.code,
                text: attribute.code
            };
        });
    }
}
