import { Category } from '../../../../../../../main/webapp/app/shared/ontology/model/category';
import { VariableDetails } from '../../../../../../../main/webapp/app/shared/ontology/model/variable-details';
import { VariableValidationService } from '../../../../../../../main/webapp/app/shared/ontology/service/variable-validation.service';
import { TestBed } from '@angular/core/testing';
import { BmsjHipsterTestModule } from '../../../../test.module';

describe('VariableValidationService', () => {
    let variableValidationService: VariableValidationService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [BmsjHipsterTestModule],
            providers: [VariableValidationService]
        })
        // TODO angular 9, TestBed.inject()
        variableValidationService = TestBed.get(VariableValidationService);
    });

    it('should validate min and max in expected range', () => {
        expect(variableValidationService.isValidValue(10, <VariableDetails>({ expectedRange: { max: 9 } }))).toBe(false);
        expect(variableValidationService.isValidValue(8, <VariableDetails>({ expectedRange: { max: 9 } }))).toBe(true);
        expect(variableValidationService.isValidValue(10, <VariableDetails>({ expectedRange: { min: 9 } }))).toBe(true);
        expect(variableValidationService.isValidValue(8, <VariableDetails>({ expectedRange: { min: 9 } }))).toBe(false);
    });

    it('should validate min-max ranges in expected range', () => {
        expect(variableValidationService.isValidValue(10, <VariableDetails>({ expectedRange: { max: 9, min: 5 } }))).toBe(false);
        expect(variableValidationService.isValidValue(6, <VariableDetails>({ expectedRange: { max: 9, min: 5 } }))).toBe(true);
        expect(variableValidationService.isValidValue(5, <VariableDetails>({ expectedRange: { max: 9, min: 5 } }))).toBe(true);
        expect(variableValidationService.isValidValue(9, <VariableDetails>({ expectedRange: { max: 9, min: 5 } }))).toBe(true);
        expect(variableValidationService.isValidValue(4, <VariableDetails>({ expectedRange: { max: 9, min: 5 } }))).toBe(false);
    });

    it('should validate min and max scale valid values', () => {
        expect(variableValidationService.isValidValue(10, <VariableDetails>({ scale: { validValues: { max: 9 } } }))).toBe(false);
        expect(variableValidationService.isValidValue(8, <VariableDetails>({ scale: { validValues: { max: 9 } } }))).toBe(true);
        expect(variableValidationService.isValidValue(10, <VariableDetails>({ scale: { validValues: { min: 9 } } }))).toBe(true);
        expect(variableValidationService.isValidValue(8, <VariableDetails>({ scale: { validValues: { min: 9 } } }))).toBe(false);
    });

    it('should validate min-max ranges in scale valid values', () => {
        expect(variableValidationService.isValidValue(10, <VariableDetails>({ scale: { validValues: { max: 9, min: 5 } } }))).toBe(false);
        expect(variableValidationService.isValidValue(6, <VariableDetails>({ scale: { validValues: { max: 9, min: 5 } } }))).toBe(true);
        expect(variableValidationService.isValidValue(5, <VariableDetails>({ scale: { validValues: { max: 9, min: 5 } } }))).toBe(true);
        expect(variableValidationService.isValidValue(9, <VariableDetails>({ scale: { validValues: { max: 9, min: 5 } } }))).toBe(true);
        expect(variableValidationService.isValidValue(4, <VariableDetails>({ scale: { validValues: { max: 9, min: 5 } } }))).toBe(false);
    });

    it('should validate categories', () => {
        const categories: Category[] = [{ name: '3' }, { name: '4' }, { name: '5' }]
        expect(variableValidationService.isValidValue('2', <VariableDetails>({ scale: { validValues: { categories } } }))).toBe(false);
        expect(variableValidationService.isValidValue('3', <VariableDetails>({ scale: { validValues: { categories } } }))).toBe(true);
        expect(variableValidationService.isValidValue('5', <VariableDetails>({ scale: { validValues: { categories } } }))).toBe(true);
        expect(variableValidationService.isValidValue('6', <VariableDetails>({ scale: { validValues: { categories } } }))).toBe(false);
    });
});
