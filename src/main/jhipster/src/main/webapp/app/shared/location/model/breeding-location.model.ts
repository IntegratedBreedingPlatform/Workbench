export class BreedingLocationModel {
    constructor(
        public id?: number,
        public name?: string,
        public type?: number,
        public abbreviation?: string,
        public latitude?: number,
        public longitude?: number,
        public altitude?: number,
        public countryId?: number,
        public provinceId?: number,
        public programUUID?: string
    ) {
    }
}
