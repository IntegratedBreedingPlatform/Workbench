export class GermplasmNameChange {
    constructor(
        public revisionType: string,
        public nameType: string,
        public value: string,
        public creationDate: string, // TODO: check should be date?
        public locationName: string,
        public createdBy: string,
        public createdDate: string, // TODO: check should be date?
        public modifiedBy: string,
        public modifiedDate: string, // TODO: check should be date?
        public preferred: boolean // TODO: check should be date?
    ) {
    }
}
