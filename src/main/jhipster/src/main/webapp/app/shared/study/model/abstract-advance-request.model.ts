export abstract class AbstractAdvanceRequest {
    protected constructor(
        public instanceIds: number[],
        public selectedReplications: number[],
        public selectionTraitRequest?: SelectionTraitRequest,
        public excludedAdvancedRows?: number[],
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
