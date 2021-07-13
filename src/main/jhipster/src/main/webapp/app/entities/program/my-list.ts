export class MyList {
    constructor(
        public listId?: number,
        public name?: string,
        public type?: string,
        public typeName?: string,
        public date?: string,
        public folder?: string,
        public selected?: boolean,
        // public metadata?: MyStudyMetadata
    ) {
    }
}
