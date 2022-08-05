export class SearchSamplesRequest {
    constructor(
        public commonCropNames?: Array<string>,
        public externalReferenceIDs?: Array<string>,
        public externalReferenceIds?: Array<string>,
        public externalReferenceSources?: Array<string>,
        public germplasmDbIds?: Array<string>,
        public germplasmNames?: Array<string>,
        public observationUnitDbIds?: Array<string>,
        public page?: number,
        public pageSize?: number,
        public plateDbIds?: Array<string>,
        public plateNames?: Array<string>,
        public programDbIds?: Array<string>,
        public programNames?: Array<string>,
        public sampleDbIds?: Array<string>,
        public sampleGroupDbIds?: Array<string>,
        public sampleNames?: Array<string>,
        public studyDbIds?: Array<string>,
        public studyNames?: Array<string>,
        public trialDbIds?: Array<string>,
        public trialNames?: Array<string>
    ) {
    }
}
