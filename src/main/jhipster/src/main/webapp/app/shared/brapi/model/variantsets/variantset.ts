export class VariantSet {
    constructor(
        public additionalInfo: any,
        public analysis: Analysis[],
        public availableFormats: AvailableFormat[],
        public callSetCount: number,
        public referenceSetDbId: string,
        public studyDbId: string,
        public variantCount: number,
        public variantSetDbId: string,
        public variantSetName: string) {
    }
}

export class Analysis {
    constructor(
        public analysisDbId: string,
        public analysisName: string,
        public created: Date,
        public description: string,
        public software: string[],
        public type: string,
        public updated: Date) {
    }
}

export class AvailableFormat {
    constructor(
        public dataFormat: string,
        public fileFormat: string,
        public fileURL: string) {
    }

}
