import { ExternalReference } from '../germplasm/germplasm';

export class Sample {
    constructor(
        public additionalInfo?: any[],
        public column?: number,
        public externalReferences?: ExternalReference[],
        public germplasmDbId?: string,
        public observationUnitDbId?: string,
        public plateDbId?: string,
        public plateName?: string,
        public programDbId?: string,
        public row?: string,
        public sampleBarcode?: string,
        public sampleDbId?: string,
        public sampleDescription?: string,
        public sampleGroupDbId?: string,
        public sampleName?: string,
        public samplePUI?: string,
        public sampleTimestamp?: Date,
        public sampleType?: string,
        public studyDbId?: string,
        public takenBy?: string,
        public tissueType?: string,
        public trialDbId?: string,
        public well?: string
    ) {
    }
}
