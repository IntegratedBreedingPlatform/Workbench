import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { VariableDetails } from '../model/variable-details';
import { DataType } from '../data-type';
import { isNumeric } from '../../util/is-numeric';

@Injectable()
export class VariableValidationService {
    constructor(
        private translateService: TranslateService
    ) {
    }

    isValidValue(value, variable: VariableDetails): VariableValidationStatusType {
        let isInRange = true,
            isValid = true;

        if (!variable || !value) {
            return { isValid, isInRange };
        }

        const expectedRange = variable.expectedRange;
        if (expectedRange) {
            if (expectedRange.max || expectedRange.max === 0) {
                isInRange = isInRange && value <= expectedRange.max;
            }
            if (expectedRange.min || expectedRange.min === 0) {
                isInRange = isInRange && value >= expectedRange.min;
            }
        }

        if (variable.scale && variable.scale.validValues) {
            const validValues = variable.scale.validValues;
            if (validValues.max || validValues.max === 0) {
                isInRange = isInRange && value <= validValues.max;
            }
            if (validValues.min || validValues.min === 0) {
                isInRange = isInRange && value >= validValues.min;
            }

            const categories = validValues.categories;
            if (categories && categories.length) {
                isValid = isValid && categories.some((category) => category.name === value)
            }
        }

        if (variable.scale && variable.scale.dataType) {
            const dataType = variable.scale.dataType;
            if (dataType && dataType.name === DataType.NUMERIC) {
                isValid = isValid && isNumeric(value);
            }
        }

        return { isValid, isInRange };
    }

    getStatusIcon(attribute: VariableDetails, attributeStatusById: {[key: number]: VariableValidationStatusType}) {
        const validationStatus: VariableValidationStatusType = attributeStatusById[attribute.id];
        const successIcon = '<span class="fa fa-lg fa-check-circle text-success"></span>';
        if (!validationStatus) {
            return successIcon;
        } else if (!validationStatus.isValid) {
            const scale = attribute.scale.name;
            const dataType = attribute.scale.dataType.name;
            const title = this.translateService.instant('germplasm.import.basicDetails.attributes.tooltip.invalid', { scale, dataType });
            return '<span class="fa fa-lg fa-times-circle text-danger" title="' + title + '"></span>'
        } else if (!validationStatus.isInRange) {
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
        } else {
            return successIcon;
        }
    }
}

export type VariableValidationStatusType = { isInRange?: boolean, isValid?: boolean };
