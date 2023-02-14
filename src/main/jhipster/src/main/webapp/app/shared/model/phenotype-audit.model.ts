export class PhenotypeAudit {

    constructor(
        public phenotypeId?: number,
        public value?: string,
        public draftValue?: string,
        public updatedBy?: string,
        public updatedByUserId?: number,
        public updatedDate?: string,
        public revisionType?: string,
        public valueChanged?: boolean,
        public draftValueChanged?: boolean
    ) {
    }
}
