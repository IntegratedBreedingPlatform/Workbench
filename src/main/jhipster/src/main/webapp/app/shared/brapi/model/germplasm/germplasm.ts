export class Donor {
    constructor(
        public donorInstituteCode: string,
        public germplasmPUI: string) {
    }

}

export class ExternalReference {
    constructor(
        public referenceID: string,
        public referenceSource: string) {
    }
}

export class Geometry {
    constructor(
        public coordinates: number[],
        public type: string) {
    }
}

export class Coordinates {
    constructor(
        public geometry: Geometry,
        public type: string) {
    }

}

export class GermplasmOrigin {
    constructor(
        public coordinateUncertainty: string,
        public coordinates: Coordinates) {
    }
}

export class StorageType {
    constructor(
        public code: string,
        public description: string) {
    }
}

export class Synonym {
    constructor(
        public synonym: string,
        public type: string) {
    }
}

export class TaxonId {
    constructor(
        public sourceName: string,
        public taxonId: string) {
    }
}

export class Germplasm {
    constructor(
        public accessionNumber: string,
        public acquisitionDate: string,
        public additionalInfo: any[],
        public biologicalStatusOfAccessionCode: string,
        public biologicalStatusOfAccessionDescription: string,
        public breedingMethodDbId: string,
        public collection: string,
        public commonCropName: string,
        public countryOfOriginCode: string,
        public defaultDisplayName: string,
        public documentationURL: string,
        public donors: Donor[],
        public externalReferences: ExternalReference[],
        public genus: string,
        public germplasmDbId: string,
        public germplasmName: string,
        public germplasmOrigin: GermplasmOrigin[],
        public germplasmPUI: string,
        public germplasmPreprocessing: string,
        public instituteCode: string,
        public instituteName: string,
        public pedigree: string,
        public seedSource: string,
        public seedSourceDescription: string,
        public species: string,
        public speciesAuthority: string,
        public storageTypes: StorageType[],
        public subtaxa: string,
        public subtaxaAuthority: string,
        public synonyms: Synonym[],
        public taxonIds: TaxonId[]) {
    }
}
