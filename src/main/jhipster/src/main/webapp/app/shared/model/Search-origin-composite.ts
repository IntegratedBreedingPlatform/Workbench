export class SearchOriginComposite {
    constructor(
        public searchRequestId?: number,
        public searchOrigin?: SearchOrigin
    ) {}
}

export enum SearchOrigin {
    MANAGE_STUDY = 'MANAGE_STUDY',
    GERMPLASM_SEARCH = 'GERMPLASM_SEARCH'
}
