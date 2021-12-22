import { Germplasm } from '../../../entities/germplasm/germplasm.model';

export class GermplasmMergeRequest {
    constructor(
        public mergeOptions?: MergeOptions,
        public nonSelectedGermplasm?: NonSelectedGermplasm[],
        public targetGermplasmId?: number
    ) {
    }
}

export class MergeOptions {
    constructor(
        public migrateAttributesData?: boolean,
        public migrateNameTypes?: boolean,
        public migratePassportData?: boolean,
        public migrateFiles?: boolean
    ) {
    }
}

export class NonSelectedGermplasm {
    constructor(
        public germplasm?: Germplasm,
        public germplasmId?: number,
        public migrateLots?: boolean,
        public omit?: boolean
    ) {
    }
}
