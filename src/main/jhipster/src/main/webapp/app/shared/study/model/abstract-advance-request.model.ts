export abstract class AbstractAdvanceRequest {
    protected constructor(
        public instanceIds: number[],
        public selectedReplications: number[],
        public selectionTraitRequest?: SelectionTraitRequest,
        public propagateDescriptors?: boolean,
        public descriptorIds?: number[],
        public overrideDescriptorsLocation?: boolean,
        public locationOverrideId?: number
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
