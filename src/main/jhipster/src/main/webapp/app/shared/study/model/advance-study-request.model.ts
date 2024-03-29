import { AbstractAdvanceRequest, SelectionTraitRequest } from './abstract-advance-request.model';

export class AdvanceStudyRequest extends AbstractAdvanceRequest {
    constructor(
        public datasetId: number,
        public instanceIds: number[],
        public selectedReplications: number[],
        public breedingMethodSelectionRequest: BreedingMethodSelectionRequest,
        public selectionTraitRequest?: SelectionTraitRequest,
        public lineSelectionRequest?: LineSelectionRequest,
        public bulkingRequest?: BulkingRequest,
        public propagateDescriptors?: boolean,
        public descriptorIds?: number[],
        public overrideDescriptorsLocation?: boolean,
        public locationOverrideId?: number
    ) {
        super(instanceIds, selectedReplications, selectionTraitRequest);
    }
}

export class BreedingMethodSelectionRequest {
    constructor(
        public breedingMethodId?: number,
        public methodVariateId?: number
    ) {
    }
}

export class LineSelectionRequest {
    constructor(
        public linesSelected?: number,
        public lineVariateId?: number
    ) {
    }
}

export class BulkingRequest {
    constructor(
        public allPlotsSelected?: boolean,
        public plotVariateId?: number
    ) {
    }
}
