// TODO
//  - rename src/main/jhipster/src/main/webapp/app/entities/germplasm/germplasm.model.ts
//  - rename GermplasmDto > Germplasm
export class GermplasmDto {
    constructor(
        public gid?: number,
        public germplasmUUID?: string,
        public preferredName?: string,
        public creationDate?: string,
        public reference?: string,
        public breedingLocationId?: number,
        public breedingLocation?: string,
        public breedingMethodId?: number,
        public breedingMethod?: string,
        public isGroupedLine?: boolean,
        public groupId?: number,
        public names?: GermplasmName[],
        public attributes?: GermplasmAttribute[],
    ) {
    }
}

export class GermplasmName {
    constructor(
        public gid?: number,
        public name?: string,
        public date?: string,
        public locationId?: number,
        public locationName?: string,
        public nameTypeCode?: string,
        public nameTypeDescription?: string,
    ) {
    }

}

export class GermplasmAttribute {
    constructor(
        public value?: string,
        public attributeCode?: string,
        public attributeDescription?: string,
        public date?: string,
        public locationId?: string,
        public locationName?: string,
    ) {
    }
}

export class GermplasmList {
    constructor(
        public listId?: number,
        public listName?: string,
        public creationDate?: string,
        public description?: string
    ) {
    }
}

export class GermplasmStudy {
    constructor(
        public studyId?: number,
        public name?: string,
        public description?: string
    ) {
    }
}
