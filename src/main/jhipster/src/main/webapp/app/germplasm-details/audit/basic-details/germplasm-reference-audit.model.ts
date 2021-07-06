import { GermplasmAudit } from '../germplasm-audit.model';

export class GermplasmReferenceAudit extends GermplasmAudit {
    constructor(
        public revisionType: string,
        public createdBy: string,
        public createdDate: number,
        public modifiedBy: string,
        public modifiedDate: number,
        public value: string,
        public valueChanged: boolean
    ) {
        super(revisionType, createdBy, createdDate, modifiedBy, modifiedDate);
    }

}
