export class GermplasmNameChange {
    constructor(
        public revisionType: string,
        public nameType: string,
        public value: string,
        public creationDate: string,
        public locationName: string,
        public createdBy: string,
        public createdDate: number,
        public modifiedBy: string,
        public modifiedDate: number,
        public preferred: boolean,
        public nameTypeChanged: boolean,
        public locationChanged: boolean,
        public creationDateChanged: boolean,
        public valueChanged: boolean,
        public preferredChanged: boolean
) {
    }
}