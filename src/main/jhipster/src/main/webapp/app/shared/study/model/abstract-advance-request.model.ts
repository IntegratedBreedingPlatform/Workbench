export abstract class AbstractAdvanceRequest {
    protected constructor(
        public instanceIds: number[],
        public selectedReplications: string[],
        public selectionTraitRequest?: SelectionTraitRequest
    ) {
    }
}

export class SelectionTraitRequest {
    constructor(
        public datasetId: number,
        public variableId: number
    ) {
    }
}
