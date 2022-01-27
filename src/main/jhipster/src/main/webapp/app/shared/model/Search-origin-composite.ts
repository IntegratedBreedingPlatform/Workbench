export class SearchOriginComposite {
    constructor(
        public searchRequestId?: number,
        public searchOrigin?: SearchOrigin
    ) {}
}

export enum SearchOrigin {
    MANAGE_STUDY_SOURCE = 'MANAGE_STUDY_SOURCE',
    GERMPLASM_SEARCH = 'GERMPLASM_SEARCH',
    MANAGE_STUDY_PLOT = 'MANAGE_STUDY_PLOT'
}
