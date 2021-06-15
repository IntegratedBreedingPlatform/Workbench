import { isValidValue } from '../../../../../../../main/webapp/app/shared/ontology/util/is-valid-value';
import { ValidValues } from '../../../../../../../main/webapp/app/shared/ontology/model/valid-values';
import { Category } from '../../../../../../../main/webapp/app/shared/ontology/model/category';
import { VariableDetails } from '../../../../../../../main/webapp/app/shared/ontology/model/variable-details';

describe('isValidValue', () => {
    it('should validate', () => {
        // expected range
        expect(isValidValue(10, <VariableDetails>({ expectedRange: { max: 9 } }))).toBe(false);
        expect(isValidValue(8, <VariableDetails>({ expectedRange: { max: 9 } }))).toBe(true);
        expect(isValidValue(10, <VariableDetails>({ expectedRange: { min: 9 } }))).toBe(true);
        expect(isValidValue(8, <VariableDetails>({ expectedRange: { min: 9 } }))).toBe(false);

        expect(isValidValue(10, <VariableDetails>({ expectedRange: { max: 9, min: 5 } }))).toBe(false);
        expect(isValidValue(6, <VariableDetails>({ expectedRange: { max: 9, min: 5 } }))).toBe(true);
        expect(isValidValue(5, <VariableDetails>({ expectedRange: { max: 9, min: 5 } }))).toBe(true);
        expect(isValidValue(9, <VariableDetails>({ expectedRange: { max: 9, min: 5 } }))).toBe(true);
        expect(isValidValue(4, <VariableDetails>({ expectedRange: { max: 9, min: 5 } }))).toBe(false);

        // scale
        expect(isValidValue(10, <VariableDetails>({ scale: { validValues: { max: 9 } } }))).toBe(false);
        expect(isValidValue(8, <VariableDetails>({ scale: { validValues: { max: 9 } } }))).toBe(true);
        expect(isValidValue(10, <VariableDetails>({ scale: { validValues: { min: 9 } } }))).toBe(true);
        expect(isValidValue(8, <VariableDetails>({ scale: { validValues: { min: 9 } } }))).toBe(false);

        expect(isValidValue(10, <VariableDetails>({ scale: { validValues: { max: 9, min: 5 } } }))).toBe(false);
        expect(isValidValue(6, <VariableDetails>({ scale: { validValues: { max: 9, min: 5 } } }))).toBe(true);
        expect(isValidValue(5, <VariableDetails>({ scale: { validValues: { max: 9, min: 5 } } }))).toBe(true);
        expect(isValidValue(9, <VariableDetails>({ scale: { validValues: { max: 9, min: 5 } } }))).toBe(true);
        expect(isValidValue(4, <VariableDetails>({ scale: { validValues: { max: 9, min: 5 } } }))).toBe(false);

        const categories: Category[] = [{ name: '3' }, { name: '4' }, { name: '5' }]
        expect(isValidValue('2', <VariableDetails>({ scale: { validValues: { categories } } }))).toBe(false);
        expect(isValidValue('3', <VariableDetails>({ scale: { validValues: { categories } } }))).toBe(true);
        expect(isValidValue('5', <VariableDetails>({ scale: { validValues: { categories } } }))).toBe(true);
        expect(isValidValue('6', <VariableDetails>({ scale: { validValues: { categories } } }))).toBe(false);
    });
});
