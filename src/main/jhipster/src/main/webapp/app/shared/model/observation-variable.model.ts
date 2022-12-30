import { DataTypeEnum, DataTypeIdEnum } from '../ontology/data-type.enum';

export class ObservationVariable {
    constructor(public termId: number,
                public name: string,
                public alias: string,
                public description?: string,
                public dataTypeId?: DataTypeIdEnum,
                public dataType?: DataTypeEnum,
                public possibleValues?: ValueReference[],
                public scaleMinRange?: number,
                public scaleMaxRange?: number,
                public variableMinRange?: number,
                public variableMaxRange?: number,
                public value?: string
                // TODO: added missing properties required for entry details
                ) {
    }
}

export class ValueReference {
    constructor(
        public id: number,
        public name: string,
        public description: string,
    ) {
    }
}
