export class GermplasmImportRequest {
    constructor(
        public germplasmList: GermplasmImportPayload,
        public connectUsing: PedigreeConnectionType
    ) {
    }
}

export class GermplasmImportPayload {
    constructor(
        public clientId?: number,
        public germplasmPUI?: string,
        public locationAbbr?: string,
        public breedingMethodAbbr?: string,
        public reference?: string,
        public preferredName?: string,
        public names?: any,
        public attributes?: any,
        public creationDate?: string,
        public progenitor1?: string,
        public progenitor2?: string
    ) {
    }
}

export class GermplasmImportValidationPayload extends GermplasmImportPayload {
    constructor(
        public amount?: number,
        public stockId?: string,
        public storageLocationAbbr?: string,
        public unit?: string,
    ) {
        super();
    }
}

export enum PedigreeConnectionType {
    NONE = 'NONE',
    GID = 'GID',
    GUID = 'GUID'
}
