import { SearchComposite } from './search-composite';
import { GermplasmSearchRequest } from '../../entities/germplasm/germplasm-search-request.model';

export class GermplasmList {
    constructor(
        public name?: string,
        public description?: string,
        public type?: string,
        public date?: any,
        public notes?: string,
        public parentFolderId?: string,
        public entries?: GermplasmListEntry[],
        public searchComposite?: SearchComposite<GermplasmSearchRequest, number>
    ) {
    }
}

export class GermplasmListEntry {
    constructor(
        public entryNo?: number,
        public gid?: number,
        public entryCode?: string,
        public seedSource?: string,
        public designation?: string,
        public groupName?: string,
    ) {

    }
}
