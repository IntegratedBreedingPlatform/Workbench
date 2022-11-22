export class SearchCallsRequest {
    constructor(
        public callSetDbIds?: Array<string>,
        public expandHomozygotes?: boolean,
        public sepPhased?: string,
        public sepUnphased?: string,
        public unknownString?: string,
        public variantDbIds?: Array<string>,
        public variantSetDbIds?: Array<string>,
        public pageSize?: number,
        public page?: number
    ) {
    }
}
