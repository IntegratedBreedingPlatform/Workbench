import { Category } from '../../../../../../../main/webapp/app/shared/ontology/model/category';
import { VariableDetails } from '../../../../../../../main/webapp/app/shared/ontology/model/variable-details';
import { VariableValidationService, VariableValidationStatusType } from '../../../../../../../main/webapp/app/shared/ontology/service/variable-validation.service';
import { TestBed } from '@angular/core/testing';
import { BmsjHipsterTestModule } from '../../../../test.module';
import { DataType } from '../../../../../../../main/webapp/app/shared/ontology/data-type';

describe('VariableValidationService', () => {
    let variableValidationService: VariableValidationService;

    const OUT_OF_RANGE: VariableValidationStatusType = { isInRange: false }
    const IN_RANGE: VariableValidationStatusType = { isInRange: true }
    const INVALID: VariableValidationStatusType = { isValid: false }
    const VALID: VariableValidationStatusType = { isValid: true }

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [BmsjHipsterTestModule],
            providers: [VariableValidationService]
        })
        // TODO angular 9, TestBed.inject()
        variableValidationService = TestBed.get(VariableValidationService);
    });

    it('should validate min and max in expected range', () => {
        expect(variableValidationService.isValidValue(10, <VariableDetails>({ expectedRange: { max: 9 } }))).toEqual(jasmine.objectContaining(OUT_OF_RANGE));
        expect(variableValidationService.isValidValue(8, <VariableDetails>({ expectedRange: { max: 9 } }))).toEqual(jasmine.objectContaining(IN_RANGE));
        expect(variableValidationService.isValidValue(10, <VariableDetails>({ expectedRange: { min: 9 } }))).toEqual(jasmine.objectContaining(IN_RANGE));
        expect(variableValidationService.isValidValue(8, <VariableDetails>({ expectedRange: { min: 9 } }))).toEqual(jasmine.objectContaining(OUT_OF_RANGE));
    });

    it('should validate min-max ranges in expected range', () => {
        expect(variableValidationService.isValidValue(10, <VariableDetails>({ expectedRange: { max: 9, min: 5 } }))).toEqual(jasmine.objectContaining(OUT_OF_RANGE));
        expect(variableValidationService.isValidValue(6, <VariableDetails>({ expectedRange: { max: 9, min: 5 } }))).toEqual(jasmine.objectContaining(IN_RANGE));
        expect(variableValidationService.isValidValue(5, <VariableDetails>({ expectedRange: { max: 9, min: 5 } }))).toEqual(jasmine.objectContaining(IN_RANGE));
        expect(variableValidationService.isValidValue(9, <VariableDetails>({ expectedRange: { max: 9, min: 5 } }))).toEqual(jasmine.objectContaining(IN_RANGE));
        expect(variableValidationService.isValidValue(4, <VariableDetails>({ expectedRange: { max: 9, min: 5 } }))).toEqual(jasmine.objectContaining(OUT_OF_RANGE));
    });

    it('should validate min and max scale valid values', () => {
        expect(variableValidationService.isValidValue(10, <VariableDetails>({ scale: { validValues: { max: 9 } } }))).toEqual(jasmine.objectContaining(OUT_OF_RANGE));
        expect(variableValidationService.isValidValue(8, <VariableDetails>({ scale: { validValues: { max: 9 } } }))).toEqual(jasmine.objectContaining(IN_RANGE));
        expect(variableValidationService.isValidValue(10, <VariableDetails>({ scale: { validValues: { min: 9 } } }))).toEqual(jasmine.objectContaining(IN_RANGE));
        expect(variableValidationService.isValidValue(8, <VariableDetails>({ scale: { validValues: { min: 9 } } }))).toEqual(jasmine.objectContaining(OUT_OF_RANGE));
    });

    it('should validate min-max ranges in scale valid values', () => {
        expect(variableValidationService.isValidValue(10, <VariableDetails>({ scale: { validValues: { max: 9, min: 5 } } }))).toEqual(jasmine.objectContaining(OUT_OF_RANGE));
        expect(variableValidationService.isValidValue(6, <VariableDetails>({ scale: { validValues: { max: 9, min: 5 } } }))).toEqual(jasmine.objectContaining(IN_RANGE));
        expect(variableValidationService.isValidValue(5, <VariableDetails>({ scale: { validValues: { max: 9, min: 5 } } }))).toEqual(jasmine.objectContaining(IN_RANGE));
        expect(variableValidationService.isValidValue(9, <VariableDetails>({ scale: { validValues: { max: 9, min: 5 } } }))).toEqual(jasmine.objectContaining(IN_RANGE));
        expect(variableValidationService.isValidValue(4, <VariableDetails>({ scale: { validValues: { max: 9, min: 5 } } }))).toEqual(jasmine.objectContaining(OUT_OF_RANGE));
    });

    it('should validate categories', () => {
        const categories: Category[] = [{ name: '3' }, { name: '4' }, { name: '5' }]
        expect(variableValidationService.isValidValue('2', <VariableDetails>({ scale: { validValues: { categories } } }))).toEqual(jasmine.objectContaining(INVALID));
        expect(variableValidationService.isValidValue('3', <VariableDetails>({ scale: { validValues: { categories } } }))).toEqual(jasmine.objectContaining(VALID));
        expect(variableValidationService.isValidValue('5', <VariableDetails>({ scale: { validValues: { categories } } }))).toEqual(jasmine.objectContaining(VALID));
        expect(variableValidationService.isValidValue('6', <VariableDetails>({ scale: { validValues: { categories } } }))).toEqual(jasmine.objectContaining(INVALID));
    });

    it('should validate non numerical values for numeric types', () => {
        const inRange = variableValidationService.isValidValue(10, <VariableDetails>({ scale: { dataType: { name: DataType.NUMERIC } } }));
        expect(inRange).toEqual(jasmine.objectContaining(IN_RANGE));
        expect(inRange).toEqual(jasmine.objectContaining(VALID));
        const outOfRange = variableValidationService.isValidValue(10,
            <VariableDetails>({ expectedRange: { max: 9, min: 5 }, scale: { dataType: { name: DataType.NUMERIC } } }));
        expect(outOfRange).toEqual(jasmine.objectContaining(OUT_OF_RANGE))
        expect(outOfRange).toEqual(jasmine.objectContaining(VALID))
        expect(variableValidationService.isValidValue('a', <VariableDetails>({ scale: { dataType: { name: DataType.NUMERIC } } }))).toEqual(jasmine.objectContaining(INVALID));
        expect(variableValidationService.isValidValue('a', <VariableDetails>({ scale: { dataType: { name: DataType.CHARACTER } } }))).toEqual(jasmine.objectContaining(VALID));
    });
});
