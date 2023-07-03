import { AbstractAdvanceRequest, SelectionTraitRequest } from './abstract-advance-request.model';

export class AdvanceSamplesRequest extends AbstractAdvanceRequest {

    constructor(
        public instanceIds: number[],
        public selectedReplications: number[],
        public breedingMethodId: number,
        public selectionTraitRequest?: SelectionTraitRequest,
        public propagateAttributesData?: boolean,
        public propagatePassportDescriptorData?: boolean
    ) {
        super(instanceIds, selectedReplications, selectionTraitRequest);
    }
}
