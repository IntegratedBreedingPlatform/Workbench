import { Injectable } from '@angular/core';
import { ObservationVariable, ValueReference } from '../../model/observation-variable.model';
import { DataTypeEnum } from '../../ontology/data-type.enum';
import { ObservationUnitData } from './observation-unit-data.model';

@Injectable()
export class ObservationVariableHelperService {

    constructor() {
    }

    getVariableDisplayName(variable: ObservationVariable): string {
        return variable.alias ? variable.alias : variable.name;
    }

    getVariableValueFromVariable(variable: ObservationVariable) {
        if (variable.dataType === DataTypeEnum.CATEGORICAL && variable.possibleValues && variable.possibleValues.length > 0) {
            return variable.possibleValues.filter((valueReference: ValueReference) => String(valueReference.id) === variable.value)
                .map((value: ValueReference) => value.description);
        }
        return variable.value;
    }

    getVariableValueFromUnitData(variable: ObservationVariable, observationUnitData: ObservationUnitData) {
        if (variable.dataType === DataTypeEnum.CATEGORICAL && variable.possibleValues && variable.possibleValues.length > 0) {
            return this.getCategoricalValue(variable, observationUnitData.value);
        }
        return observationUnitData ? observationUnitData.value : null;
    }

    private getCategoricalValue(variable: ObservationVariable, categoricalValue: string) {
        return variable.possibleValues.filter((valueReference: ValueReference) =>
                String(valueReference.id) === categoricalValue || String(valueReference.name) === categoricalValue)
            .map((value: ValueReference) => value.description);
    }

}
