import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { VariableDetails } from '../model/variable-details';
import { DataType } from '../data-type';

@Injectable()
export class VariableValidationService {
    constructor(
        private translateService: TranslateService
    ) {
    }

    isValidValue(value, variable: VariableDetails) {
        if (!variable || !value) {
            return true;
        }
        let isValid = true;

        const expectedRange = variable.expectedRange;
        if (expectedRange) {
            if (expectedRange.max || expectedRange.max === 0) {
                isValid = isValid && value <= expectedRange.max;
            }
            if (expectedRange.min || expectedRange.min === 0) {
                isValid = isValid && value >= expectedRange.min;
            }
        }

        if (variable.scale && variable.scale.validValues) {
            const validValues = variable.scale.validValues;
            if (validValues.max || validValues.max === 0) {
                isValid = isValid && value <= validValues.max;
            }
            if (validValues.min || validValues.min === 0) {
                isValid = isValid && value >= validValues.min;
            }
            const categories = validValues.categories;
            if (categories && categories.length) {
                isValid = isValid && categories.some((category) => category.name === value)
            }
        }

        return isValid;
    }

    getStatus(attribute: VariableDetails, attributeStatusById: {[key: number]: boolean}) {
        const hasSomeInvalid = attributeStatusById[attribute.id];
        if (!hasSomeInvalid) {
            return '<span class="fa fa-lg fa-check-circle text-success"></span>'
        } else if (attribute.scale.dataType.name === DataType.CATEGORICAL) {
            const scale = attribute.scale.name;
            const dataType = attribute.scale.dataType.name;
            const title = this.translateService.instant('germplasm.import.basicDetails.attributes.tooltip.invalid', { scale, dataType });
            return '<span class="fa fa-lg fa-times-circle text-danger" title="' + title + '"></span>'
        } else if (attribute.scale.dataType.name === DataType.NUMERIC) {
            const min = attribute.scale.validValues && (
                attribute.scale.validValues.min || attribute.scale.validValues.min === 0)
                ? attribute.scale.validValues.min
                : attribute.expectedRange.min;
            const max = attribute.scale.validValues && (
                attribute.scale.validValues.max || attribute.scale.validValues.max === 0)
                ? attribute.scale.validValues.max
                : attribute.expectedRange.max;
            const title = this.translateService.instant('germplasm.import.basicDetails.attributes.tooltip.outOfExpected', { min, max });
            return '<span class="fa fa-lg fa-warning text-warning" title="' + title + '"></span>'
        }
    }
}
