export class CallSet {
    constructor(
        public additionalInfo: any,
        public callSetDbId: string,
        public callSetName: string,
        public created: Date,
        public sampleDbId: string,
        public studyDbId: string,
        public updated: Date,
        public variantSetDbIds: string[]) {
    }

}
