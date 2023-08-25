import { AbstractAdvanceRequest, SelectionTraitRequest } from './abstract-advance-request.model';

export class AdvanceSamplesRequest extends AbstractAdvanceRequest {

    constructor(
        public instanceIds: number[],
        public selectedReplications: number[],
        public breedingMethodId: number,
        public selectionTraitRequest?: SelectionTraitRequest,
        public propagateDescriptors?: boolean,
        public descriptorIds?: number[],
        public overrideDescriptorsLocation?: boolean,
        public locationOverrideId?: number
    ) {
        super(instanceIds, selectedReplications, selectionTraitRequest);
    }
}
