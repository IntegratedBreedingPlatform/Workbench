export class Germplasm {
    constructor(
        public gid: number,
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
        public germplasmPeferredName?: string,
        public germplasmPeferredId?: string,
        public groupSourceGID?: string,
        public groupSourcePreferredName?: string,
        public immediateSourceGID?: string,
        public immediateSourcePreferredName?: string,
        public femaleParentGID?: string,
        public femaleParentPreferredName?: string,
        public maleParentGID?: string,
        public maleParentPreferredName?: string,
        public pedigreeString?: string,
        public attributeTypesValueMap?: any,
        public nameTypesValueMap?: any
    ) {
    }
}
