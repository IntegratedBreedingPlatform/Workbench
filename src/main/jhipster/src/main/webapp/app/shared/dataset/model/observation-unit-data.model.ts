export class ObservationUnitData {
    constructor(public observationId?: number,
                public categoricalValueId?: number,
                public value?: string,
                public variableId?: number,
                public draftCategoricalValueId?: number,
                public draftValue?: string) {
    }
}
