export class SearchStudiesRequest {
    constructor(
        public active?: boolean,
        public commonCropNames?: Array<string>,
        public externalReferenceIDs?: Array<string>,
        public externalReferenceSources?: Array<string>,
        public germplasmDbIds?: Array<string>,
        public germplasmNames?: Array<string>,
        public locationDbIds?: Array<string>,
        public locationNames?: Array<string>,
        public observationVariableDbIds?: Array<string>,
        public observationVariableNames?: Array<string>,
        public programDbIds?: Array<string>,
        public programNames?: Array<string>,
        public seasonDbIds?: Array<string>,
        public sortBy?: string,
        public sortOrder?: string,
        public studyCodes?: Array<string>,
        public studyDbIds?: Array<string>,
        public studyNames?: Array<string>,
        public studyPUIs?: Array<string>,
        public studyTypes?: Array<string>,
        public trialDbIds?: Array<string>,
        public trialNames?: Array<string>,
        public page?: number,
        public pageSize?: number
    ) {
    }
}
