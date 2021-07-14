export class SearchOriginComposite {
    constructor(
        public searchRequestId?: number,
        public searchOrigin?: SearchOrigin
    ) {}
}

export enum SearchOrigin {
    MANAGE_STUDY = 'ManageStudy',
    GERMPLASM_SEARCH = 'GermplasmSearch'
}
