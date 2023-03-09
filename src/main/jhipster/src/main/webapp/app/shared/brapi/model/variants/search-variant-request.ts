export class SearchVariantRequest {
    constructor(
        public callSetDbIds?: Array<string>,
        public studyDbIds?: Array<string>,
        public studyNames?: Array<string>,
        public variantDbIds?: Array<string>,
        public variantSetDbIds?: Array<string>,
        public pageSize?: number,
        public pageToken?: string
    ) {
    }
}
