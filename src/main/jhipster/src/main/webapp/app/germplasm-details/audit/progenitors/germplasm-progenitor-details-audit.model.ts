import { GermplasmAudit } from '../germplasm-audit.model';

export class GermplasmProgenitorDetailsAudit extends GermplasmAudit {
    constructor(
        public revisionType: string,
        public createdBy: string,
        public createdDate: number,
        public modifiedBy: string,
        public modifiedDate: number,
        public breedingMethodName: string,
        public breedingMethodType: string,
        public femaleParent: number,
        public maleParent: number,
        public progenitorsNumber: number,
        public breedingMethodChanged: boolean,
        public femaleParentChanged: boolean,
        public maleParentChanged: boolean,
        public progenitorsNumberChanged: boolean
    ) {
        super(revisionType, createdBy, createdDate, modifiedBy, modifiedDate);
    }

}
