import { ObservationVariable } from '../model/observation-variable.model';
import { VariableDetails } from './model/variable-details';
import { ScaleDetails } from './model/scale-details';
import { ValidValues } from './model/valid-values';
import { Category } from './model/category';
import { ExpectedRange } from './model/expected-range';

// TODO complete mapping
export function convertToVariableDetails(variable: ObservationVariable): VariableDetails {
    return <VariableDetails>({
        scale: <ScaleDetails>({
            validValues: <ValidValues>({
                categories: variable.possibleValues && variable.possibleValues.length
                    ? variable.possibleValues.map((v) => <Category>({ name: v.name, id: String(v.id), description: v.description }))
                    : [],
                min: variable.scaleMinRange,
                max: variable.scaleMaxRange
            })
        }),
        expectedRange: <ExpectedRange>({
            min: variable.variableMinRange,
            max: variable.variableMaxRange
        })
    });
}
