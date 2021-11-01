import { SearchComposite } from '../../model/search-composite';
import { GermplasmSearchRequest } from '../../../entities/germplasm/germplasm-search-request.model';
import { ListEntry, ListModel } from '../../list-builder/model/list.model';

export class GermplasmList extends ListModel {
    constructor(
        public searchComposite?: SearchComposite<GermplasmSearchRequest, number>
    ) {
        super();
    }
}

export class GermplasmListEntry extends ListEntry {
    constructor(
        public gid?: number,
        public entryCode?: string,
        public seedSource?: string,
        public groupName?: string,
        // {variableId: GermplasmListObservationDto}
        public data?: {[key: number]: any}
    ) {
        super();
    }
}
