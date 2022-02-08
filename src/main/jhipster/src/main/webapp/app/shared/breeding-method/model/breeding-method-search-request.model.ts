import { BreedingMethodTypeEnum } from './breeding-method-type.model';

export class BreedingMethodSearchRequest {
    constructor(
        public favoriteProgramUUID?: string,
        public filterFavoriteProgramUUID?: boolean,
        public methodTypes?: BreedingMethodTypeEnum[],
        public methodIds?: number[],
        public methodAbbreviations?: string[],
        public nameFilter?: any,
        public description?: string,
        public groups?: string[],
        public methodDateFrom?: string,
        public methodDateTo?: string,
        public methodClassIds?: number[]
    ) {
    }
}
