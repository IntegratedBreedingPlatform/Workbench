export class SearchTypeComposite {
    constructor(
        public searchRequestId?: number,
        public searchType?: string
    ) {}
}

export enum SearchType {
    MANAGE_STUDY = 'ManageStudy',
    GERMPLASM_SEARCH = 'GermplasmSearch'
}
