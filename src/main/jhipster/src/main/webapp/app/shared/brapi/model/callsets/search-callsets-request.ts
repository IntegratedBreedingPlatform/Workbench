export class SearchCallsetsRequest {
    constructor(
        public callSetDbIds?: Array<string>,
        public callSetNames?: Array<string>,
        public germplasmDbIds?: Array<string>,
        public germplasmNames?: Array<string>,
        public sampleDbIds?: Array<string>,
        public sampleNames?: Array<string>,
        public variantSetDbIds?: Array<string>,
        public page?: number,
        public pageSize?: number
    ) {
    }
}
