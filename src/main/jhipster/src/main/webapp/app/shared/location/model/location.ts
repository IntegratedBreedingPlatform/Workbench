import { ProgramFavorite } from '../../program/model/program-favorite.model';

export class Location {
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
        public programUUID?: string,
        public countryName?: string,
        public provinceName?: string,
        public locationTypeName?: string,
        public countryCode?: string,
        public programFavorites?: ProgramFavorite[],
    ) {
    }
}
