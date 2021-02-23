import { SearchComposite } from '../../model/search-composite';

export class ListModel {
    constructor(
        public name?: string,
        public description?: string,
        public type?: string,
        public date?: any,
        public notes?: string,
        public parentFolderId?: string,
        public entries?: ListEntry[],
        public searchComposite?: SearchComposite<any, any>
    ) {
    }
}

export class ListEntry {

    constructor(
        public entryNo?: number,
    ) {
    }
}
