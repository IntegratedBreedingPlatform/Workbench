import { AbstractAdvanceRequest, SelectionTraitRequest } from './abstract-advance-request.model';

export class AdvanceSamplesRequest extends AbstractAdvanceRequest {

    constructor(
        public instanceIds: number[],
        public selectedReplications: string[],
        public breedingMethodId: number,
        public selectionTraitRequest?: SelectionTraitRequest
    ) {
        super(instanceIds, selectedReplications, selectionTraitRequest);
    }
}
