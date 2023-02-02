import { ObservationVariable } from '../../model/observation-variable.model';

export class TreatmentVariable {
    constructor(public levelVariable: ObservationVariable,
                public valueVariable: ObservationVariable,
                public values: string[]) {
    }
}
