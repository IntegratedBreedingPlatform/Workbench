export class AdvanceStudyRequest {
    constructor(
        public instanceIds: number[],
        public selectedReplications: string[],
        public breedingMethodSelectionRequest: BreedingMethodSelectionRequest,
        public selectionTraitRequest?: SelectionTraitRequest,
        public lineSelectionRequest?: LineSelectionRequest,
        public bulkingRequest?: BulkingRequest,
    ) {
    }
}

export class BreedingMethodSelectionRequest {
    constructor(
        public breedingMethodId?: number,
        public methodVariateId?: number
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
