// TODO rename to GermplasmSearchResponse
//  for a simplified entity see shared/germplasm/model/germplasm.model.ts
export class Germplasm {
    constructor(
        public gid: number,
        public germplasmUUID: string,
        public groupId?: number,
        public names?: number,
        public methodName?: number,
        public locationName?: string,
        public availableBalance?: string,
        public unit?: string,
        public lotCount?: number,
        public germplasmDate?: string,
        public methodCode?: string,
        public methodNumber?: string,
        public methodGroup?: string,
        public germplasmPreferredName?: string,
        public germplasmPreferredId?: string,
        public groupSourceGID?: string,
        public groupSourcePreferredName?: string,
        public immediateSourceGID?: string,
        public immediateSourceName?: string,
        public femaleParentGID?: string,
        public femaleParentPreferredName?: string,
        public maleParentGID?: string,
        public maleParentPreferredName?: string,
        public pedigreeString?: string,
        public attributeTypesValueMap?: any,
        public nameTypesValueMap?: any,
        public locationId?: number,
        public breedingMethodId?: number,
        public hasProgeny?: boolean,
        public usedInLockedStudy?: boolean,
        public usedInLockedList?: boolean
    ) {
    }
}
