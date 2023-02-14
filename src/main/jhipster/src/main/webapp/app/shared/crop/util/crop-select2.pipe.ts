import { Pipe, PipeTransform } from '@angular/core';
import { Select2OptionData } from 'ng-select2';

@Pipe({ name: 'CropSelect2Data' })
export class CropSelect2DataPipe implements PipeTransform {
    transform(crops: string[]): Select2OptionData[] {
        if (!crops) {
            return [];
        }
        return crops.map((crop) => {
            return {
                id: crop,
                text: crop
            };
        });
    }
}
