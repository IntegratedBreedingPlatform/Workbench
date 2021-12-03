import { LocationTypeEnum } from './location-type.enum';

export class LocationSearchRequest {
    constructor(
        public favoriteProgramUUID?: string,
        public locationTypeIds?: Array<LocationTypeEnum>,
        public locationIds?: Array<number>,
        public locationAbbreviations?: Array<string>,
        public locationTypeName?: string,
        public locationName?: string,
        public countryIds?: Array<number>,
        public provinceIds?: Array<number>
    ) {
    }
}
