export class Call {
    constructor(
        public additionalInfo: any,
        public callSetDbId: string,
        public callSetName: string,
        public genotypeMetadata: any[],
        public genotypeValue: string,
        public phaseSet: string,
        public variantDbId: string,
        public variantName: string,
        public variantSetDbId: string,
        public variantSetName: string) {
    }
}
