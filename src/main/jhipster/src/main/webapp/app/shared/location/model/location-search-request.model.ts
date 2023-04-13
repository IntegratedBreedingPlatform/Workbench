import { LocationTypeEnum } from './location-type.enum';

export class LocationSearchRequest {
    constructor(
        public favoriteProgramUUID?: string,
        public filterFavoriteProgramUUID?: boolean,
        public locationTypeIds?: Array<LocationTypeEnum>,
        public locationDbIds?: Array<number>,
        public abbreviations?: Array<string>,
        public locationTypes?: Array<string>,
        public locationNameFilter?: any,
        public countryIds?: Array<number>,
        public provinceIds?: Array<number>,
        public countryName?: string,
        public provinceName?: string
    ) {
    }
}
