export class LocationModel {
    constructor(
        public id?: number,
        public name?: string,
        public abbreviation?: string,
        public latitude?: number,
        public longitude?: number,
        public altitude?: number,
        public defaultLocation?: boolean
    ) {
    }
}

export enum LocationTypeEnum {
    COUNTRY = 405,
    PROVINCE = 406,
    BREEDING_LOCATION = 410
}
