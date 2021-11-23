export class GermplasmListDataSearchRequest {
    constructor(
        public entryNumbers?: Array<number>,
        public gids?: Array<number>,
        public germplasmUUID?: string,
        public groupId?: string,
        public designationFilter?: any,
        public immediateSourceName?: any,
        public groupSourceName?: any,
        public femaleParentName?: any,
        public maleParentName?: any,
        public breedingMethodName?: string,
        public breedingMethodAbbreviation?: string,
        public breedingMethodGroup?: string,
        public locationName?: string,
        public locationAbbreviation?: string,
        public germplasmDateFrom?: number,
        public germplasmDateTo?: number,
        public reference?: string,
        public namesFilters?: any,
        public descriptorsFilters?: any,
        public variablesFilters?: any
    ) {
    }
}
