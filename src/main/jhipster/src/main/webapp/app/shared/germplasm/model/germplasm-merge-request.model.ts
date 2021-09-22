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
        public migratePassportData?: boolean
    ) {
    }
}

export class NonSelectedGermplasm {
    constructor(
        public closeLots?: boolean,
        public germplasmId?: number,
        public migrateLots?: boolean,
        public omit?: boolean
    ) {
    }
}
