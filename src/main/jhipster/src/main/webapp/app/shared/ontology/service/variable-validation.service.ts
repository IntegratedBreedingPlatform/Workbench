import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { VariableDetails } from '../model/variable-details';
import { DataTypeEnum } from '../data-type.enum';
import { isNumeric } from '../../util/is-numeric';
import { DateFormatEnum, isValidDate } from '../../util/date-utils';
import { ObservationVariable } from '../../model/observation-variable.model';
import { convertToVariableDetails } from '../variable-utils';

@Injectable()
export class VariableValidationService {
    constructor(
        private translateService: TranslateService
    ) {
    }

    /**
     * <pre>
     * returns
     *   {
     *       isValid: if categorical and is one of the accepted values
     *                or if numerical and the value is numeric
     *                or if date and value is a valid ISO date
     *       isInRange: if numerical and the value is both inside scale range and expected range
     *   }
     * </pre>
     */
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
            if (dataType.name === DataTypeEnum.NUMERIC) {
                isValid = isValid && isNumeric(value);
            } else if (dataType.name === DataTypeEnum.DATE) {
                isValid = isValid && isValidDate(value, DateFormatEnum.ISO_8601_NUMBER);
            }
        }

        return { isValid, isInRange };
    }

    isValidValueObservation(value, variable: ObservationVariable): VariableValidationStatusType {
        return this.isValidValue(value, convertToVariableDetails(variable));
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
