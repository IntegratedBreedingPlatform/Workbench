export class GermplasmImportRequest {
    constructor(
        public clientId?: number,
        public germplasmUUID?: string,
        public locationAbbr?: string,
        public breedingMethodAbbr?: string,
        public reference?: string,
        public preferredName?: string,
        public names?: any,
        public attributes?: any,
        public creationDate?: string,
    ) {
    }
}

export class ExtendedGermplasmImportRequest extends GermplasmImportRequest {
    constructor(
        public amount?: number,
        public stockId?: string,
        public storageLocationAbbr?: string,
        public unit?: string,
    ) {
        super();
    }
}
