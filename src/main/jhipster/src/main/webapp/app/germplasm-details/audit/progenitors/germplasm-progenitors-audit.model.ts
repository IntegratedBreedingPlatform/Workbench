import { GermplasmAudit } from '../germplasm-audit.model';

export class GermplasmProgenitorAudit extends GermplasmAudit {
    constructor(
        public revisionType: string,
        public createdBy: string,
        public createdDate: number,
        public modifiedBy: string,
        public modifiedDate: number,
        public progenitorGid: number
    ) {
        super(revisionType, createdBy, createdDate, modifiedBy, modifiedDate);
    }

}
