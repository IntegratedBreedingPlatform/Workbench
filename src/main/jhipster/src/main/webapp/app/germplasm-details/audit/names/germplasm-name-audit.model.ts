import { GermplasmAudit } from '../germplasm-audit.model';

export class GermplasmNameAudit extends GermplasmAudit {
    constructor(
        public revisionType: string,
        public createdBy: string,
        public createdDate: number,
        public modifiedBy: string,
        public modifiedDate: number,
        public nameType: string,
        public value: string,
        public creationDate: string,
        public locationName: string,
        public preferred: boolean,
        public nameTypeChanged: boolean,
        public locationChanged: boolean,
        public creationDateChanged: boolean,
        public valueChanged: boolean,
        public preferredChanged: boolean
    ) {
        super(revisionType, createdBy, createdDate, modifiedBy, modifiedDate);
    }
}
