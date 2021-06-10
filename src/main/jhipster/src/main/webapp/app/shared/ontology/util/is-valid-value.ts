import { ValidValues } from '../model/valid-values';
import { VariableDetails } from '../model/variable-details';

export function isValidValue(value, variable: VariableDetails) {
    if (!variable) {
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
