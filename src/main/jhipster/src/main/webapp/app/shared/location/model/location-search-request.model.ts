import { LocationTypeEnum } from './location-type.enum';

export class LocationSearchRequest {
    constructor(
        public favoriteProgramUUID?: string,
        public filterFavoriteProgramUUID?: boolean,
        public locationTypeIds?: Array<LocationTypeEnum>,
        public locationIds?: Array<number>,
        public locationAbbreviations?: Array<string>,
        public locationTypeName?: string,
        public locationNameFilter?: any,
        public countryIds?: Array<number>,
        public provinceIds?: Array<number>,
        public countryName?: string,
        public provinceName?: string
    ) {
    }
}
