import { SearchComposite } from '../../model/search-composite';

export class ListModel {
    constructor(
        public id?: number,
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
