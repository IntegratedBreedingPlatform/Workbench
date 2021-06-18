export class GermplasmAudit {
    constructor(public revisionType: string,
                public createdBy: string,
                public createdDate: number,
                public modifiedBy: string,
                public modifiedDate: number) {
    }

}
