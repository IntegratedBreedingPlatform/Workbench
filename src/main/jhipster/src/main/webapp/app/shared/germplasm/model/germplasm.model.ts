// TODO
//  - rename src/main/jhipster/src/main/webapp/app/entities/germplasm/germplasm.model.ts
//  - rename GermplasmDto > Germplasm
export class GermplasmDto {
    constructor(
        public gid?: number,
        public germplasmUUID?: string,
        public germplasmPUI?: string,
        public preferredName?: string,
        public creationDate?: string,
        public createdBy?: string,
        public createdByUserId?: number,
        public reference?: string,
        public breedingLocationId?: number,
        public breedingLocation?: string,
        public breedingMethodId?: number,
        public breedingMethod?: string,
        public isGroupedLine?: boolean,
        public groupId?: number,
        public gpid1?: number,
        public gpid2?: number,
        public pedigreeString?: string,
        public otherProgenitors?: number[],
        public names?: GermplasmName[],
        public attributes?: GermplasmAttribute[],
        public germplasmOrigin?: GermplasmOrigin,
        public externalReferences?: GermplasmExternalReference[]
    ) {
    }
}

export class GermplasmBasicDetailsDto {
    constructor(
        public creationDate?: string,
        public reference?: string,
        public breedingLocationId?: number
    ) {
    }
}

export class GermplasmName {
    constructor(
        public id?: number,
        public gid?: number,
        public name?: string,
        public date?: string,
        public locationId?: number,
        public locationName?: string,
        public nameTypeCode?: string,
        public nameTypeDescription?: string,
        public preferred?: boolean,
    ) {
    }

}

export class GermplasmAttribute {
    constructor(
        public id?: number,
        public value?: string,
        public variableId?: number,
        public variableName?: string,
        public variableDescription?: string,
        public date?: string,
        public locationId?: string,
        public locationName?: string,
        public hasFiles?: boolean
    ) {
    }
}

export class GermplasmList {
    constructor(
        public listId?: number,
        public listName?: string,
        public creationDate?: string,
        public description?: string,
        public programUUID?: string
    ) {
    }
}

export class GermplasmStudy {
    constructor(
        public studyId?: number,
        public name?: string,
        public description?: string,
        public programUUID?: string
    ) {
    }
}

export class GermplasmOrigin {
    constructor(
        public programUUID?: string,
        public studyId?: number,
        public studyName?: string,
        public observationUnitId?: string,
        public plotNumber?: number,
        public repNumber?: number,
        public blockNumber?: number,
        public positionCoordinateX?: string,
        public positionCoordinateY?: string,
        public geoCoordinates?: any,
    ) {
    }
}

export class GermplasmProgenitorsDetails {
    constructor(
        public breedingMethodId?: number,
        public breedingMethodName?: string,
        public breedingMethodCode?: string,
        public breedingMethodType?: string,
        public femaleParent?: GermplasmDto,
        public maleParents?: GermplasmDto[],
        public groupSource?: GermplasmDto,
        public immediateSource?: GermplasmDto,
        public numberOfDerivativeProgeny?: number
    ) {
    }
}

export class GermplasmExternalReference {
    constructor(
        public referenceID?: string,
        public referenceSource?: string
    ) {
    }
}
