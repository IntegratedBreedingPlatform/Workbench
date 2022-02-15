import { SearchComposite } from '../../model/search-composite';

export class ListModel {
    constructor(
        public listId?: number,
        public listName?: string,
        public description?: string,
        public listType?: string,
        public creationDate?: any,
        public notes?: string,
        public parentFolderId?: string,
        public entries?: ListEntry[],
        public searchComposite?: SearchComposite<any, any>
    ) {
    }
}

export class ListEntry {
    // for lists with duplicated entries
    private _internal_id = Math.random();

    constructor(
        public entryNo?: number,
    ) {
    }

    get internal_id() {
        return this._internal_id;
    }
}
