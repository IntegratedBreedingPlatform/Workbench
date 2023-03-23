export class AdvancedGermplasmPreview {
    constructor(
        public uniqueId: string,
        public trialInstance: string,
        public locationName: string,
        public entryNumber: string,
        public plotNumber: string,
        public plantNumber: string,
        public cross: string,
        public immediateSource: string,
        public breedingMethodAbbr: string,
        public designation: string,
        public deleted: boolean
    ) {
    }
}
