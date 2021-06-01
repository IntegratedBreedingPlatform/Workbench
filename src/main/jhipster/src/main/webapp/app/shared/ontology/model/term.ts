export class Term {
    constructor(
        public id?: number,
        public vocabularyId?: number,
        public name?: string,
        public definition?: string,
        public obsolete?: boolean,
        public dateCreated?: any,
        public dateLastModified?: any,
    ) {
    }
}
