import { GermplasmAudit } from '../germplasm-audit.model';

export class GermplasmReferenceAudit extends GermplasmAudit {
    constructor(
        public revisionType: string,
        public value: string,
        public createdBy: string,
        public createdDate: number,
        public modifiedBy: string,
        public modifiedDate: number,
        public valueChanged: boolean
    ) {
        super(revisionType, createdBy, createdDate, modifiedBy, modifiedDate);
    }

}
